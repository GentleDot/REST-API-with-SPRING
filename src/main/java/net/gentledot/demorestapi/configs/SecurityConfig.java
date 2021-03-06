package net.gentledot.demorestapi.configs;

import net.gentledot.demorestapi.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    // oauth Token 저장소
    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    // security 대상 제외하여 인가
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().mvcMatchers("/docs/index.html", "/swagger-ui/**", "/swagger**");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
    // security chain 을 타고 해당 request 인가
//        http.authorizeRequests()
//                .mvcMatchers("/docs/index.html").anonymous()
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());

//        http.formLogin()
//                .and()
//                .authorizeRequests()
//                    .mvcMatchers(HttpMethod.GET, "/api/**").authenticated();
//    }
}
