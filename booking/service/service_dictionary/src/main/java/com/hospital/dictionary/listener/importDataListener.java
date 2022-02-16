package com.hospital.dictionary.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.hospital.dictionary.mapper.dictionaryMapper;
import model.cmn.Dict;
import org.springframework.beans.BeanUtils;
import vo.cmn.DictEeVo;

public class importDataListener extends AnalysisEventListener<DictEeVo> {

    private dictionaryMapper dictionaryMapper;

    public importDataListener(dictionaryMapper dm){
        this.dictionaryMapper=dm;
    }
//    按文件的行数读取
    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();  //输入的文件格式
        BeanUtils.copyProperties(dictEeVo,dict);  //格式转换为输入文件格式
        dictionaryMapper.insert(dict);

     }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
