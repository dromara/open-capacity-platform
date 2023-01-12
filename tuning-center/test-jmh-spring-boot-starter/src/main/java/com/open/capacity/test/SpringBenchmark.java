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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.open.capacity.JmhTestApp;
import com.open.capacity.dao.TestDao;

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
public class SpringBenchmark {
	private ConfigurableApplicationContext context;
	private TestDao testDao ;

	@Setup
    public void init() {
        // 这里的WebApplication.class是项目里的spring boot启动类
        context = SpringApplication.run(JmhTestApp.class);
        // 获取需要测试的bean
        testDao= context.getBean(TestDao.class);
    }

    @TearDown
    public void down() {
        context.close();
    } 
    
    @Benchmark
	@SneakyThrows
	public void testObjectHashMapper() {
    	testDao.add(true);
	}
	
 

	public static void main(String[] args) throws RunnerException {
		Options options = new OptionsBuilder().include(SpringBenchmark.class.getSimpleName()).resultFormat(ResultFormatType.JSON).build();
		new Runner(options).run();
	}
}