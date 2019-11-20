package net.gentledot.demorestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;

@SpringBootApplication
public class DemoRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoRestApiApplication.class, args);
    }


    // Charset = UTF-8 설정
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public Filter characterEncodingFilter(){
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        return characterEncodingFilter;
    }
}
