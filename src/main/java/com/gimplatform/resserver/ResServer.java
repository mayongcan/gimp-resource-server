package com.gimplatform.resserver;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 服务启动主函数
 * @author zzd
 */
@SpringBootApplication
@EnableEurekaClient
public class ResServer {
    
    public static void main(String[] args) {
    	SpringApplication app = new SpringApplication(ResServer.class);
    	app.setBannerMode(Banner.Mode.OFF);
    	app.run(args);
    }
}
