package com.kafkamgt.uiapi.config;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@ConditionalOnProperty(name = "kafkawize.enable.sso", havingValue = "true")
@EnableWebSecurity
public class SecurityConfigSSO extends WebSecurityConfigurerAdapter {

  private void shutdownApp() {
    // ((ConfigurableApplicationContext) contextApp).close();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    String[] staticResources = {
      "/logout**",
      "/login**",
      "/assets/**",
      "/js/**",
      "/oauthLogin",
      "/login/oauth2/**",
      "/lib/**",
      "/register**",
      "/terms**",
      "/registrationReview**",
      "/forgotPassword",
      "/getDbAuth",
      "/resetPassword",
      "/getRoles",
      "/getAllTeamsSUFromRegisterUsers",
      "/getTenantsInfo",
      "/getBasicInfo",
      "/getAllTeamsSUFromRegisterUsers",
      "/registerUser",
      "/resetMemoryCache/**",
      "/userActivation**",
      "/getActivationInfo**"
    };

    String[] loginResources = {"/logout", "/login**"};

    http.csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(staticResources)
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .oauth2Login()
        .loginPage("/login")
        .and()
        .logout()
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
        .addLogoutHandler(
            new HeaderWriterLogoutHandler(
                new ClearSiteDataHeaderWriter(
                    ClearSiteDataHeaderWriter.Directive.CACHE,
                    ClearSiteDataHeaderWriter.Directive.COOKIES,
                    ClearSiteDataHeaderWriter.Directive.STORAGE)));
  }

  private CsrfTokenRepository csrfTokenRepository() {
    HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    repository.setHeaderName("X-XSRF-TOKEN");
    return repository;
  }

  @Bean
  WebClient webClient(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository authorizedClientRepository) {
    ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(
            clientRegistrationRepository, authorizedClientRepository);
    oauth2.setDefaultOAuth2AuthorizedClient(true);

    return WebClient.builder().apply(oauth2.oauth2Configuration()).build();
  }

  @Bean
  public AuthorizationRequestRepository<OAuth2AuthorizationRequest>
      authorizationRequestRepository() {
    return new HttpSessionOAuth2AuthorizationRequestRepository();
  }

  @Bean
  public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
      accessTokenResponseClient() {
    DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient =
        new DefaultAuthorizationCodeTokenResponseClient();
    return accessTokenResponseClient;
  }

  @Bean
  public InMemoryUserDetailsManager inMemoryUserDetailsManager() throws Exception {
    final Properties globalUsers = new Properties();
    return new InMemoryUserDetailsManager(globalUsers);
  }
}
