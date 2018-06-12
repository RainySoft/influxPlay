package com.rainysoft.influxCollector.repository;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class InfluxClientTest {

	public void testTest() {
		InfluxClient client = new InfluxClient();
		try {
			client.readTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void testParseInstant() {
		String dateStr="2018/06/12 16:54:37.123456789";
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.nnnnnnnnn").withZone(ZoneId.systemDefault());
		Instant time = formatter.parse(dateStr, Instant::from);
		System.out.println(time);
	}
	
}
