package com.taotao.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.TaotaoResult;
import com.taotao.common.utils.ExceptionUtil;
import com.taotao.order.pojo.Order;
import com.taotao.order.service.OrderService;

@Controller

public class OrderController {
	@Autowired
	private OrderService OrderService;
	@RequestMapping(value="/create",method=RequestMethod.POST)
	@ResponseBody
	public Object createorder(@RequestBody Order order ){
		try {
			return this.OrderService.addOrder(order, order.getOrderItems(), order.getOrderShipping());
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
			
		}
		
		
	}
	@RequestMapping("/index")
	public String  index(){
		return "index";
		
	}
}
