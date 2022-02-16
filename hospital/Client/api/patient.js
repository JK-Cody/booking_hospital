import request from '@/utils/myRequest'
const api_name = `/api/client/patient`

export default {
    //就诊人列表
    getPatientList() {
        return request({
            url: `${api_name}/auth/getPatientList`,
            method: `get`
        })
    },
    //根据id查询就诊人信息
    getPatient(id) {
        return request({
            url: `${api_name}/auth/getPatient/${id}`,
            method: 'get'
        })
},
    //添加就诊人信息
    savePatient(patient) {
        return request({
            url: `${api_name}/auth/savePatient`,
            method: 'post',
            data: patient
        })
    },
    //修改就诊人信息
    updatePatientById(patient) {
        return request({
            url: `${api_name}/auth/updatePatient`,
            method: 'post',
            data: patient
        })
    },
    //删除就诊人信息
    removePatientById(id) {
        return request({
            url: `${api_name}/auth/removePatient/${id}`,
            method: 'delete'
        })
    }
}
