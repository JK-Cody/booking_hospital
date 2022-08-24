package com.hospital.client.order.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.client.order.mapper.ClientOrderMapper;
import com.hospital.client.order.service.ClientOrderService;
import com.hospital.client.order.service.WechatPaymentService;
import com.hospital.commom.rabbit.RabbitService;
import com.hospital.commom.rabbit.config.RabbitConstant;
import com.hospital.common.Config.helper.HttpRequestHelper;
import com.hospital.common.exception.bookException;
import com.hospital.common.result.ResultCodeEnum;
import com.hospital.feign.client.hospital.HospitalFeignClient;
import com.hospital.feign.client.login.LoginFeignClient;
import enums.OrderStatusEnum;
import model.order.OrderInfo;
import model.user.Patient;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vo.hosp.ScheduleOrderVo;
import vo.msm.MsmVo;
import vo.order.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ClientOrderServiceImpl extends ServiceImpl<ClientOrderMapper, OrderInfo> implements ClientOrderService {

    @Autowired
    HospitalFeignClient hospitalFeignClient;

    @Autowired
    LoginFeignClient loginFeignClient;

    @Autowired
    RabbitService rabbitService;

    @Autowired
    WechatPaymentService wechatPaymentService;

    @Override
    public Long saveOrder(String id, Long patientId) {   //返回订单编号

//获取就诊人信息
        Patient patient = loginFeignClient.getPatientById(patientId);
        if(null == patient) {
            throw new bookException(ResultCodeEnum.PARAM_ERROR);
        }

//获取排班信息
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrder(id);
        if(null == scheduleOrderVo) {
            throw new bookException(ResultCodeEnum.PARAM_ERROR);
        }
//关键信息判定===========================
        //判断排班时间是否未开始或已结束
        if(new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
                || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
            throw new bookException(ResultCodeEnum.TIME_NO);  //当前时间不可用
        }
        //判断排班时间是否有预约剩余
        if(scheduleOrderVo.getAvailableNumber() <= 0) {
            throw new bookException(ResultCodeEnum.NUMBER_NO);
        }
        //获取医院签名信息
        SignInfoVo hospitalSignInfo = hospitalFeignClient.getSignInfo(scheduleOrderVo.getHoscode());
        if(null == hospitalSignInfo) {
            throw new bookException(ResultCodeEnum.PARAM_ERROR);
        }

//复制scheduleOrderVo 到 订单对象(补充集合信息，以及方便前台获取参数)
        OrderInfo orderInfo = new OrderInfo();  //订单对象
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
        //订单对象添加补充信息
        String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        //将scheduleId数值更改为MongoDB的id(不被使用)
       //orderInfo.setScheduleId(id);
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        //保存到数据库 client_order_info
        baseMapper.insert(orderInfo);

//获取信息保存到大对象,用于发送到数据库的order_info
        //收集订单对象信息
        Map<String, Object> bigParamMap = new HashMap<>();
        bigParamMap.put("hoscode",orderInfo.getHoscode());
        bigParamMap.put("depcode",orderInfo.getDepcode());
        bigParamMap.put("scheduleId",orderInfo.getScheduleId());
        bigParamMap.put("reserveDate",new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        bigParamMap.put("reserveTime", orderInfo.getReserveTime());
        bigParamMap.put("amount",orderInfo.getAmount());
        //收集就诊人信息
        bigParamMap.put("name", patient.getName());
        bigParamMap.put("certificatesType",patient.getCertificatesType());
        bigParamMap.put("certificatesNo", patient.getCertificatesNo());
        bigParamMap.put("sex",patient.getSex());
        bigParamMap.put("birthdate", patient.getBirthdate());
        bigParamMap.put("phone",patient.getPhone());
        bigParamMap.put("isMarry", patient.getIsMarry());
        bigParamMap.put("provinceCode",patient.getProvinceCode());
        bigParamMap.put("cityCode", patient.getCityCode());
        bigParamMap.put("districtCode",patient.getDistrictCode());
        bigParamMap.put("address",patient.getAddress());
        bigParamMap.put("contactsName",patient.getContactsName());
        bigParamMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        bigParamMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
        bigParamMap.put("contactsPhone",patient.getContactsPhone());
        bigParamMap.put("timestamp", HttpRequestHelper.getTimestamp());

        //获取签名信息的医院签名
        String sign = HttpRequestHelper.getSign(bigParamMap, hospitalSignInfo.getSignKey());
        bigParamMap.put("sign", sign);
        //通过签名信息的Url（http://localhost:9998），发送请求到manage_hospital模块提交订单
        JSONObject Hospitalresult = HttpRequestHelper.sendRequest(bigParamMap, hospitalSignInfo.getApiUrl()+"/order/submitOrder");
        if(Hospitalresult.getInteger("code") == 200) { //发送成功时
// 补充预约信息到订单对象
            //获取大对象的数据json
            JSONObject jsonObject = Hospitalresult.getJSONObject("data");
            //获取预约信息
            String bookingRecordId= jsonObject.getString("bookingRecordId"); //获取预约记录主键，唯一标识
            Integer number = jsonObject.getInteger("number");;  //获取预约序号
            String fetchTime = jsonObject.getString("fetchTime");; //获取取号时间
            String fetchAddress = jsonObject.getString("fetchAddress");;    //获取取号地址
            orderInfo.setBookingRecordId(bookingRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);

//保存信息到订单消息对象
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(id);  //排班信息
            Integer reservedNumber = jsonObject.getInteger("reservedNumber"); //排班可预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber"); //排班剩余预约数
            orderMqVo.setAvailableNumber(availableNumber);
            orderMqVo.setReservedNumber(reservedNumber);
            //保存短信提示信息
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate =
                    new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                            + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);
            //保存短信提示到订单消息对象
            orderMqVo.setMsmVo(msmVo);

//使用RabbitMQ 发送信息更新号源和短信通知
            rabbitService.sendMessage(RabbitConstant.EXCHANGE_DIRECT_ORDER, RabbitConstant.ROUTING_ORDER, orderMqVo);

        } else {   //发送不成功时
            throw new bookException(Hospitalresult.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        return orderInfo.getId();
    }


    @Override
    public OrderInfo getOrder(String orderId) {   //获取单个订单

        OrderInfo orderInfo=baseMapper.selectById(orderId);
        return this.orderStatus(orderInfo);
    }

    @Override
    public IPage<OrderInfo> getOrderListPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {  //获取所有订单并分页

        //保存订单值到订单集合
        String hosname = orderQueryVo.getKeyword(); //医院名称
        Long patientId = orderQueryVo.getPatientId(); //就诊人ID
        String patientName = orderQueryVo.getPatientName();
        String orderStatus = orderQueryVo.getOrderStatus(); //订单状态
        String reserveDate = orderQueryVo.getReserveDate();//预约日期
        String outTradeNo = orderQueryVo.getOutTradeNo();
        String createTimeBegin = orderQueryVo.getCreateTimeBegin(); //订单生成时间
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();  //订单结束时间

        //后台查询时所需的项目
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(hosname)) {
            wrapper.like("hosname",hosname);
        }
        if(!StringUtils.isEmpty(outTradeNo)) {
            wrapper.eq("out_trade_no",outTradeNo);
        }
        if(!StringUtils.isEmpty(patientId)) {
            wrapper.eq("patient_id",patientId);
        }
        if(!StringUtils.isEmpty(patientName)) {
            wrapper.eq("patient_name",patientName);
        }
        if(!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status",orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)) {
            wrapper.ge("reserve_date",reserveDate);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        //分页
        IPage<OrderInfo> pages = baseMapper.selectPage(pageParam, wrapper);

        //订单状态循环添加状态码
        pages.getRecords().stream().forEach(item -> {
            this.orderStatus(item);
        });
        return pages;
    }

    @Override
    public Map<String, Object> getPatientListByOrderId(Long orderId) {

        Map<String, Object> map = new HashMap<>();
        OrderInfo orderInfo = this.orderStatus(this.getById(orderId));
        map.put("orderInfo", orderInfo);
        Patient patient =  loginFeignClient.getPatientById(orderInfo.getPatientId());
        map.put("patient", patient);
        return map;
    }


    @Override
    public Boolean cancelOrder(Long orderId) {

//订单查询和检验
        OrderInfo orderInfo = this.getById(orderId);
        //当前时间大约退号时间，不能取消预约
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if(quitTime.isBeforeNow()) {
            throw new bookException(ResultCodeEnum.CANCEL_ORDER_NO);
        }
        //查询医院签名
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfo(orderInfo.getHoscode());
        if(null == signInfoVo) {
            throw new bookException(ResultCodeEnum.PARAM_ERROR);
        }
//发送请求到manage_hospital模块取消订单预约
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode",orderInfo.getHoscode());
        reqMap.put("recordId",orderInfo.getBookingRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
        reqMap.put("sign", sign);
        //通过签名信息的Url（http://localhost:9998），发送请求到manage_hospital模块取消预约
        JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl()+"/order/updateCancelStatus");
        if(result.getInteger("code") != 200) {
            throw new bookException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        } else {
//退款处理
            //已支付时
            if(orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus().intValue()) {
                //退款处理
                boolean isRefund = wechatPaymentService.getWechatPaymentRefund(orderId);
                if(!isRefund) {
                    throw new bookException(ResultCodeEnum.CANCEL_ORDER_FAIL);
                }
            }
//更改订单状态为取消预约
            orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
            this.updateById(orderInfo);
//发送mq信息更新预约数 我们与下单成功更新预约数使用相同的mq信息，不设置可预约数与剩余预约数，接收端可预约数减1即可
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(orderInfo.getScheduleId());
//发送订单短信信息
            MsmVo msmVo = new MsmVo();
            //设置短信信息
            msmVo.setPhone(orderInfo.getPatientPhone());
            msmVo.setTemplateCode("SMS_194640722");
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);
            //发送短信
            rabbitService.sendMessage(RabbitConstant.EXCHANGE_DIRECT_ORDER, RabbitConstant.ROUTING_ORDER, orderMqVo);
        }
        return true;
    }


    @Override
    public void sendPatientNotice() {

//查询符合条件的就诊人
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        //不属于取消预约的订单
        queryWrapper.ne("order_status",OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfoList = baseMapper.selectList(queryWrapper);
//设置短信信息
        for(OrderInfo orderInfo : orderInfoList) {
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(RabbitConstant.EXCHANGE_DIRECT_MSM, RabbitConstant.ROUTING_MSM_ITEM, msmVo);
        }
    }


    @Override
    public Map<String, Object> getClientOrderStatisticsData(OrderCountQueryVo orderCountQueryVo) {

        Map<String, Object> map = new HashMap<>();
        //预约订单统计数据对象
        //selectOrderCount为自定义sql查询
        List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);
        //日期统计列表
        List<String> dateList
                =orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        //个数统计列表
        List<Integer> countList
                =orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());
        //保存日期和个数
        map.put("dateList", dateList);
        map.put("countList", countList);
        return map;
    }


    //++++++++++++++++++++++++++++++++++++++
    private OrderInfo orderStatus(OrderInfo orderInfo){

        //获取状态码
        String OrderStatus = OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus());
        orderInfo.getParam().put("orderStatusString",OrderStatus);
        return orderInfo;
    }

}



