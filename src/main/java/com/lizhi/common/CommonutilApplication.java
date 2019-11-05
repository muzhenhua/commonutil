package com.lizhi.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.lizhi.common.dao")
@EnableScheduling
public class CommonutilApplication {
	

    public static void main(String[] args) {
    	
    	SpringApplication.run(CommonutilApplication.class, args);
		
    }
}

