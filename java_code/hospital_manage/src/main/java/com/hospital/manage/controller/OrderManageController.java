package com.hospital.manage.controller;

import com.hospital.manage.service.HospitalManageService;
import com.hospital.manage.service.OrderManageService;
import com.hospital.manage.util.HttpRequestHelper;
import com.hospital.manage.util.Result;
import com.hospital.manage.util.ResultCodeEnum;
import com.hospital.manage.util.manageException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 提供方法给其他模块使用post请求来调用
 */
@Api(tags = "订单管理接口")
@RestController
public class OrderManageController {

	@Autowired
	private OrderManageService orderManageService;

	@Autowired
	private HospitalManageService hospitalManageService;

	/**
	 * 预约下单
	 * @param request
	 * @return
	 */
	@PostMapping("/order/submitOrder")
	public Result AgreeAccountLendProject(HttpServletRequest request, HttpServletResponse response) {

		try {
			//Map<String, String[] 转为 Map<String, Object>
			Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
			if(!HttpRequestHelper.isSignEquals(paramMap, hospitalManageService.getSignKey())) {
				throw new manageException(ResultCodeEnum.SIGN_ERROR);
			}

			Map<String, Object> resultMap = orderManageService.submitOrder(paramMap);
			return Result.ok(resultMap);
		} catch (manageException e) {
			return Result.fail().message(e.getMessage());
		}
	}

	/**
	 * 更新支付状态
	 * @param request
	 * @return
	 */
	@PostMapping("/order/updatePayStatus")
	public Result updatePayStatus(HttpServletRequest request, HttpServletResponse response) {

		try {
			//Map<String, String[] 转为 Map<String, Object>
			Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
			if(!HttpRequestHelper.isSignEquals(paramMap, hospitalManageService.getSignKey())) {
				throw new manageException(ResultCodeEnum.SIGN_ERROR);
			}

			orderManageService.updatePayStatus(paramMap);
			return Result.ok();
		} catch (manageException e) {
			return Result.fail().message(e.getMessage());
		}
	}

	/**
	 * 更新取消预约状态
	 * @param request
	 * @return
	 */
	@PostMapping("/order/updateCancelStatus")
	public Result updateCancelStatus(HttpServletRequest request, HttpServletResponse response) {

		try {
			//Map<String, String[] 转为 Map<String, Object>
			Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
			if(!HttpRequestHelper.isSignEquals(paramMap, hospitalManageService.getSignKey())) {
				throw new manageException(ResultCodeEnum.SIGN_ERROR);
			}

			orderManageService.updateCancelStatus(paramMap);
			return Result.ok();
		} catch (manageException e) {
			return Result.fail().message(e.getMessage());
		}
	}
}

