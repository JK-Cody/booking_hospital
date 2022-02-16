package com.hospital.feign.client.dictionary;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name ="service-dictionary")
@Repository
public interface DictionaryFeignClient {

    /**
     * 发送请求让service_dictionary响应，获取数据字典名称
     * @param dictCode
     * @param value
     * @return
     */
    @GetMapping(value = "/admin/cmn/dict/getName/{dictCode}/{value}")
    public String getName(@PathVariable("dictCode") String dictCode,
                          @PathVariable("value") String value);

    /**
     * 发送请求让service_dictionary响应，获取数据字典名称
     * @param value
     * @return
     */
    @GetMapping(value = "/admin/cmn/dict/getName/{value}")
    public String getName(@PathVariable("value") String value);
}