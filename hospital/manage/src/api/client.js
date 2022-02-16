import request from '@/utils/request'

const api_name = '/admin/client'

export default {
 //获取用户列表
 getUserList(page, limit, searchObj) {
  return request({
    url: `${api_name}/${page}/${limit}`,
    method: 'get',
    params: searchObj
  })
 },
//锁定用户
lockUser(userId,status) {
  return request({
    url: `${api_name}/lockUser/${userId}/${status}`,
    method: 'get',
  })
 },
 //获取用户详情
 showUserDetail(userId) {
  return request({
    url: `${api_name}/showUser/${userId}`,
    method: 'get'
  })
 },
 //确认用户认证审批状态
 setUserAuthenticationStatus(userId, authStatus) {
  return request({
    url: `${api_name}/authUser/${userId}/${authStatus}`,
    method: 'get'
  })
}

}
