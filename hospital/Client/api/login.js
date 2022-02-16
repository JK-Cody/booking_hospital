import request from '@/utils/myRequest'

const api_name = `/api/client`

export default {
    //用户手机登录
    login(loginInfo) {
        return request({
            url: `${api_name}/login`,
            method: `post`,
            data: loginInfo
        })
    },
    //获取用户信息
    getUserInfo() {
        return request({
          url: `${api_name}/auth/getUserInfo`,
          method: `get`
        })
        },
    //用户身份验证 
    saveUserAuth(userAuah) {
        return request({
        url: `${api_name}/auth/userAuth`,
        method: 'post',
        data: userAuah
        })
    }
        
}
