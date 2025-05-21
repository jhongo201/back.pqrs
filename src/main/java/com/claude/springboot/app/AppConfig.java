package com.claude.springboot.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({ 
	@PropertySource(value="classpath:configFrontend.properties") 
})
public class AppConfig {

}
