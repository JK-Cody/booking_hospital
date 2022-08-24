package com.hospital.book.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.hospital.book.repository.DepartmentRepository;
import com.hospital.book.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import model.hosp.Department;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import vo.hosp.DepartmentQueryVo;
import vo.hosp.DepartmentVo;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> paramMap) {

        String jsonString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(jsonString, Department.class);
        //查询数据库
        Department targetDepartment = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        if(null != targetDepartment) {
            //复制不为null的值，该方法为自定义方法
//            BeanUtils.copyBean(department, targetDepartment, Department.class);
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(targetDepartment);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }

    }

    @Override
    public Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo){

        //设定排序
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //设定页码
        Pageable pageable = PageRequest.of(page-1, limit, sort); //0为第一页
        //将departmentQueryVo的数据复制到department对象里
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);
        //创建匹配器规则，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        //创建匹配器对象
        Example<Department> example = Example.of(department, matcher);

        return departmentRepository.findAll(example, pageable);
    }

    @Override
    public void remove(String hoscode, String depcode) {

        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(null != department) {
        //departmentRepository.delete(department);
            departmentRepository.deleteById(department.getId());
        }
    }

    //根据医院编号查询部门列表
    @Override
    public List<DepartmentVo> getDepartmentList(String hoscode) {

        List<DepartmentVo> bigcodeList=new ArrayList<>();    //保存大科室组的数据
        //根据hoscode查找部门
        Department department = new Department();
        department.setHoscode(hoscode);
        //返回部门信息的集合
        Example<Department> example = Example.of(department);
        List<Department> departmentList = departmentRepository.findAll(example);

        //按照部门信息的大科室编号作分组，创建新集合
        Map<String,List<Department>> bigBigcodeMap = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        for(Map.Entry<String,List<Department>>entry:bigBigcodeMap.entrySet()){
            //得到每组大科室编号
            String bigcode=entry.getKey();
            //得到每组大科室的值（包含科室）
            List<Department> bigCodeValue = entry.getValue();

            //将大科室数据保存在DepartmentVo1
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigcode);
            //通常大科室是唯一的，所以取第一个元素
            departmentVo1.setDepname(bigCodeValue.get(0).getBigname());

            List<DepartmentVo> depcodeList=new ArrayList<>();   //保存所有科室组的数据
            //根据大科室编号得到每组科室数据
            for(Department depcodeValue:bigCodeValue){
                //将每组科室数据保存在DepartmentVo2
                DepartmentVo departmentVo2 = new DepartmentVo();
                departmentVo2.setDepcode(depcodeValue.getDepcode());
                departmentVo2.setDepname(depcodeValue.getDepname());
                depcodeList.add(departmentVo2);  //保存所有科室组的数据
            }
            //设置大科室数据对应的科室数据
            departmentVo1.setChildren(depcodeList);
            bigcodeList.add(departmentVo1);  //保存所有大科室组的数据
        }
        return bigcodeList;
    }

    @Override
    public String getDepartmentName(String hoscode, String depcode) {

        Department departmentByHoscodeAndDepcode = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(departmentByHoscodeAndDepcode!=null) {
            return departmentByHoscodeAndDepcode.getDepname();
        }
        return null;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {

        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }

}
