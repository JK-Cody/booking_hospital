import request from '@/utils/request'
const api_name = '/admin/order'

export default {
    //获取用户订单列表 
    getOrderList(page, limit, searchObj) {
    return request({
      url: `${api_name}/getOrderList/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  },
    //获取用户订单状态列表
    getOrderStatusList() {
    return request({
        url: `${api_name}/getOrderStatusList`,
        method: 'get'
    })
  },
  //获取就诊人列表
  getPatientList(orderId) {
    return request({
      url: `${api_name}/getPatientList/${orderId}`,
      method: 'get'
    })
  }



}
