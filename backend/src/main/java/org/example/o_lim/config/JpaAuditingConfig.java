package org.example.o_lim.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration // 전역 설정
@EnableJpaAuditing // JpaAuditing 사용 설정
public class JpaAuditingConfig {}
