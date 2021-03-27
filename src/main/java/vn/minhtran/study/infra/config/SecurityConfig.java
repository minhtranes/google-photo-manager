package vn.minhtran.study.infra.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatcher(EndpointRequest.toAnyEndpoint())
			.authorizeRequests(r->r.anyRequest().permitAll());
		
		http.authorizeRequests()
//				.antMatchers("/data/**").permitAll()
//				.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
//				.anyRequest()
//				.authenticated()
				.and().oauth2Login();
	}

}