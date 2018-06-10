package com.rainysoft.device.publisher;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisPublisher {
	
	@Autowired 
	RedisTemplate<String,Object> redisTemplate;
	
	public void sendMessage(String message) {
		redisTemplate.convertAndSend("myChannel", message );
	}
}
