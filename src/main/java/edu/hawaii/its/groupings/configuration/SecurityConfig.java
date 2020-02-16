package edu.hawaii.its.groupings.configuration;

import javax.annotation.PostConstruct;

import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Saml11TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetailsSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.util.Assert;

import edu.hawaii.its.groupings.access.UserBuilder;
import edu.hawaii.its.groupings.access.UserDetailsServiceImpl;

@ComponentScan(basePackages = "edu.hawaii.its")
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${url.base}")
    private String urlBase;

    @Value("${cas.login.url}")
    private String casLoginUrl;

    @Value("${cas.logout.url}")
    private String casLogoutUrl;

    @Value("${cas.main.url}")
    private String casMainUrl;

    @Value("${cas.saml.tolerance}")
    private long casSamlTolerance;

    @Value("${cas.send.renew}")
    private boolean casSendRenew;

    @Autowired
    private UserBuilder userBuilder;

    @PostConstruct
    public void init() {
        Assert.hasLength(urlBase, "property 'urlBase' is required");
        Assert.hasLength(casLoginUrl, "property 'casLoginUrl' is required");
        Assert.hasLength(casLogoutUrl, "property 'casLogoutUrl' is required");
    }

    @Bean
    public ProxyGrantingTicketStorage proxyGrantingTicketStorage() {
        return new ProxyGrantingTicketStorageImpl();
    }

    @Bean
    public SingleSignOutFilter singleLogoutFilter() {
        return new SingleSignOutFilter();
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(urlBase + "/login/cas");
        serviceProperties.setSendRenew(casSendRenew);
        serviceProperties.setAuthenticateAllArtifacts(true);

        return serviceProperties;
    }

    @Bean
    public CasAuthenticationEntryPoint casProcessingFilterEntryPoint() {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(casLoginUrl);
        entryPoint.setServiceProperties(serviceProperties());
        return entryPoint;
    }

    @Bean
    public LogoutFilter logoutFilter() {
        return new LogoutFilter(casLogoutUrl, new SecurityContextLogoutHandler());
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setKey("an_id_for_this_auth_provider_only");
        provider.setAuthenticationUserDetailsService(authenticationUserDetailsService());
        provider.setServiceProperties(serviceProperties());

        Saml11TicketValidator ticketValidator = new Saml11TicketValidator(casMainUrl);
        ticketValidator.setTolerance(casSamlTolerance);
        provider.setTicketValidator(ticketValidator);

        return provider;
    }

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> authenticationUserDetailsService() {
        return new UserDetailsServiceImpl(userBuilder);
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());

        SimpleUrlAuthenticationFailureHandler authenticationFailureHandler =
                new SimpleUrlAuthenticationFailureHandler();
        authenticationFailureHandler.setDefaultFailureUrl("/home");
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);

        SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        authenticationSuccessHandler.setAlwaysUseDefaultTargetUrl(false);
        authenticationSuccessHandler.setDefaultTargetUrl("/home");
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);

        ServiceAuthenticationDetailsSource authenticationDetailsSource =
                new ServiceAuthenticationDetailsSource(serviceProperties());
        filter.setAuthenticationDetailsSource(authenticationDetailsSource);

        filter.setProxyGrantingTicketStorage(proxyGrantingTicketStorage());
        filter.setProxyReceptorUrl("/receptor");
        filter.setServiceProperties(serviceProperties());

        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilter(casAuthenticationFilter());
        http.exceptionHandling()
                .authenticationEntryPoint(casProcessingFilterEntryPoint());
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/api/**").hasRole("UH")
                .antMatchers("/css/**").permitAll()
                .antMatchers("/fonts/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/javascript/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/home").permitAll()
                .antMatchers("/info").permitAll()
                .antMatchers("/feedback").hasRole("UH")
                .antMatchers("/campus").hasRole("UH")
                .antMatchers("/campuses").hasRole("UH")
                .antMatchers("/denied").permitAll()
                .antMatchers("/404").permitAll()
                .antMatchers("/login").hasRole("UH")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/groupings/**").hasAnyRole("ADMIN", "OWNER")
                .antMatchers("/memberships/**").hasRole("UH")
                .anyRequest().authenticated()
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(casAuthenticationProvider());
    }

}
