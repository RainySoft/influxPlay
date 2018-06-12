package com.rainysoft.influxCollector.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
		BatchPoints batchPoints = BatchPoints.database("TESTDB").retentionPolicy("mypolicy").build();
		for (int i = 0; i < 500000; i++) {
			Long microseconds = Instant.now().getLong(ChronoField.MICRO_OF_SECOND);
			Point p = Point.measurement("demo").tag("tag", "tagValue").addField("seq", i)
					.addField("testField", random.nextFloat()).addField("cpu", Math.round(random.nextFloat() * 100))
					.build();
			influx.write("TESTDB", "mypolicy", p);
			Thread.sleep(1L);
		}
		// influx.write(batchPoints);
		influx.close();
	}

	public void readTest() throws IOException {
		InfluxDB influx = connect();
		influx.setDatabase("TESTDB");
		influx.enableBatch(2000, 100, TimeUnit.NANOSECONDS);
		influx.enableGzip();
		List<Map<String, String>> results;
		BufferedReader reader = Files.newBufferedReader(Paths.get("testdata.csv"));
		// first line to get entryKeySet
		List<String> keys = Arrays.asList(reader.readLine().split(","));
		LinkedList<String> keySet = keys.stream().collect(Collectors.toCollection(LinkedList::new));
		while (reader.ready()) {
			LinkedList<String> values = Arrays.asList(reader.readLine().split(",")).stream()
					.collect(Collectors.toCollection(LinkedList::new));
			Map<String, Object> map = new LinkedHashMap<>();
			for (int i = 0; i < keySet.size(); i++) {
				if (!keySet.get(i).equals("timeStamp")) {
					map.put(keySet.get(i), Double.parseDouble(values.get(i)));
				} else {
					map.put(keySet.get(i), values.get(i));
				}

			}
			// covert to Shanghai time
			Instant time = Instant.parse((String) map.get("timeStamp")).atZone(ZoneId.of("Asia/Shanghai")).toInstant();
			Long nanoTime = TimeUnit.SECONDS.toNanos(time.getEpochSecond()) + time.getNano();
			Point p = Point.measurement("csv3").fields(map)
					.time(nanoTime, TimeUnit.NANOSECONDS).build();
			influx.write("TESTDB", "mypolicy", p);
		}
		influx.close();

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
