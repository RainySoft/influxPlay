package com.rainysoft.influxCollector.model;

import java.util.Objects;

import lombok.ToString;
import lombok.Value;

@Value
public class MeasureDataRecord {
	private String	equipmentId;
	private String	measurem;

	public static MeasureDataRecord fromCsvLine(String line) {
		Objects.requireNonNull(line, "line cannot be empty");
		String[] s = line.split(",");
		MeasureDataRecord r = new MeasureDataRecord(s[0], s[1]);
		return r;
	}
}
