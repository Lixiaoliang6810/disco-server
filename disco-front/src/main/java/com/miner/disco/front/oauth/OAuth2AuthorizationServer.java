package com.miner.disco.front.oauth;

import com.miner.disco.basic.constants.BasicConst;
import com.miner.disco.front.oauth.exception.WebResponseExceptionTranslator;
import com.miner.disco.front.oauth.filter.IntegrationAuthenticationFilter;
import com.miner.disco.front.oauth.model.CustomUserDetails;
import com.miner.disco.front.oauth.service.CustomDetailsService;
import com.miner.disco.front.oauth.service.DatabaseCachableClientDetailsService;
import com.miner.disco.front.oauth.service.IntegrationUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Created by lubycoder@163.com 2018/12/26
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private CustomDetailsService customDetailsService;

    @Autowired
    private WebResponseExceptionTranslator webResponseExceptionTranslator;

    @Autowired
    private DatabaseCachableClientDetailsService databaseCachableClientDetailsService;

    @Autowired
    private IntegrationUserDetailsService integrationUserDetailsService;

    @Autowired
    private IntegrationAuthenticationFilter integrationAuthenticationFilter;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(databaseCachableClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .tokenEnhancer(tokenEnhancer())
                .tokenStore(redisTokenStore())
                .authenticationManager(authenticationManager)
                .exceptionTranslator(webResponseExceptionTranslator)
                .userDetailsService(integrationUserDetailsService)
                .reuseRefreshTokens(false);
//                .pathMapping("/oauth/token", "/member/login");
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients()
                .addTokenEndpointAuthenticationFilter(integrationAuthenticationFilter);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public RedisTokenStore redisTokenStore() {
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix(BasicConst.USER_OAUTH_CACHE_PREFIX);
        return redisTokenStore;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            if (accessToken instanceof DefaultOAuth2AccessToken) {
                DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
                CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
                Map<String, Object> additionalInformation = new LinkedHashMap<>();
                additionalInformation.put("id", customUserDetails.getId());
                additionalInformation.put("im_account", customUserDetails.getImAccount());
                additionalInformation.put("im_password", customUserDetails.getImPassword());
                token.setAdditionalInformation(additionalInformation);
            }
            return accessToken;
        };
    }


}
