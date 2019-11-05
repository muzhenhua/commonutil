package com.lizhi.common.dao;

import java.util.HashMap;

import com.lizhi.common.entity.Product;
public interface ProductDao {
	Product selectProductById(Long id);
	void reduceNum(HashMap<String,Integer> hm);
}
