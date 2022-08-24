import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import cookie from 'js-cookie'

// 创建axios实例
const service = axios.create({
    baseURL: 'http://localhost:8000',    //指向gateway的端口号
    timeout: 15000 // 请求超时时间
})

// 拦截器获取token参数
service.interceptors.request.use(   // http request 拦截器
    config => {
        //判断cookie是否有token值
        if(cookie.get('token')) {
            //token值放到cookie里面
            config.headers['token']=cookie.get('token')
        }
        return config
    },    
  err => {
    return Promise.reject(err)
})

service.interceptors.response.use(   // http response 拦截器
    response => {
        //登录的接口如果token没有或者token过期，服务器端会返回208状态
        // 拦截后端响应码为208时，弹窗登录框
        if(response.data.code === 208) {
            //弹出登录输入框
            loginEvent.$emit('loginDialogEvent')  //响应myheader的全局监听事件
            return
        } else {
            if (response.data.code !== 200) {
                Message({
                    message: response.data.message,
                    type: 'error',
                    duration: 5 * 1000
                })
                return Promise.reject(response.data)
            } else {
                return response.data
            }
        }
    },
    error => {
        return Promise.reject(error.response)
})
export default service
