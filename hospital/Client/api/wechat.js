import request from '@/utils/myRequest'

const api_name = `/api/client/wechat`
const api_pay_name = `/api/order/payment`

export default {
    //微信登录
    getLoginParam() {
        return request({
        url: `${api_name}/getLoginParam`,
        method: `get`
        })
    },
    //生成微信支付二维码
    createWechatPayment(orderId) {
        return request({
        url: `${api_pay_name}/createWechatPayment/${orderId}`,
        method: `get`
        })
    },
     //获取支付状态
     getWechatPaymentStatus(orderId) {
        return request({
            url: `${api_pay_name}/getWechatPaymentStatus/${orderId}`,
            method: 'get'
        })
    }
 
    
    
}
