package com.taotao.order.service;

import java.util.List;

import com.taotao.common.TaotaoResult;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;

public interface OrderService {
	TaotaoResult addOrder(TbOrder order,List<TbOrderItem> orderitems,TbOrderShipping ordershipping);
}
