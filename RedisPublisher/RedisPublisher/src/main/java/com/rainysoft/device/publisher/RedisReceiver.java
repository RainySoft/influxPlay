package com.rainysoft.device.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisReceiver {
	private final static Logger logger = LoggerFactory.getLogger(RedisReceiver.class);
	
	
	public void onReceive(String message) {
		logger.info("Receive Message:{}",message);
	}

}
