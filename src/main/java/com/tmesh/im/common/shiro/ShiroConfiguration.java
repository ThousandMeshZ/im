package com.tmesh.im.common.shiro;

import jakarta.servlet.Filter;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : ShiroConfiguration
 */
@Configuration
public class ShiroConfiguration {

    /**
     * 下面两个方法对 注解权限起作用有很大的关系，请把这两个方法，放在配置的最上面
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 身份认证 Realm，此处的注入不可以缺少。否则会在 UserRealm 中注入对象会报空指针.
     * 将自己的验证方式加入容器
     */ 
    @Bean
    public ShiroRealm shiroRealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCredentialsMatcher(this.hashedCredentialsMatcher());
        return shiroRealm;
    }

    /**
     * 配置shiro session 的一个管理器
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionListeners(new ArrayList<>());
        return sessionManager;
    }

    /**
     * 核心的安全事务管理器
     * 设置 realm、cacheManager 等
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置 realm
        securityManager.setRealm(this.shiroRealm());
        // 设置 sessionManager
        securityManager.setSessionManager(this.sessionManager());
        return securityManager;
    }

    /**
     * 开启 shiro aop 注解支持.
     * 使用代理方式;所以需要开启代码支持;否则 @RequiresRoles 等注解无法生效
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 哈希密码比较器。在 myShiroRealm 中作用参数使用
     * 登陆时会比较用户输入的密码，跟数据库密码配合盐值 salt 解密后是否一致。
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5"); //散列算法:这里使用 md5 算法;
        hashedCredentialsMatcher.setHashIterations(1); //散列的次数，比如散列两次，相当于 md5( md5(""));
        return hashedCredentialsMatcher;
    }
    
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //oauth 过滤
        Map<String, Filter> filterMap = new HashMap<>(16);
        filterMap.put("oauth2", new ShiroTokenFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        //权限控制map
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 自定义拦截全部写到下面↓↓↓
        // 免登录接口，增加@IgnoreAuth注解
        // 自定义拦截全部写到上面↑↑↑
        filterChainDefinitionMap.put("/**", "oauth2");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }
}
