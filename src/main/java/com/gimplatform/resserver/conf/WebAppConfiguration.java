package com.gimplatform.resserver.conf;

import java.io.File;

import javax.servlet.MultipartConfigElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 配置站点相关设置
 * @author zzd
 */
@Configuration
public class WebAppConfiguration {

    private static final Logger logger = LogManager.getLogger(WebAppConfiguration.class);

    @Value("${resourceServer.uploadFilePath}")
    private String uploadFilePath;

    /**
     * 配置跨域访问
     * @return
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        logger.info("Initializing CORS Configuration ");
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowCredentials(true).allowedHeaders("*").allowedMethods("*").allowedOrigins("*");
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                logger.info("设置文件上传资源路径:" + uploadFilePath);
                registry.addResourceHandler("/res/**").addResourceLocations("file:" + uploadFilePath);
                super.addResourceHandlers(registry);
            }
        };
    }

    /**
     * 设置文件上传配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //处理临时文件
        String location = System.getProperty("user.dir") + "/data/tmp";
        File tmpFile = new File(location);
        if (!tmpFile.exists()) {
            tmpFile.mkdirs();
        }
        factory.setLocation(location);
        return factory.createMultipartConfig();
    }
}