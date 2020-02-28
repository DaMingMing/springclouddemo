package com.xiaojm.config;

import com.xiaojm.service.UserServiceDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * Created by xiaojm
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    //配置客户端信息
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()   //将客户端信息存储在内存中
                .withClient("uaa-service")
                .secret("123456")
                .scopes("service")
                .autoApprove(true)
                .authorizedGrantTypes("implicit","refresh_token", "password", "authorization_code")
               // .accessTokenValiditySeconds(24*3600);//配置token过期时间 24小时过期
                .accessTokenValiditySeconds(60 * 5);//配置token过期时间 20分钟过期
    }

    //检查token是否有效， oauth/check_token  请求所需参数：token 。例如：http://localhost/oauth/check_token?token=f57ce129-2d4d-4bd7-1111-f31ccc69d4d1
    //此接口默认关闭，不允许访问，如需访问，需要重写以下方法
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        //允许表单认证
        oauthServer.allowFormAuthenticationForClients();
        oauthServer.checkTokenAccess("permitAll()");
    }

    @Autowired
    UserServiceDetail userServiceDetail;

    //配置授权token节点跟token服务,配置tokenStore和authenticationManager
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //tokenStore有三种策略
        //1、InMemoryTokenStore,token存在内存中，仅限于资源服务跟授权服务器为同个服务
        //2、JdbcTokenStore,存在数据库
        //3、JwtTokenStore，采用JWT形式，不做任何存储，因为JWT本身包含用户信息
        endpoints.tokenStore(tokenStore()).tokenEnhancer(jwtTokenEnhancer()).authenticationManager(authenticationManager)
        .userDetailsService(userServiceDetail);   //设置此参数方可请求刷新token请求（/oauth/token），重新更新access_token
    }

    //这个bean来源于WebSecurityConfigurerAdapter，只有配置了这个bean才会开启密码类型验证
    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtTokenEnhancer());
    }

    @Bean
    protected JwtAccessTokenConverter jwtTokenEnhancer() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("fzp-jwt.jks"), "fzp123".toCharArray());
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("fzp-jwt"));
        return converter;
    }
}
