import request from '@/utils/myRequest'
const api_name = `/admin/cmn/dict/`

export default{
    //获取医院列表
    getChildDataByDictCode(dictCode){
        return request({
            url:`${api_name}/findChildDataByDictCode/${dictCode}`,
            method: 'get',
        })
    },
    //根据医院名称获取医院列表
    getChildData(id){
        return request({
            url:`${api_name}/findChildData/${id}`,
            method: 'get'
        })
    }
}