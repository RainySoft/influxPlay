package com.rainysoft.device.publisher;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

/**
 * 1. Redis can only inject connection factory. Not Redis Template. 
 * 2. For Message, follow spring message pattern. Message Container invoke a Listener Adapter 
 * 
 * 
 * @author Lance
 *
 */
@SpringBootApplication
public class RedisPublisherApplication implements CommandLineRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
	
	public static void main(String[] args) {
		SpringApplication.run(RedisPublisherApplication.class, args);
	}

	public void run(String... args) throws Exception {
		Instant now = Instant.now();
		for (int i=0; i < 10000; i ++ ) {
			redisTemplate.convertAndSend("myChannel", "Hello world " + i );
		}
		System.exit(0);
	}

	
	@Bean
	public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return template;		
	}
	
	@Bean
	public RedisMessageListenerContainer init(RedisConnectionFactory connectionFactory,MessageListenerAdapter listenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("myChannel"));
		return container;		
	}
	
	@Bean
	public MessageListenerAdapter listenerAdapter(RedisReceiver receiver) {
		
		return new MessageListenerAdapter(receiver, "onReceive");
	}	
	
	@Bean
	public RedisReceiver receiver() {
		return new RedisReceiver();
	}
	
}
