package com.example.Api;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/public/**").permitAll() // Permitir acceso público a rutas /public/**
                .antMatchers("/admin/**").hasRole("ADMIN") // Requiere el rol "ADMIN" para rutas /admin/**
               // .antMatchers("/swagger-ui/**", "/v2/api-docs", "/swagger-resources/**").permitAll() // Permitir acceso a Swagger sin autenticación
                .antMatchers(HttpMethod.GET, "/posts/**").permitAll()
              
                .antMatchers(HttpMethod.GET, "/posts/**").hasAnyRole("USER", "ADMIN") // Requiere el rol "USER" o "ADMIN" para métodos GET
                .antMatchers(HttpMethod.POST, "/posts/createPost").hasAuthority("CREATE_POST") // Requiere la autoridad "CREATE_POST" para el método createPost
                .antMatchers(HttpMethod.PUT, "/posts/updatePost/**").hasAuthority("UPDATE_POST") // Requiere la autoridad "UPDATE_POST" para métodos PUT
                .antMatchers(HttpMethod.GET, "/posts/getPostById/**").hasAnyRole("USER", "ADMIN") // Requiere el rol "USER" o "ADMIN" para el método getPostById
                .antMatchers(HttpMethod.DELETE, "/posts/deletePostById/**").hasAuthority("DELETE_POST") // Requiere la autoridad "DELETE_POST" para métodos DELETE
                .anyRequest().authenticated() // Denegar acceso por defecto a cualquier otra ruta
                .and()
                .formLogin() // Configurar página de inicio de sesión
                .and()
                .httpBasic()
                .and()
                .csrf().disable() // Deshabilitar CSRF para permitir peticiones desde Swagger UI
                .headers().frameOptions().disable(); // Deshabilitar X-Frame-Options para permitir la visualización de Swagger UI en un iframe
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}password").roles("USER")
                .authorities("GET_POSTS", "GET_POST_BY_ID")
                .and()
                .withUser("admin").password("{noop}password").roles("ADMIN")
                .authorities("CREATE_POST", "UPDATE_POST", "DELETE_POST", "GET_POSTS", "GET_POST_BY_ID");
    }
}
