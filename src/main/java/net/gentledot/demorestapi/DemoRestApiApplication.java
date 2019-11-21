package net.gentledot.demorestapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
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


    // Charset = UTF-8 설정 (WebMvcTest)
    // The bean 'characterEncodingFilter', defined in class path resource [org/springframework/boot/autoconfigure/web/servlet/HttpEncodingAutoConfiguration.class], could not be registered. A bean with that name has already been defined in net.gentledot.demorestapi.DemoRestApiApplication and overriding is disabled.
    // Consider renaming one of the beans or enabling overriding by setting spring.main.allow-bean-definition-overriding=true
    // ****** Spring boot 2.1 이후부터는 bean definition overriding이 false ******
    // spring.main.allow-bean-definition-overriding=true 임에도 해당 bean 설정이 MockHttpServletResponse에 반영되지 않음.
    // 해당 옵션을 주어 charset 설정은 bean에서 설정하도록 하면 통과는 되지만....
    // @SpringBootApplication(exclude = HttpEncodingAutoConfiguration.class)
    // application.properties 상에서 설정으로 해결하였음.
    /*
    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        return characterEncodingFilter;
    }
    */

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
