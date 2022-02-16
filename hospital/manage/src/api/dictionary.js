import request from '@/utils/request'

export default {
  
    dictionaryList(id) {//数据字典列表
      return request ({
        url: `/admin/cmn/dict/findChildData/${id}`,
        method: 'get'
      })
    }
  }



  