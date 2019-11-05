package com.lizhi.common.service.impl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lizhi.common.dao.OrderDao;
import com.lizhi.common.dao.ProductDao;
import com.lizhi.common.entity.Order;
import com.lizhi.common.entity.Product;
import com.lizhi.common.service.OrderService;

@Component("orderService")
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderDao orderDao;

	@Autowired
	private ProductDao productDao;
	
	
	@Transactional
	public boolean doOrder(Order o) {
    	//获取当前的产品库存数量
    	Product nowp = productDao.selectProductById(o.getProductId());
    	if(nowp.getSize()>=o.getPnum()){
    		/*try {
    			Thread.sleep(30000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}*/
    		orderDao.saveOrder(o);
    		HashMap<String,Integer> hm = new HashMap<String,Integer>();
    		hm.put("nums", o.getPnum());
    		hm.put("id",nowp.getId());
    		productDao.reduceNum(hm);
    		System.out.println("库存充足，购买成功");
    	}else{
    		System.out.println("库存不足，购买失败");
    		return false;
    	}
		return true;
	}
	
	
	
	
}
