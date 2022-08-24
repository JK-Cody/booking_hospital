package com.hospital.book.service;


import model.hosp.Schedule;
import org.springframework.data.domain.Page;
import vo.hosp.ScheduleOrderVo;
import vo.hosp.ScheduleQueryVo;

import java.util.List;
import java.util.Map;

public interface ScheduleService {

    /**
     * 上传排班信息
     * @param paramMap
     */
    void save(Map<String, Object> paramMap);

    /**
     * 分页查询
     * @param page 当前页码
     * @param limit 每页记录数
     * @param scheduleQueryVo 查询条件
     * @return
     */
    Page<Schedule> selectPage(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo);

    /**
     * 删除科室
     * @param hoscode
     * @param scheduleId
     */
    void remove(String hoscode, String scheduleId);

    /**
     * 获取排班信息
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    Map<String, Object> getScheduleList(long page, long limit, String hoscode, String depcode);

    /**
     * 获取排班详细信息
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate);

    /**
     * 获取排班可预约日期数据
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    Map<String, Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode);

    /**
     * 根据id获取排班
     * @param id
     * @return
     */
    Schedule getScheduleById(String id);

    /**
     * 根据排班id获取预约下单数据
     * @param scheduleId
     * @return
     */
    ScheduleOrderVo getBookingOrder(String scheduleId);

    /**
     * 对排班数据更新时间
     * @param schedule
     * @return
     */
    void updateTimeForSchedule(Schedule schedule);
}
