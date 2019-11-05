package com.lizhi.common.lock.test;

import java.util.Date;

import com.lizhi.common.CommonutilApplication;
import com.lizhi.common.entity.Order;
import com.lizhi.common.lock.impl.RedisDistributedLock;
import com.lizhi.common.service.OrderService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.swing.JEditorPane;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;


/**
 * @author zhi.li
 * @Description
 * @created 2019/1/1 21:42
 */
public class RedisDistributedLockTest {
    static int n = 5;
    public static void secskill() {
    	if(n <= 0) {
            System.out.println("抢购完成");
            return;
        }

        System.out.println(--n);
    }

    public static void main(String[] args) {
    	SpringApplication.run(CommonutilApplication.class, args);
		ApplicationContext context = SpringUtil.getApplicationContext();
		OrderService orderService = context.getBean(OrderService.class);
    	
    	
        Runnable runnable = () -> {
            RedisDistributedLock lock = null;
            String unLockIdentify = null;
            try {
                Jedis conn = new Jedis("10.138.228.199",34476);
                lock = new RedisDistributedLock(conn, "test1");
                unLockIdentify = lock.acquire();
                System.out.println(unLockIdentify);
                testpurchase(orderService);
            } finally {
                if (lock != null) {
                    lock.release(unLockIdentify);
                }
            }
        };

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(runnable);
            t.start();
        }
    }
    
    public static void testpurchase(OrderService orderService){
        Order order = new Order();
        order.setProductId(1L);
        order.setCreateTime(new Date());
        order.setPnum(1);
        orderService.doOrder(order);
    }
}
