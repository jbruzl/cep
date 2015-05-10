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

import cz.muni.fi.cep.api.DTO.CepGroup;
import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.services.users.IdentityService;

/**
 *
 * @author Jan Bruzl
 */
@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailsService")
    UserDetailsService userDetailsService;
    
    @Autowired
    private IdentityService identityService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        
        //verify and set up user roles
        CepGroup admin = new CepGroup();
        admin.setCode("administrator");
        admin.setName("Administrátor");
        admin.setType("Aplikace");
        
        if(identityService.getGroupByCode(admin.getCode())==null)
        	identityService.createGroup(admin);
        admin = identityService.getGroupByCode(admin.getCode());
        
        String adminUserEmail = "admin@admin.cz";
		CepUser adminUser = identityService.getCepUserByEmail(adminUserEmail);
		if(adminUser==null){
        	adminUser = new CepUser();
        	adminUser.setFirstName("Admin");
        	adminUser.setLastName("Administrator");
        	adminUser.setMail(adminUserEmail);
        	adminUser.setPassword(passwordEncoder().encode("1234"));
        	
        	identityService.createUser(adminUser);
        	adminUser = identityService.getCepUserByEmail(adminUserEmail);
        }
		
		identityService.createMembership(adminUser, admin);
        
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
                .and()
                .authorizeRequests()
                .antMatchers("/odbery**", "/uzivatele**", "/skupiny**").access("hasRole('administrator')")                
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
