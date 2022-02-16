import request from '@/utils/myRequest'

const api_name = `/api/order`

export default {
 //用户提交挂号订单
 submitOrder(scheduleId, patientId) {
  return request({
    url: `${api_name}/auth/submitOrder/${scheduleId}/${patientId}`,
    method: 'post'
  })
 },
 //用户获取单个挂号订单
 getOrder(orderId) {
  return request({
    url: `${api_name}/auth/getOrder/${orderId}`,
    method: 'get'
  })
 },
 //用户获取订单列表
 getOrderList(page, limit, searchObj) {
  return request({
      url: `${api_name}/auth/getOrderList/${page}/${limit}`,
      method: `get`,
      params: searchObj
  })
},
//用户获取订单状态列表
getOrderStatusList() {
  return request({
      url: `${api_name}/auth/getOrderStatusList`,
      method: 'get'
  })
},
// 取消预约订单
cancelOrder(orderId) {
return request({
    url: `${api_name}/auth/cancelOrder/${orderId}`,
    method: 'get'
  })
},


}
