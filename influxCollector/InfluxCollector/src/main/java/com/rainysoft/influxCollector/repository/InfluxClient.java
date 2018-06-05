package com.rainysoft.influxCollector.repository;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfluxClient {

	private final static Logger logger = LoggerFactory.getLogger(InfluxClient.class);

	private InfluxDB connect() {
		return InfluxDBFactory.connect("http://10.1.1.4:8086", "lance", "lance");
	}

	private boolean pingServer(InfluxDB influxDB) {
		try {
			// Ping and check for version string
			Pong response = influxDB.ping();
			if (response.getVersion().equalsIgnoreCase("unknown")) {
				logger.error("Error pinging server.");
				return false;
			} else {
				logger.info("Database version: {}", response.getVersion());
				return true;
			}
		} catch (InfluxDBIOException idbo) {
			logger.error("Exception while pinging database: ", idbo);
			return false;
		}
	}
	
	private void writePoint() throws InterruptedException {
		InfluxDB influx = connect();
		influx.setDatabase("TESTDB");
		influx.enableBatch();
		influx.enableGzip();
		
		Random random = new Random();
        BatchPoints batchPoints = BatchPoints
                .database("TESTDB")
                .retentionPolicy("mypolicy")
                .build();		
		for (int i = 0; i <500000 ; i ++) {
			Long microseconds = Instant.now().getLong(ChronoField.MICRO_OF_SECOND);
			Point p = Point
					.measurement("demo")
					.tag("tag", "tagValue")
					.addField("seq", i)
					.addField("testField", random.nextFloat())
					.addField("cpu", Math.round(random.nextFloat()*100)).build();
			influx.write("TESTDB", "mypolicy", p);
			Thread.sleep(1L);
		}
//		influx.write(batchPoints);
		influx.close();
	}
	
	
	private void readTest() {
		
	}
	
	public boolean test() {
		try {
			writePoint();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pingServer(connect());
	}

}
