import request from '@/utils/request'

const api_name = '/admin/statistics'

export default {

    getOrderStatistics(searchObj) {
        return request({
            url: `${api_name}/getOrderStatistics`,
            method: 'get',
            params: searchObj
        })
    }
}
