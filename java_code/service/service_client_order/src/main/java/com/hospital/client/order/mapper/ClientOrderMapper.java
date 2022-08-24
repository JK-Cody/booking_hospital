package com.hospital.client.order.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import vo.order.OrderCountQueryVo;
import vo.order.OrderCountVo;

import java.util.List;

@Mapper
public interface ClientOrderMapper extends BaseMapper<OrderInfo> {

    //预约订单统计数据（用于图表查询）
    //selectOrderCount为自定义sql查询
    List<OrderCountVo> selectOrderCount(@Param("OrderCount") OrderCountQueryVo orderCountQueryVo);
}
