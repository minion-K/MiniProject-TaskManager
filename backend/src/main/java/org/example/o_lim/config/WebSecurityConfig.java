package org.example.o_lim.config;

import lombok.RequiredArgsConstructor;
import org.example.o_lim.common.constants.ApiMappingPattern;
import org.example.o_lim.filter.JwtAuthenticationFilter;
import org.example.o_lim.handler.JsonAccessDeniedHandler;
import org.example.o_lim.handler.JsonAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JsonAuthenticationEntryPoint authenticationEntryPoint;
    private final JsonAccessDeniedHandler accessDeniedHandler;

    @Value("${cors.allowed-origins}")
    private String allowOrigins;

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${cors.allowed-methods: GET, POST, PUT, PATCH, DELETE, OPTIONS}")
    private String allowedMethods;

    @Value("${cors.exposed-headers:Authorization, Set-Cookie}")
    private String exposedHeaders;

    @Value("${security.h2-console:true}")
    private boolean h2ConsoleEnabled;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        List<String> origins = splitToList(allowOrigins);

        config.setAllowCredentials(true);
        config.setAllowedHeaders(splitToList(allowedHeaders));
        config.setAllowedMethods(splitToList(allowedMethods));
        config.setExposedHeaders(splitToList(exposedHeaders));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm
                        ->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource((corsConfigurationSource())))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        if(h2ConsoleEnabled) {
            http.headers(header
                    -> header.frameOptions(frame -> frame.sameOrigin()));
        }

        http
                .authorizeHttpRequests(auth -> {
                    if(h2ConsoleEnabled) auth.requestMatchers("h2-console/**").permitAll();

                    auth
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

//                            users/auth
                            .requestMatchers(ApiMappingPattern.Auth.ALL).permitAll()
                            .requestMatchers(ApiMappingPattern.Users.ME_ALL).authenticated()
                            .requestMatchers(HttpMethod.GET, ApiMappingPattern.Users.ALL).permitAll()

//                            admin
                            .requestMatchers(ApiMappingPattern.Admin.ALL).hasRole("ADMIN")

//                            comments
                            .requestMatchers(HttpMethod.POST, ApiMappingPattern.Comments.ALL).hasAnyRole("ADMIN", "MANAGER", "USER")
                            .requestMatchers(HttpMethod.GET, ApiMappingPattern.Comments.ALL).permitAll()
                            .requestMatchers(HttpMethod.DELETE, ApiMappingPattern.Comments.ALL).authenticated()

//                            notifications
                            .requestMatchers(HttpMethod.GET, ApiMappingPattern.Notification.ALL).permitAll()
                            .requestMatchers(HttpMethod.POST, ApiMappingPattern.Notification.ALL).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, ApiMappingPattern.Notification.ALL).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, ApiMappingPattern.Notification.ALL).hasRole("ADMIN")

//                            project
                            .requestMatchers(HttpMethod.POST, ApiMappingPattern.Projects.ALL).hasAnyRole("ADMIN","MANAGER")
                            .requestMatchers(HttpMethod.GET, ApiMappingPattern.Projects.ALL).permitAll()
                            .requestMatchers(HttpMethod.PUT, ApiMappingPattern.Projects.ALL).authenticated()
                            .requestMatchers(HttpMethod.DELETE, ApiMappingPattern.Projects.ALL).hasAnyRole("ADMIN","MANAGER")

//                            tag
                            .requestMatchers(HttpMethod.POST, ApiMappingPattern.Tags.ALL).authenticated()
                            .requestMatchers(HttpMethod.GET, ApiMappingPattern.Tags.ALL).permitAll()
                            .requestMatchers(HttpMethod.DELETE, ApiMappingPattern.Tags.ALL).authenticated()

//                            task
                            .requestMatchers(HttpMethod.POST, ApiMappingPattern.Tasks.ALL).hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, ApiMappingPattern.Tasks.ALL).permitAll()
                            .requestMatchers(HttpMethod.PUT, ApiMappingPattern.Tasks.ALL).authenticated()
                            .requestMatchers(HttpMethod.DELETE, ApiMappingPattern.Tasks.ALL).hasAnyRole("ADMIN", "MANAGER");
                });

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static List<String> splitToList(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}

