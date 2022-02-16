import request from '@/utils/request'

export default{
  
    hospitalList(page,limit,searchObj) {//医院等级列表
      return request ({
        url: `/admin/hosp/hospital/list/${page}/${limit}`,
        method: 'get',
        params:searchObj
      })
    },
    findChildDataByDictCode(dictCode) {  //根据dictCode查询医院等级列表
      return request({
          url: `/admin/cmn/dict/findChildDataByDictCode/${dictCode}`,
          method: 'get'
        })
      },
      findChildData(id) {    //根据id查询医院等级列表
        return request({
            url: `/admin/cmn/dict/findChildData/${id}`,
            method: 'get'
          })  
        },
       updateStatus(id,status) {    //更新医院显示状态
          return request({
              url: `/admin/hosp/hospital/updateStatus/${id}/${status}`,
              method: 'get'
            })  
        },
        showHospitalDetail(id) {    //得到医院所有信息
            return request({
                url: `/admin/hosp/hospital/showHospitalDetail/${id}`,
                method: 'get'
              })  
        },
        departmentList(hoscode) {    //显示部门信息
          return request({
              url: `/admin/hosp/department/list/${hoscode}`,
              method: 'get'
            })  
        },
        scheduleList(page, limit, hoscode, depcode) {    //显示排班列表
          return request({
            url: `/admin/hosp/schedule/getSchedule/${page}/${limit}/${hoscode}/${depcode}`,
            method: 'get'       
            }) 
        },
        scheduleDetail(hoscode, depcode, workDate) {    //显示排班详细信息
          return request({
            url: `/admin/hosp/schedule/getScheduleDetail/${hoscode}/${depcode}/${workDate}`,
            method: 'get'       
            }) 
        }       
    }



  