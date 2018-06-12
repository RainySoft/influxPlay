package com.rainysoft.influxCollector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDataGenerator {
	private static Logger logger = LoggerFactory.getLogger(TestDataGenerator.class); 
	List<Map<String, String>> dataHolder;
	Set<String> keys;
	Random ran;

	public static void main(String[] args) {
		TestDataGenerator g = new TestDataGenerator();
		try {
			g.writeData(g.genData());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeData(List<Map<String, String>> records) throws IOException {
		Path path = Paths.get("testdata.csv");
		BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"));
		StringBuffer buff = new StringBuffer();
		Boolean bFirst = true;
		for (Map<String,String> rec:records) {			
			if (bFirst) {
				rec.keySet().forEach(k -> buff.append(k).append(","));
				buff.deleteCharAt(buff.length()-1);
				buff.append(System.lineSeparator());
				bFirst = false; 
				
			}
			rec.values().forEach(v -> buff.append(v).append(","));
			buff.deleteCharAt(buff.length()-1);
			buff.append(System.lineSeparator());
		}
		writer.write(buff.toString());
		writer.flush();
		writer.close();				
		logger.info("gen data complete");
	}

	public List<Map<String, String>> genData() {
		Instant start = Instant.now();
		long nanostart = System.nanoTime();
		List<Map<String, String>> l = new LinkedList<>();
		for (int i = 0; i < 5000000; i++) {
			Map<String, String> a = new LinkedHashMap<>();
			long eclipse = System.nanoTime()-nanostart; 
			a.put("timeStamp", start.plusNanos(eclipse).toString());
			a.put("key", "123456");
			getKeys().forEach(k -> a.put(k, randomValue()));
			l.add(a);
		}
		
		logger.info("Start time:[{}] || End time: [{}]",start,Instant.now());
		logger.info("Start time:[{}] || End time: [{}]", nanostart,System.nanoTime()-nanostart);
		return l;
	}

	public String randomValue() {
		if (ran == null)
			ran = new Random();
		Double result = (ran.nextDouble()) * 1000;
		return result.toString();
	}

	public Set<String> getKeys() {
		if (keys != null)
			return keys;
		keys = Stream.of("field1", "field2", "field3", "field4","field5","field6").collect(Collectors.toCollection(LinkedHashSet::new));
		return keys;

	}

}
