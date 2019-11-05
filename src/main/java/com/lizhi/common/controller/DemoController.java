package com.lizhi.common.controller;


import com.lizhi.common.entity.Order;
import com.lizhi.common.lock.impl.RedisDistributedLock;
import com.lizhi.common.lock.impl.RedisDistributedRedLock;
import com.lizhi.common.lock.impl.ZooKeeperDistributedLock;
import com.lizhi.common.service.OrderService;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;

import java.util.Date;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private OrderService orderService;
    
    
    
    @RequestMapping(value = "testPurchase.json", method = {RequestMethod.GET})
    @ResponseBody
    public String purchase() {
    	Runnable runnable = () -> {
            try {
                testPurchase();
            } catch(Exception e){
            	e.printStackTrace();
            }
        };

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(runnable);
            t.start();
        }
        return "testPurchase";

    }
    
    
    
    
    @RequestMapping(value = "redisLock.json", method = {RequestMethod.GET})
    @ResponseBody
    public String redisLock() {
    	Runnable runnable = () -> {
            RedisDistributedLock lock = null;
            String unLockIdentify = null;
            try {
                Jedis conn = new Jedis("10.138.228.199",34476);
                lock = new RedisDistributedLock(conn, "test");
                unLockIdentify = lock.acquire();
                System.out.println(Thread.currentThread().getName() + unLockIdentify);
                System.out.println(Thread.currentThread().getName() + "正在运行");
                testPurchase();
                System.out.println(Thread.currentThread().getName() + "正在运行");
            } finally {
                if (lock != null) {
                    lock.release(unLockIdentify);
                    System.out.println(Thread.currentThread().getName() + "释放锁");
                }
            }
        };

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(runnable);
            t.start();
        }
        return "redisLock";

    }

    @RequestMapping(value = "redisRedLock.json", method = {RequestMethod.GET})
    @ResponseBody
    public String redisRedLock() {
        Config config = new Config();
        //支持单机，主从，哨兵，集群等模式
        //此为哨兵模式
        config.useSentinelServers()
                .setMasterName("mymaster")
                .addSentinelAddress("127.0.0.1:26379","127.0.0.1:26479","127.0.0.1:26579")
                .setDatabase(0);
        Runnable runnable = () -> {
            RedisDistributedRedLock redisDistributedRedLock = null;
            RedissonClient redissonClient = null;
            try {
                redissonClient = Redisson.create(config);
                redisDistributedRedLock = new RedisDistributedRedLock(redissonClient, "stock_lock");
                redisDistributedRedLock.acquire();
                testPurchase();
                System.out.println(Thread.currentThread().getName() + "正在运行");
            } finally {
                if (redisDistributedRedLock != null) {
                    redisDistributedRedLock.release(null);
                }
                redissonClient.shutdown();
            }
        };
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(runnable);
            t.start();
        }
        return "redisRedLock";

    }
    
    
    @RequestMapping(value = "zookeeperLock.json", method = {RequestMethod.GET})
    @ResponseBody
    public String zookeeperLock() {
    	Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ZooKeeperDistributedLock lock = null;
                try {
                    lock = new ZooKeeperDistributedLock("127.0.0.1:2181", "test1");
                    lock.acquire();
                    testPurchase();
                    System.out.println(Thread.currentThread().getName() + "正在运行");
                } finally {
                    if (lock != null) {
                        lock.release(null);
                    }
                }
            }
        };

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(runnable);
            t.start();
        }
        return "zookeeperLock";

    }
    
    
    private void testPurchase(){
    	Order order = new Order();
        order.setProductId(1L);
        order.setCreateTime(new Date());
        order.setPnum(1);
        orderService.doOrder(order);
    }

}
