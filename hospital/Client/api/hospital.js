import request from '@/utils/myRequest'
const api_name = `/api/hosp/client`

export default{
    //获取医院列表
    getHospitalList(page,limit,searchObj){
        return request({
            url:`${api_name}/clientGetHospitalList/${page}/${limit}`,
            method: 'get',
            params: searchObj
        })
    },
    //根据医院名称获取医院详细"
    getHospitalByHosname(hosname){
        return request({
            url:`${api_name}/clientGetHospitalDetail/${hosname}`,
            method: 'get'
        })
    },
    //根据医院Code获取挂号详情
    getHospitalDetail(hoscode) {
        return request({
            url: `${api_name}/clientGetHospitalByHoscode/${hoscode}`,
            method: 'get'
        })
    },
      //根据医院Code获取科室
      getDepartmentByHosname(hoscode) {
        return request({
            url: `${api_name}/clientGetDepartmentByHoscode/${hoscode}`,
            method: 'get'
        })
    },
    //获取可预约排班数据
    getBookingScheduleRule(page, limit, hoscode, depcode) {
        return request({
            url: `${api_name}/auth/getBookingScheduleRule/${page}/${limit}/${hoscode}/${depcode}`,
            method: 'get'
        })
    },
    //获取单个医院的详细排班数据      
    getHospitalSchedule(hoscode, depcode, workDate) {
        return request({
            url: `${api_name}/auth/getHospitalSchedule/${hoscode}/${depcode}/${workDate}`,
            method: 'get'
        })
    },
    //获取单个医院的详细排班数据   
    getSchedule(scheduleId) {
        return request({
          url: `${api_name}/getSchedule/${scheduleId}`,
          method: 'get'
        })
    }   
    

}