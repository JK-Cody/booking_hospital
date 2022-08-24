package com.hospital.manage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hospital.manage.mapper.OrderInfoMapper;
import com.hospital.manage.mapper.ScheduleMapper;
import com.hospital.manage.model.OrderInfo;
import com.hospital.manage.model.Patient;
import com.hospital.manage.model.Schedule;
import com.hospital.manage.service.OrderManageService;
import com.hospital.manage.util.ResultCodeEnum;
import com.hospital.manage.util.manageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OrderManageServiceImpl implements OrderManageService {

    @Autowired
	private ScheduleMapper scheduleMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;


    @Transactional(rollbackFor = Exception.class)  //发生该异常时回滚
    @Override
    public Map<String, Object> submitOrder(Map<String, Object> paramMap) {

        log.info(JSONObject.toJSONString(paramMap));
        String hoscode = (String)paramMap.get("hoscode");
        String depcode = (String)paramMap.get("depcode");
        String scheduleId = (String)paramMap.get("scheduleId");
        String reserveDate = (String)paramMap.get("reserveDate");
        String reserveTime = (String)paramMap.get("reserveTime");
        String amount = (String)paramMap.get("amount");

        Schedule schedule = this.getSchedule(scheduleId);
        if(null == schedule) {
            throw new manageException(ResultCodeEnum.DATA_ERROR);
        }

        if(!schedule.getHoscode().equals(hoscode)
                || !schedule.getDepcode().equals(depcode)
                || !schedule.getAmount().toString().equals(amount)) {
            throw new manageException(ResultCodeEnum.DATA_ERROR);
        }

        //就诊人信息
        Patient patient = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Patient.class);
        log.info(JSONObject.toJSONString(patient));
        //处理就诊人业务
        Long patientId = this.savePatient(patient);

        Map<String, Object> resultMap = new HashMap<>();
        int availableNumber = schedule.getAvailableNumber() - 1;
        if(availableNumber > 0) {
            schedule.setAvailableNumber(availableNumber);
            scheduleMapper.updateById(schedule);

            //记录预约记录
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setPatientId(patientId);
            orderInfo.setScheduleId(Long.parseLong(scheduleId));
            int number = schedule.getReservedNumber() - schedule.getAvailableNumber();
            orderInfo.setNumber(number);
            orderInfo.setAmount(new BigDecimal(amount));
            String fetchTime = "0".equals(reserveDate) ? " 09:30前" : " 14:00前";
            orderInfo.setFetchTime(reserveTime + fetchTime);
            orderInfo.setFetchAddress("一楼9号窗口");
            //默认 未支付
            orderInfo.setOrderStatus(0);
            orderInfoMapper.insert(orderInfo);

            resultMap.put("resultCode","0000");
            resultMap.put("resultMsg","预约成功");
            //预约记录唯一标识（医院预约记录主键）
            resultMap.put("bookingRecordId", orderInfo.getId());
            //预约号序
            resultMap.put("number", number);
            //取号时间
            resultMap.put("fetchTime", reserveDate + "09:00前");;
            //取号地址
            resultMap.put("fetchAddress", "一层114窗口");;
            //排班可预约数
            resultMap.put("reservedNumber", schedule.getReservedNumber());
            //排班剩余预约数
            resultMap.put("availableNumber", schedule.getAvailableNumber());
        } else {
            throw new manageException(ResultCodeEnum.DATA_ERROR);
        }
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class)  //发生该异常时回滚
    @Override
    public void updatePayStatus(Map<String, Object> paramMap) {
        String hoscode = (String)paramMap.get("hoscode");
        String bookingRecordId = (String)paramMap.get("bookingRecordId");

        OrderInfo orderInfo = orderInfoMapper.selectById(bookingRecordId);
        if(null == orderInfo) {
            throw new manageException(ResultCodeEnum.DATA_ERROR);
        }
        //已支付
        orderInfo.setOrderStatus(1);
        orderInfo.setPayTime(new Date());
        orderInfoMapper.updateById(orderInfo);
    }


    @Transactional(rollbackFor = Exception.class)  //发生该异常时回滚
    @Override
    public void updateCancelStatus(Map<String, Object> paramMap) {
        String hoscode = (String)paramMap.get("hoscode");
        String bookingRecordId = (String)paramMap.get("bookingRecordId");

        OrderInfo orderInfo = orderInfoMapper.selectById(bookingRecordId);
        if(null == orderInfo) {
            throw new manageException(ResultCodeEnum.DATA_ERROR);
        }
        //已取消
        orderInfo.setOrderStatus(-1);
        orderInfo.setQuitTime(new Date());
        orderInfoMapper.updateById(orderInfo);
    }


    /**
     * 医院处理就诊人信息
     * @param id
     */
    private Schedule getSchedule(String id) {
        return scheduleMapper.selectById(id);
    }


    /**
     * 医院处理就诊人信息
     * @param patient
     */
    private Long savePatient(Patient patient) {
        // 业务：略
        return 1L;
    }


}
