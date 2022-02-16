package com.hospital.book.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.book.mapper.ScheduleMapper;
import com.hospital.book.service.DepartmentService;
import com.hospital.book.service.HospitalService;
import com.hospital.common.exception.bookException;
import com.hospital.common.result.ResultCodeEnum;
import model.hosp.BookingRule;
import model.hosp.Department;
import model.hosp.Hospital;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import com.hospital.book.repository.ScheduleRepository;
import com.hospital.book.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import model.hosp.Schedule;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.stereotype.Service;
import vo.hosp.BookingScheduleRuleVo;
import vo.hosp.ScheduleOrderVo;
import vo.hosp.ScheduleQueryVo;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;


    ScheduleMapper scheduleMapper;

    @Override
    public void save(Map<String, Object> paramMap) {

        String toJSONString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(toJSONString, Schedule.class);
        //查询数据库
        Schedule targetSchedule = scheduleRepository.getScheduleByHoscodeAndScheduleId(schedule.getHoscode(), schedule.getScheduleId());
        if(null != targetSchedule) {
        //值copy不为null的值，该方法为自定义方法
//            BeanUtils.copyBean(schedule, targetSchedule, Schedule.class);
            schedule.setCreateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(0);
            scheduleRepository.save(targetSchedule);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(0);
            scheduleRepository.save(schedule);
        }
    }


    @Override
    public Page<Schedule> selectPage(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo) {

        //设定排序
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //设定页码
        Pageable pageable = PageRequest.of(page-1, limit, sort);  //0为第一页
        //将ScheduleQueryVo的数据复制到Schedule对象里
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);
        //创建匹配器规则，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        //创建匹配器对象
        Example<Schedule> example = Example.of(schedule, matcher);
        //创建分页对象
        Page<Schedule> pages = scheduleRepository.findAll(example, pageable);
        return pages;
    }


    @Override
    public void remove(String hoscode, String scheduleId) {

        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndScheduleId(hoscode, scheduleId);
        if(null != schedule) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }


    @Override
    public Map<String, Object> getScheduleList(long page, long limit, String hoscode, String depcode) {

            //1 根据医院编号 和 科室编号 查询
            Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

            //2 根据工作日workDate期进行分组
            Aggregation agg = Aggregation.newAggregation(
                    Aggregation.match(criteria),//匹配条件
                    Aggregation.group("workDate")//分组字段
                            .first("workDate").as("workDate")
                            //3 统计号源数量
                            .count().as("docCount")
                            .sum("reservedNumber").as("reservedNumber")
                            .sum("availableNumber").as("availableNumber"),
                    //排序
                    Aggregation.sort(Sort.Direction.DESC,"workDate"),
                    //4 实现分页
                    Aggregation.skip((page-1)*limit),
                    Aggregation.limit(limit)
            );
            //调用方法，最终执行
            AggregationResults<BookingScheduleRuleVo> aggResults =
                    mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
            List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();

            //分组查询的总记录数
            Aggregation totalAgg = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    Aggregation.group("workDate")
            );
            AggregationResults<BookingScheduleRuleVo> totalAggResults =
                    mongoTemplate.aggregate(totalAgg,
                            Schedule.class, BookingScheduleRuleVo.class);
            int total = totalAggResults.getMappedResults().size();

            //把日期对应星期获取
            for(BookingScheduleRuleVo bookingScheduleRuleVo:bookingScheduleRuleVoList) {
                Date workDate = bookingScheduleRuleVo.getWorkDate();
                String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
                bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
            }

            //设置最终数据，进行返回
            Map<String, Object> result = new HashMap<>();
            result.put("bookingScheduleRuleList",bookingScheduleRuleVoList);
            result.put("total",total);

            //获取医院名称
            String hosName = hospitalService.getHospitalName(hoscode);
            //其他基础数据
            Map<String, String> baseMap = new HashMap<>();
            baseMap.put("hosname",hosName);
            result.put("baseMap",baseMap);

            return result;
        }



        @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {

        List<Schedule> scheduleDetail=
                scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        scheduleDetail.stream().forEach(item->{
            this.getOtherColunmBySchedule(item);
        });
        return scheduleDetail;
    }

    @Override
    public Map<String, Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode) {

        //用于最后收集结果
        Map<String,Object> mapBookingScheduleRule=new HashMap<>();

        Hospital hospital = hospitalService.getByHoscode(hoscode);
        if(hospital==null){
            throw new bookException(ResultCodeEnum.DATA_ERROR);
        }
        //获取医院的排班规则
        BookingRule bookingRule = hospital.getBookingRule();
        //获取预约挂号的日期数据
        IPage pageBookingRuleData = this.getBookingRuleData(page,limit,bookingRule); //实现分页
        List <Date> BookingRuleDataList = pageBookingRuleData.getRecords();

        //获取MongoDB的数据,可预约日期科室剩余预约数
        Criteria criteria =Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(BookingRuleDataList);
        Aggregation aggregation =Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")//分组字段
                        .first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );
        AggregationResults<BookingScheduleRuleVo> aggregationResults =
                mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> ListBookingScheduleRule = aggregationResults.getMappedResults();

        //转为 可预约日期+预约日期科室剩余预约数 的集合
        Map<Date, BookingScheduleRuleVo> mapDateWithListBookingScheduleRule = new HashMap<>();
        if(!CollectionUtils.isEmpty(ListBookingScheduleRule)) {
            mapDateWithListBookingScheduleRule = ListBookingScheduleRule.stream().collect(Collectors
                    .toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));
        }

        //获取可预约排班规则
        List<BookingScheduleRuleVo> bookingScheduleInDateList = new ArrayList<>();
        //循环获取可预约挂号的日期数据
        for(int i=0,len=BookingRuleDataList.size();i<len;i++) {
            Date date = BookingRuleDataList.get(i);
            //获取可预约挂号的日期的预约数
            BookingScheduleRuleVo bookingScheduleInDate = mapDateWithListBookingScheduleRule.get(date);
            if(null == bookingScheduleInDate) { // 说明当天没有排班医生
                bookingScheduleInDate = new BookingScheduleRuleVo();
                //就诊医生人数为 0
                bookingScheduleInDate.setDocCount(0);
                //科室剩余预约数  -1表示无号
                bookingScheduleInDate.setAvailableNumber(-1);
            }
            bookingScheduleInDate.setWorkDate(date);
            bookingScheduleInDate.setWorkDateMd(date);
            //计算当前预约日期为周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleInDate.setDayOfWeek(dayOfWeek);

            //最后一页最后一条记录为即将预约   状态 0：正常 1：即将放号 -1：当天已停止挂号
            if(i == len-1 && page == pageBookingRuleData.getPages()) {
                bookingScheduleInDate.setStatus(1);
            } else {
                bookingScheduleInDate.setStatus(0);
            }
            //当天预约如果过了停号时间， 不能预约
            if(i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if(stopTime.isBeforeNow()) {
                    //停止预约
                    bookingScheduleInDate.setStatus(-1);
                }
            }
            bookingScheduleInDateList.add(bookingScheduleInDate);
        }

        //将上述结果存入集合
        mapBookingScheduleRule.put("bookingScheduleList", bookingScheduleInDateList);
        mapBookingScheduleRule.put("total", pageBookingRuleData.getTotal());
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hospitalService.getHospitalName(hoscode));
        Department department =departmentService.getDepartment(hoscode, depcode);
        baseMap.put("bigname", department.getBigname());
        baseMap.put("depname", department.getDepname());
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        baseMap.put("stopTime", bookingRule.getStopTime());
        mapBookingScheduleRule.put("baseMap", baseMap);
        return mapBookingScheduleRule;
    }


    @Override
    public Schedule getScheduleById(String id) {

        Schedule schedule = scheduleRepository.findById(id).get();
        return this.getOtherColunmBySchedule(schedule);
    }


    @Override
    public ScheduleOrderVo getBookingOrder(String id) {

        //获取排班对象
        Schedule schedule = this.getScheduleById(id);
        if(null==schedule){
            throw new bookException(ResultCodeEnum.PARAM_ERROR);
        }
        //获取对应医院对象
        Hospital hospital = hospitalService.getByHoscode(schedule.getHoscode());
        if(null==hospital){
            throw new bookException(ResultCodeEnum.PARAM_ERROR);
        }
        //获取对应排班规则
        BookingRule bookingRule = hospital.getBookingRule();
        if(null==bookingRule){
            throw new bookException(ResultCodeEnum.PARAM_ERROR);
        }

        //将上述保存在预约订单集合
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        scheduleOrderVo.setHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospitalService.getHospitalName(schedule.getHoscode()));
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        scheduleOrderVo.setDepname(departmentService.getDepartmentName(schedule.getHoscode(), schedule.getDepcode()));
        scheduleOrderVo.setScheduleId(schedule.getScheduleId());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());

        //保存退号截止天数（如：就诊前一天为-1，当天为0）
        int quitDay = bookingRule.getQuitDay();
        DateTime quitTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(), bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(quitTime.toDate());

        //保存预约开始时间
        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());

        //保存预约截止时间
        DateTime endTime = this.getDateTime(new DateTime().plusDays(bookingRule.getCycle()).toDate(), bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());

        //保存当天停止挂号时间
        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStartTime(startTime.toDate());
        return scheduleOrderVo;

    }

    @Override
    public void updateTimeForSchedule(Schedule schedule) {

        //更新时间
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }


    //+++++++++++++++++++++工具方法
    /**
     * 对预约挂号数据的日期进行分页
     * @param page
     * @param limit
     * @param bookingRule
     * @return
     */
    private IPage getBookingRuleData(int page, int limit, BookingRule bookingRule) {
        //当天放号时间
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        //预约周期
        int cycle = bookingRule.getCycle();
        //如果当天放号时间已过，则预约周期后一天为即将放号时间，周期加1
        if (releaseTime.isBeforeNow()) {
            cycle += 1;
        }
        //可预约所有日期，最后一天显示即将放号倒计时
        List<Date> dateList = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
        //计算当前预约日期
            DateTime curDateTime = new DateTime().plusDays(i);
            String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }
        //日期分页，由于预约周期不一样，页面一排最多显示7天数据，多了就要分页显示
        List<Date> pageDateList = new ArrayList<>();
        int start = (page - 1) * limit;
        int end = (page - 1) * limit + limit;
        if (end > dateList.size()) {
            end = dateList.size();
        }
        for (int i = start; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }
        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page(page, 7, dateList.size());
        iPage.setRecords(pageDateList);
        return iPage;
    }

    /**
     * 封装排班详情其他值 医院名称、科室名称、日期对应星期
     * @param schedule
     */
    private Schedule getOtherColunmBySchedule(Schedule schedule) {

        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospitalName(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depname",departmentService.getDepartmentName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
        return schedule;
    }


    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }


}

