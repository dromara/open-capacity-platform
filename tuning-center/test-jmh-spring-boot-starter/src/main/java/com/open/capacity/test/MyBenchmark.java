package com.open.capacity.test;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.SneakyThrows;

/**
 * JMH Visual Chart：http://deepoove.com/jmh-visual-chart/
 * JMH Visualizer：https://jmh.morethan.io/
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Benchmark)
public class MyBenchmark {

	private ObjectMapper objectMapper;
	private Gson gson;
	
	
	@Setup
	public void setup() {
		objectMapper = new ObjectMapper();
		gson = new GsonBuilder().create();
	}

	@Benchmark
	@SneakyThrows
	public void testObjectHashMapper() {
		HashMap map = new HashMap();
		map.put("hello", "world");
		objectMapper.writeValueAsString(map);
	}
	
	@Benchmark
	@SneakyThrows
	public void testGson() {
		HashMap map = new HashMap();
		map.put("hello", "world");
		gson.toJson(map);
	}
 

	public static void main(String[] args) throws RunnerException {
		Options options = new OptionsBuilder().include(MyBenchmark.class.getSimpleName()).resultFormat(ResultFormatType.JSON).build();
		new Runner(options).run();
	}
}