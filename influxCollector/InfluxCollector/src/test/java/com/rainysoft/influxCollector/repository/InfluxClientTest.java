package com.rainysoft.influxCollector.repository;

import static org.junit.Assert.*;

import org.junit.Test;

public class InfluxClientTest {

	@Test
	public void testTest() {
		InfluxClient client = new InfluxClient();
		assertTrue(client.test());
		
	}

}
