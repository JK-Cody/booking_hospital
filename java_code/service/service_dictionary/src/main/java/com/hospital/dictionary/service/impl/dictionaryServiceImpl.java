package com.hospital.dictionary.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.dictionary.listener.importDataListener;
import com.hospital.dictionary.mapper.dictionaryMapper;
import com.hospital.dictionary.service.dictionaryService;
import model.cmn.Dict;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vo.cmn.DictEeVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class dictionaryServiceImpl extends ServiceImpl<dictionaryMapper,Dict> implements dictionaryService {


    /**
     * 获取dictionary列表的单条数据
     * @param id
     * @return
     */
    @Override
    @Cacheable(value = "dict",keyGenerator ="keyGenerator")
    public List<Dict> findChlidData(Long id){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        for (Dict dict:dictList) {
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }
        return dictList;
    }

    /**
     * 判断该id是否有父id
     * @param id
     * @return
     */
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);
        return count>0;  //存在则为true
    }

    /**
     *  将列表输出到文件
      * @param response
     */
    @Override
    public void exportExcelData(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            //设置输出的文件类型
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");
            //将列表信息保存在集合
            List<Dict> dictList = baseMapper.selectList(null);
            List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
            for(Dict dict : dictList) {
                DictEeVo dictVo = new DictEeVo();  //输出的文件格式
                BeanUtils.copyProperties(dict, dictVo);  //格式转换为输出文件格式
                dictVoList.add(dictVo);
            }
            //  easyexcel工具方法
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将文件输入到列表
     * @param file
     */
    @Override
    @CacheEvict(value="dict",allEntries=true)
    public void importExcelData(MultipartFile file) {   //必须为file才能匹配到element-ui的组件格式

        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new importDataListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNameByDictCodeAndValue(String dictCode, String value) {

        if(StringUtils.isEmpty(dictCode)){     //没有dictCode就查询value
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("value",value);
            return baseMapper.selectOne(wrapper).getName();
        }else {                               //否则查询dictCode
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("dict_code",dictCode);
            Long parentId = baseMapper.selectOne(wrapper).getId();
            Dict findDict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parentId)
                    .eq("value", value));
            return findDict.getName();
        }
    }

    @Override
    public List<Dict> findChlidDataByDictCode(String dictCode) {

        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code",dictCode);
        Dict ChlidData = baseMapper.selectOne(wrapper);
        List<Dict> chlidData = this.findChlidData(ChlidData.getId());
        return chlidData;
    }


}

