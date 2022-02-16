package com.hospital.dictionary.service;

import com.baomidou.mybatisplus.extension.service.IService;
import model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface dictionaryService extends IService<Dict> {

    //获取dictionary列表的单条数据
    List<Dict> findChlidData(Long id);

    //   将列表输出到文件
    void exportExcelData(HttpServletResponse response);

    void importExcelData(MultipartFile file);

    String getNameByDictCodeAndValue(String dictCode, String value);

    List<Dict> findChlidDataByDictCode(String dictCode);
}
