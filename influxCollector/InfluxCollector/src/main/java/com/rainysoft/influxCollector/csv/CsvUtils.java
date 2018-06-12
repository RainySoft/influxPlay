package com.rainysoft.influxCollector.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvUtils {

	private static final Logger log = LoggerFactory.getLogger(CsvUtils.class);

	private CsvUtils() {

	}

	/**
	 * Get A CSV records from CSV file
	 * 
	 * @param csvPath
	 * @return
	 */
	public static List<CSVRecord> readCsv(String csvPath) {
		List<CSVRecord> recordsList = null;
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(csvPath), StandardCharsets.UTF_8);
				CSVParser parser = new CSVParser(reader, CSVFormat.RFC4180.withFirstRecordAsHeader())) {
			recordsList = StreamSupport.stream(parser.spliterator(), false).collect(Collectors.toList());
		} catch (IOException e) {
			log.error("Cannot read CSV file [{}]", csvPath, e);
		}
		return recordsList;
	}

	public static Point fromCsvRecord(CSVRecord rec) {			
		Builder b  = Point.measurement("measurement");
		Instant time = Instant.parse((String) rec.get("timeStamp")).atZone(ZoneId.of("Asia/Shanghai")).toInstant();
		Long nanoTime = TimeUnit.SECONDS.toNanos(time.getEpochSecond()) + time.getNano();
		b.time(nanoTime, TimeUnit.NANOSECONDS);
		Map<String,String> m = rec.toMap();
		for (Entry<String,String> e : m.entrySet()) {
			if (e.getKey().equals("timeStamp")) 
				continue; 
			if (e.getKey().equals("key")) {
				b.tag("key", e.getValue());
			}				
			b.addField(e.getKey(), Double.parseDouble(e.getValue()));
		}
		return b.build();
	}

}
