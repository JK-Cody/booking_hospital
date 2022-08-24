package com.hospital.manage.controller;

import com.hospital.manage.mapper.HospitalManageMapper;
import com.hospital.manage.model.HospitalSet;
import com.hospital.manage.service.HospitalManageService;
import com.hospital.manage.util.manageException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "医院&科室&排班——数据管理的方法")
@Controller
@RequestMapping
public class HospitalManageController extends BaseController {

	@Autowired
	private HospitalManageService hospitalManageService;

	@Autowired
	private HospitalManageMapper hospitalManageMapper;

	/**
	 * 接收请求后转发到 hospitalSet目录的index
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	//  前缀: http://localhost:9998/
	@RequestMapping("/hospitalSet/index")
	public String getHospitalSet(ModelMap model,RedirectAttributes redirectAttributes) {

		HospitalSet hospitalSet = hospitalManageMapper.selectById(1);
		//添加
		model.addAttribute("hospitalSet", hospitalSet);
		return "hospitalSet/index";
	}

	/**
	 * 更新医院设置对象后转发到 hospitalSet目录的index
	 * @param model
	 * @param hospitalSet
	 * @return
	 */
	@RequestMapping(value="/hospitalSet/save")
	public String createHospitalSet(ModelMap model, HospitalSet hospitalSet) {
		hospitalManageMapper.updateById(hospitalSet);
		return "redirect:/hospitalSet/index";
	}

	/**
	 * 查询医院对象后转发到 hospital目录的index
	 * @param model
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("/hospital/index")
	public String getHospital(ModelMap model,HttpServletRequest request,RedirectAttributes redirectAttributes) {
		try {
			//验证医院设置对象
			HospitalSet hospitalSet = hospitalManageMapper.selectById(1);
			//无医院code与签名key时，返回错误信息
			if(null == hospitalSet || StringUtils.isEmpty(hospitalSet.getHoscode()) || StringUtils.isEmpty(hospitalSet.getSignKey())) {
				this.failureMessage("先设置医院code与签名key", redirectAttributes);
				return "redirect:/hospitalSet/index";
			}
			//验证通过后,查询该医院对象并保存参数
			model.addAttribute("hospital", hospitalManageService.getHospital());
		} catch (manageException e) {
			this.failureMessage(e.getMessage(), request);
		} catch (Exception e) {
			this.failureMessage("getHospital数据异常", request);
		}
		return "hospital/index";
	}

	/**
	 * 直接转发到 hospital目录的create
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/hospital/create")
	public String createHospital(ModelMap model) {
		return "hospital/create";
	}


	/**
	 * 保存医院对象，无转发
	 * @param data
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/hospital/save",method=RequestMethod.POST)
	public String saveHospital(String data, HttpServletRequest request) {
		try {
			hospitalManageService.saveHospital(data);
		} catch (manageException e) {
			return this.failurePage(e.getMessage(),request);
		} catch (Exception e) {
			return this.failurePage("createHospital数据异常",request);
		}
		return this.successPage(null,request);
	}


	/**
	 * 查询部门列表后转发到 department目录的index
	 * @param model
	 * @param pageNum
	 * @param pageSize
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("/department/list")
	public String findDepartment(ModelMap model,
								 @RequestParam(defaultValue = "1") int pageNum,
								 @RequestParam(defaultValue = "10") int pageSize,
								 HttpServletRequest request,RedirectAttributes redirectAttributes) {
		try {
			//验证医院设置对象
			HospitalSet hospitalSet = hospitalManageMapper.selectById(1);
			//无医院code与签名key时，返回错误信息
			if(null == hospitalSet || StringUtils.isEmpty(hospitalSet.getHoscode()) || StringUtils.isEmpty(hospitalSet.getSignKey())) {
				this.failureMessage("先设置医院code与签名key", redirectAttributes);
				return "redirect:/hospitalSet/index";
			}
			//验证通过后查询该医院对象
			model.addAllAttributes(hospitalManageService.findDepartment(pageNum, pageSize));
		} catch (manageException e) {
			this.failureMessage(e.getMessage(), request);
		} catch (Exception e) {
			this.failureMessage("findDepartment数据异常", request);
		}
		return "department/index";
	}


	/**
	 * 直接转发到 department目录的create
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/department/create")
	public String create(ModelMap model) {
		return "department/create";
	}

	/**
	 * 保存部门对象，无转发
	 * @param data
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/department/save",method=RequestMethod.POST)
	public String save(String data, HttpServletRequest request) {
		try {
			hospitalManageService.saveDepartment(data);
		} catch (manageException e) {
			return this.failurePage(e.getMessage(),request);
		} catch (Exception e) {
			return this.failurePage("create数据异常",request);
		}
		return this.successPage(null,request);
	}

	/**
	 * 查询排班列表后转发到 schedule目录的index
	 * @param model
	 * @param pageNum
	 * @param pageSize
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("/schedule/list")
	public String findSchedule(ModelMap model,
								 @RequestParam(defaultValue = "1") int pageNum,
								 @RequestParam(defaultValue = "10") int pageSize,
							   HttpServletRequest request,RedirectAttributes redirectAttributes) {
		try {
			//验证医院设置对象
			HospitalSet hospitalSet = hospitalManageMapper.selectById(1);
			//无医院code与签名key时，返回错误信息
			if(null == hospitalSet || StringUtils.isEmpty(hospitalSet.getHoscode()) || StringUtils.isEmpty(hospitalSet.getSignKey())) {
				this.failureMessage("先设置医院code与签名key", redirectAttributes);
				return "redirect:/hospitalSet/index";
			}
			//验证通过后,查询所有排班对象并保存参数
			model.addAllAttributes(hospitalManageService.findSchedule(pageNum, pageSize));
		} catch (manageException e) {
			this.failureMessage(e.getMessage(), request);
		} catch (Exception e) {
			this.failureMessage("findSchedule数据异常", request);
		}
		return "schedule/index";
	}

	/**
	 * 直接转发
 	 * @param model
	 * @return
	 */
	@RequestMapping(value="/schedule/create")
	public String createSchedule(ModelMap model) {
		return "schedule/create";
	}

	/**
	 * 保存排班对象，无转发
	 * @param data
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/schedule/save",method=RequestMethod.POST)
	public String saveSchedule(String data, HttpServletRequest request) {
		try {
			//data = data.replaceAll("\r\n", "").replace(" ", "");
			hospitalManageService.saveSchedule(data);
		} catch (manageException e) {
			return this.failurePage(e.getMessage(),request);
		} catch (Exception e) {
			e.printStackTrace();
			return this.failurePage("createSchedule数据异常："+e.getMessage(),request);
		}
		return this.successPage(null,request);
	}


	/**
	 * 直接转发
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/hospital/createBatch")
	public String createHospitalBatch(ModelMap model) {
		return "hospital/createBatch";
	}


	/**
	 * 批量保存医院对象，无转发
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/hospital/saveBatch",method=RequestMethod.POST)
	public String saveBatchHospital(HttpServletRequest request) {
		try {
			hospitalManageService.saveBatchHospital();
		} catch (manageException e) {
			return this.failurePage(e.getMessage(),request);
		} catch (Exception e) {
			return this.failurePage("createHospitalBatch数据异常",request);
		}
		return this.successPage(null,request);
	}


	/**
	 * 删除部门，并转发到department目录的list
	 * @param model
	 * @param depcode
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value="/department/remove/{depcode}",method=RequestMethod.GET)
	public String removeDepartment(ModelMap model, @PathVariable String depcode, RedirectAttributes redirectAttributes) {
		hospitalManageService.removeDepartment(depcode);

		this.successMessage(null, redirectAttributes);
		return "redirect:/department/list";
	}


	/**
	 * 删除排班，并转发到 schedule目录的list
	 * @param model
	 * @param scheduleId
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value="/schedule/remove/{scheduleId}",method=RequestMethod.GET)
	public String removeSchedule(ModelMap model, @PathVariable String scheduleId, RedirectAttributes redirectAttributes) {
		hospitalManageService.removeSchedule(scheduleId);

		this.successMessage(null, redirectAttributes);
		return "redirect:/schedule/list";
	}

}

