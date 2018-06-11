package com.rainysoft.influxCollector.repository;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class InfluxClientTest {

	@Test
	public void testTest() {
		InfluxClient client = new InfluxClient();
		try {
			client.readTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
