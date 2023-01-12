package com.open.capacity.uaa.config;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.hdiv.config.annotation.EnableHdivWebSecurity;
import com.open.capacity.common.config.DefaultPasswordConfig;
import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.properties.TenantProperties;
import com.open.capacity.uaa.cas.CasTicketSecurityConfig;
import com.open.capacity.uaa.common.token.CustomWebAuthenticationDetails;
import com.open.capacity.uaa.face.FaceIdAuthenticationSecurityConfig;
import com.open.capacity.uaa.filter.PreSetTenantProcessFilter;
import com.open.capacity.uaa.loginsso.SsoAuthenticationProvider;
import com.open.capacity.uaa.mobile.MobileAuthenticationSecurityConfig;
import com.open.capacity.uaa.openid.OpenIdAuthenticationSecurityConfig;
import com.open.capacity.uaa.password.PasswordAuthenticationProvider;
import com.open.capacity.uaa.service.impl.UserDetailServiceFactory;
import com.open.capacity.uaa.sms.SmsAuthenticationSecurityConfig;
import com.open.capacity.uaa.tenant.TenantAuthenticationSecurityConfig;
import com.open.capacity.uaa.tenant.TenantUsernamePasswordAuthenticationFilter;

/**
 * spring security配置
 * @author owen 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51 2017年10月16日
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Configuration
@EnableWebSecurity//(debug = true)
@EnableHdivWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(DefaultPasswordConfig.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Resource
	private UserDetailServiceFactory userDetailsServiceFactory;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Resource
	private LogoutHandler logoutHandler;

	@Resource
	private LogoutSuccessHandler logoutSuccessHandler;

	@Autowired
	private OpenIdAuthenticationSecurityConfig openIdAuthenticationSecurityConfig;

	@Autowired
	private MobileAuthenticationSecurityConfig mobileAuthenticationSecurityConfig;
	
	@Autowired
	private SmsAuthenticationSecurityConfig smsAuthenticationSecurityConfig;
	
	@Autowired
	private FaceIdAuthenticationSecurityConfig faceIdAuthenticationSecurityConfig;


	@Autowired(required = false)
	private CasTicketSecurityConfig casTicketSecurityConfig;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TenantAuthenticationSecurityConfig tenantAuthenticationSecurityConfig;

	@Autowired
	private TenantProperties tenantProperties;

	@Autowired
	private AuthenticationDetailsSource<HttpServletRequest, CustomWebAuthenticationDetails> authenticationDetailsSource;

	// 自定义sso
	@Autowired
	private SsoAuthenticationProvider ssoAuthenticationProvider;

	/**
	 * 这一步的配置是必不可少的，否则SpringBoot会自动配置一个AuthenticationManager,覆盖掉内存中的用户
	 * 
	 * @return 认证管理对象
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public TenantUsernamePasswordAuthenticationFilter tenantAuthenticationFilter(
			AuthenticationManager authenticationManager) {
		TenantUsernamePasswordAuthenticationFilter filter = new TenantUsernamePasswordAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManager);
		filter.setFilterProcessesUrl(SecurityConstants.OAUTH_LOGIN_PRO_URL);
		filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
		filter.setAuthenticationFailureHandler(
				new SimpleUrlAuthenticationFailureHandler(SecurityConstants.LOGIN_FAILURE_PAGE));
		filter.setAuthenticationDetailsSource(authenticationDetailsSource);
		return filter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().anyRequest().permitAll().and().logout().logoutUrl(SecurityConstants.LOGOUT_URL)
				.logoutSuccessHandler(logoutSuccessHandler).addLogoutHandler(logoutHandler).clearAuthentication(true)
				.and().apply(openIdAuthenticationSecurityConfig).and().apply(mobileAuthenticationSecurityConfig).and()
				.apply(smsAuthenticationSecurityConfig).and()
				.apply(faceIdAuthenticationSecurityConfig).and()
				.apply(casTicketSecurityConfig).and().httpBasic().disable()
				.addFilterBefore(new PreSetTenantProcessFilter(), UsernamePasswordAuthenticationFilter.class).csrf()
				.disable()
				.headers().xssProtection().and()
				// 解决不允许显示在iframe的问题
				.frameOptions().disable().cacheControl();

		if (tenantProperties.getEnable()) {
			// 解决不同租户单点登录时角色没变化
			http.formLogin().loginPage(SecurityConstants.LOGIN_PAGE).and()
					.addFilterAt(tenantAuthenticationFilter(authenticationManager),
							UsernamePasswordAuthenticationFilter.class)
					.apply(tenantAuthenticationSecurityConfig);
		} else {
			http.formLogin().loginPage(SecurityConstants.LOGIN_PAGE)
					.loginProcessingUrl(SecurityConstants.OAUTH_LOGIN_PRO_URL)
					.successHandler(authenticationSuccessHandler)
					.authenticationDetailsSource(authenticationDetailsSource);
		}
		// 自定义单点登录模式
		http.authenticationProvider(ssoAuthenticationProvider);

		// 授权码模式单独处理，需要session的支持，此模式可以支持所有oauth2的认证
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
	}

	/**
	 * 全局用户信息
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) {
		PasswordAuthenticationProvider provider = new PasswordAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder);
		provider.setUserDetailsServiceFactory(userDetailsServiceFactory);
		auth.authenticationProvider(provider);
	}

}
