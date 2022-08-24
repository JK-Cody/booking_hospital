import request from '@/utils/request'

export default{

    //获取列表项id
    getHospitalSet(id) {
        return request({
        url: `/admin/hosp/hospitalSet/getHospitalSet/${id}`,
        method: 'get'
    })  
    },

    //分页列表项
    findPageHospitalSet(current,limit,searchObj){
        return request({
            url:`/admin/hosp/hospitalSet/findPageHospitalSet/${current}/${limit}`,
            method:'post',    //请求类型
            data: searchObj  //通过post请求获取data的值
        })
    },

    //添加列表项
    saveHospitalSet(hospitalSet) {
        return request({
        url: `/admin/hosp/hospitalSet/saveHospitalSet`,
        method: 'post',
        data:hospitalSet
        })  
    },

    //删除列表项
    deleteHospitalSet(id) {
    return request ({
      url: `/admin/hosp/hospitalSet/deleteHospitalSet/${id}`,
      method: 'delete'
    })
  },

    //批量删除列表项
    batchDeleteHospitalSet(idList) {
        return request({
        url: `/admin/hosp/hospitalSet/batchDeleteHospitalSet`,
        method: 'delete',
        data: idList    
          })
    },
   
    //修改列表项
    updateHospitalSet(hospitalSet) {  //使用id替换
        return request({
        url: `/admin/hosp/hospitalSet/updateHospitalSet`,
        method: 'post',
        data:hospitalSet
     })  
    },

    //锁定列表项和解锁
    lockHospitalSet(id,status) {
    return request({
    url: `/admin/hosp/hospitalSet/lockHospitalSet/${id}/${status}`,
    method: 'put'
        })  
    }
}