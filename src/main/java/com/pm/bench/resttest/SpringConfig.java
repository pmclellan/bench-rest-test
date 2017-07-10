package com.pm.bench.resttest;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan
public class SpringConfig
{
	@Bean( "urlGenerator" )
	public Function<Integer, String> getUrlGenerator()
	{
		return (i) -> "http://resttest.bench.co/transactions/" + i + ".json";
	}
	
	@Bean
	public RestTemplate getRestTemplate()
	{
		return new RestTemplate();
	}
}