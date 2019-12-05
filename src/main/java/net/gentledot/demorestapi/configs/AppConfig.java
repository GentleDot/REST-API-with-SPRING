package net.gentledot.demorestapi.configs;

import net.gentledot.demorestapi.accounts.Account;
import net.gentledot.demorestapi.accounts.AccountRole;
import net.gentledot.demorestapi.accounts.AccountService;
import net.gentledot.demorestapi.common.AppProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // test용 user 생성
    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Autowired
            AppProperties properties;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account admin = Account.builder()
                        .email(properties.getAdminUsername())
                        .password(properties.getAdminPassword())
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();
                this.accountService.saveAccount(admin);

                Account user = Account.builder()
                        .email(properties.getUsername())
                        .password(properties.getUserPassword())
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();
                this.accountService.saveAccount(user);
            }
        };
    }

}
