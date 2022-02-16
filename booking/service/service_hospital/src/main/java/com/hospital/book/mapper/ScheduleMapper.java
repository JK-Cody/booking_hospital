package com.hospital.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.hosp.Schedule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {
}
