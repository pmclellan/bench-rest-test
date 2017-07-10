package com.pm.bench.resttest.config;

import java.text.MessageFormat;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan( "com.pm.bench.resttest" )
public class SpringConfig
{
	@Bean( "urlGenerator" )
	public Function<Integer, String> getUrlGenerator( @Value( "${pageRequest.url}" ) final String urlTemplate )
	{
		return (i) -> MessageFormat.format( urlTemplate, i );
	}
	
	@Bean
	public RestTemplate getRestTemplate()
	{
		return new RestTemplate();
	}
}