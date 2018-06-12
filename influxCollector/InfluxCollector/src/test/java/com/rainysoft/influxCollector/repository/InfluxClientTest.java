package com.rainysoft.influxCollector.repository;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVRecord;
import org.influxdb.dto.Point;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rainysoft.influxCollector.csv.CsvUtils;
import com.rainysoft.influxCollector.model.MeasureDataRecord;

public class InfluxClientTest {
	private final static Logger logger = LoggerFactory.getLogger(InfluxClientTest.class);
	public void testTest() {
		InfluxClient client = new InfluxClient();
		try {
			client.readTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testParseInstant() {
		String dateStr = "2018/06/12 16:54:37.123456789";
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.nnnnnnnnn").withZone(ZoneId.systemDefault());
		Instant time = formatter.parse(dateStr, Instant::from);
		System.out.println(time);
		System.out.println(time.getEpochSecond());
		System.out.println(TimeUnit.SECONDS.toNanos(time.getEpochSecond())+time.getNano());		
		
	}

	public void testCsvMap() {
		String csv = "IBM,450\nMicrosfot,WindowsXP";
		System.out.println(csv);
		List<MeasureDataRecord> l = Stream.of(csv.split("\n")).map(MeasureDataRecord::fromCsvLine).collect(Collectors.toList());
		l.forEach(m -> System.out.println("measurement data " + m.toString()));	
	}
	
	@Test	
	public void testCsvParser() {
		long start = System.currentTimeMillis();
		InfluxClient client = new InfluxClient();
		client.readCsvToInflux("testdata.csv");
		long duration = System.currentTimeMillis() - start; 
		logger.info("spend time: {} milli seconds",duration);
	}
}
