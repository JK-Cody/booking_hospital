package com.hospital.dictionary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.cmn.Dict;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface dictionaryMapper extends BaseMapper<Dict> {
}
