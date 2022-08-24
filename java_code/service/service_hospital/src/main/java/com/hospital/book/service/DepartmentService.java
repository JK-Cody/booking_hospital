package com.hospital.book.service;

import model.hosp.Department;
import org.springframework.data.domain.Page;
import vo.hosp.DepartmentQueryVo;
import vo.hosp.DepartmentVo;

import java.util.List;
import java.util.Map;

public interface DepartmentService {

    /**
     * 上传科室信息
     * @param paramMap
     */
    void save(Map<String, Object> paramMap);

    /**
     * 分页查询
     * @param page 当前页码
     * @param limit 每页记录数
     * @param departmentQueryVo 查询条件
     * @return
     */
    Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    /**
     * 删除科室
     * @param hoscode
     * @param depcode
     */
    void remove(String hoscode, String depcode);

    /**
     * 根据医院编号查询部门列表
     * @param hoscode
     * @return
     */
    List<DepartmentVo> getDepartmentList(String hoscode);

    /**
     * 获取科室名称
     * @param hoscode
     * @param depcode
     * @return
     */
    String getDepartmentName(String hoscode, String depcode);

    /**
     * 获取部门
     */
    Department getDepartment(String hoscode, String depcode);

}
