import request from '@/utils/myRequest'

const api_name = `/api/client/msm`

export default {
    sendCode(phone) {
        return request({
            url: `${api_name}/send/${phone}`,
            method: `get`
        })
    }
}
