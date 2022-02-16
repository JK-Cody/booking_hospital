package com.hospital.client.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import model.order.OrderInfo;
import vo.order.OrderCountQueryVo;
import vo.order.OrderQueryVo;

import java.util.Map;

public interface ClientOrderService extends IService<OrderInfo> {

    //保存订单
    Long saveOrder(String scheduleId, Long patientId);

    //获取单个订单
    OrderInfo getOrder(String orderId);

    //查询订单的列表
    IPage<OrderInfo> getOrderListPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    //后台程序获取候诊人订单
    Map<String,Object> getPatientListByOrderId(Long orderId);

    //取消订单
    Boolean cancelOrder(Long orderId);

    //就诊通知
    void sendPatientNotice();

    //所有客户端订单统计数据图表
    Map<String, Object> getClientOrderStatisticsData(OrderCountQueryVo orderCountQueryVo);

}
