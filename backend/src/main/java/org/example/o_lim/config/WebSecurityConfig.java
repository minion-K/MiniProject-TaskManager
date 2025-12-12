package org.example.o_lim.config;

import lombok.RequiredArgsConstructor;
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
                            .requestMatchers("/api/v1/auth/**").permitAll()
                            .requestMatchers("/api/v1/users/me/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll()

//                            admin
                            .requestMatchers("/api/v1/admin/**").authenticated()

//                            comments
                            .requestMatchers(HttpMethod.POST, "/api/v1/tasks/*/comments/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/tasks/*/comments/**").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/tasks/*/comments/**").authenticated()

//                            notifications
                            .requestMatchers("/api/v1/notifications/**").hasRole("ADMIN")

//                            project
                            .requestMatchers(HttpMethod.POST, "/api/v1/projects/**").hasAnyRole("ADMIN","MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/projects/**").permitAll()
                            .requestMatchers(HttpMethod.PUT, "/api/v1/projects/**").authenticated()
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/projects/**").hasAnyRole("ADMIN","MANAGER")

//                            tag
                            .requestMatchers(HttpMethod.POST, "/api/v1/projects/*/tags/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/v1/projects*/tags/**").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/projects/*/tags/**").authenticated()

//                            task
                            .requestMatchers(HttpMethod.POST, "/api/v1/projects/*/tasks/**").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.GET, "/api/v1/projects/*/tasks/**").permitAll()
                            .requestMatchers(HttpMethod.PUT, "/api/v1/projects/*/tasks/**").authenticated()
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/projects/*/tasks/**").hasAnyRole("ADMIN", "MANAGER");
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

