package cz.muni.fi.cep.web.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 *
 * @author Jan Bruzl <bruzl@progenium.cz>
 */
@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailsService")
    UserDetailsService userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
            .antMatchers("/js/**", "/css/**", "/images/**", "/fonts/**", "/img/**", "/bower_components/**", "/icons/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	RequestMatcher csrfRequestMatcher = new RequestMatcher() {
            AntPathRequestMatcher matcherIndex = new AntPathRequestMatcher("/*");
            AntPathRequestMatcher matcherLogout = new AntPathRequestMatcher("/logout");
            @Override
            public boolean matches(HttpServletRequest request) {
                if (matcherIndex.matches(request) || matcherLogout.matches(request)) {
                    return false;
                }
                return request.getMethod().equals(HttpMethod.POST.toString());
            }
        };
    	
        http
                .authorizeRequests()
                .antMatchers("/","/index","/signup", "/signup-submit").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/#login")
                    .loginProcessingUrl("/login-submit")
                    .defaultSuccessUrl("/")
                    .failureUrl("/")
                    .permitAll()
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .permitAll()
                .and().csrf().requireCsrfProtectionMatcher(csrfRequestMatcher);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }

}
