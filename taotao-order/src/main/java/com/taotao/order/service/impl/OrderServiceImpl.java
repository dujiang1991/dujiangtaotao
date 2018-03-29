package com.taotao.order.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.TaotaoResult;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.dao.impl.JedisClientCluster;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	@Autowired
	private JedisClientCluster jedisClientCluster;
	@Value("${ORDER_PRY_KEY}")
	private String ORDER_PRY_KEY;
	@Value("${ORDER_PRY_KEY_INIT}")
	private String ORDER_PRY_KEY_INIT;
	@Value("${ORDER_ZI_PRY_KEY}")
	private String ORDER_ZI_PRY_KEY;

	@Override
	public TaotaoResult addOrder(TbOrder order, List<TbOrderItem> orderitems, TbOrderShipping ordershipping) {
		// 补全tborder数据
		// 主键、从redis中取incr
		String str = this.jedisClientCluster.get(ORDER_PRY_KEY);
		if (StringUtils.isBlank(str)) {
			this.jedisClientCluster.set(ORDER_PRY_KEY, ORDER_PRY_KEY_INIT);
		}
		Long orderId = this.jedisClientCluster.incr(ORDER_PRY_KEY);
		order.setOrderId(orderId.toString());
		// 状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		order.setStatus(1);
		// 0：未评价 1：已评价
		order.setBuyerRate(0);
		// 设置日期
		Date date = new Date();
		order.setCreateTime(date);
		order.setUpdateTime(date);
		// 向tbOrder中插入数据
		this.orderMapper.insert(order);

		// 订单子表数据插入(明细表)
		for (TbOrderItem tbOrderItem : orderitems) {
			// 从redis中获取主键
			Long orderitemID = this.jedisClientCluster.incr(ORDER_ZI_PRY_KEY);
			tbOrderItem.setId(orderitemID.toString());
			tbOrderItem.setOrderId(orderId.toString());
			// 详表中插入
			this.orderItemMapper.insert(tbOrderItem);
		}
		// 补全物流表
		ordershipping.setOrderId(orderId.toString());
		ordershipping.setCreated(date);
		ordershipping.setUpdated(date);
		this.orderShippingMapper.insert(ordershipping);

		return TaotaoResult.ok(orderId);
	}

}
