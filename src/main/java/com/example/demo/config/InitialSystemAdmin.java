package com.example.demo.config;

import com.example.demo.entity.Member;
import com.example.demo.entity.MemberRole;
import com.example.demo.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class InitialSystemAdmin implements ApplicationRunner {
    private final MemberRepository members;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public InitialSystemAdmin(MemberRepository members, PasswordEncoder passwordEncoder,
            @Value("${ERP_ADMIN_EMAIL:}") String email, @Value("${ERP_ADMIN_PASSWORD:}") String password) {
        this.members = members;
        this.passwordEncoder = passwordEncoder;
        this.email = email.strip();
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (members.existsByRole(MemberRole.SYSTEM_ADMIN)) return;

        if (email.isEmpty() && password.isEmpty()) {
            log.warn("시스템 관리자 계정이 없습니다. ERP_ADMIN_EMAIL과 ERP_ADMIN_PASSWORD를 설정하면 시작 시 생성됩니다.");
            return;
        }
        if (!email.matches("[^\\s@]+@[^\\s@]+") || email.length() > 100 || password.isBlank()
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException("ERP_ADMIN_EMAIL에 유효한 이메일을, ERP_ADMIN_PASSWORD에 72바이트 이하의 비밀번호를 설정하세요.");
        }
        if (members.existsByEmailIgnoreCase(email)) {
            if (members.existsByRole(MemberRole.SYSTEM_ADMIN)) return;
            throw new IllegalStateException("ERP_ADMIN_EMAIL이 기존 회원 이메일과 겹칩니다. 새 이메일을 지정하세요.");
        }

        Member admin = Member.builder()
                .firstName("시스템")
                .lastName("관리자")
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(MemberRole.SYSTEM_ADMIN)
                .build();
        try {
            members.saveAndFlush(admin);
            log.info("최초 시스템 관리자 계정이 생성되었습니다.");
        } catch (DataIntegrityViolationException error) {
            if (!members.existsByRole(MemberRole.SYSTEM_ADMIN)) throw error;
        }
    }
}
