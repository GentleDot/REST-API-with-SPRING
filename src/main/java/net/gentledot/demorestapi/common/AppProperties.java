package net.gentledot.demorestapi.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "my-app")
@Getter
@Setter
public class AppProperties {
    @NotEmpty
    private String adminUsername;
    @NotEmpty
    private String adminPassword;
    @NotEmpty
    private String Username;
    @NotEmpty
    private String UserPassword;
    @NotEmpty
    private String testUsername;
    @NotEmpty
    private String testUserPassword;
    @NotEmpty
    private String clientId;
    @NotEmpty
    private String clientSecret;


}
