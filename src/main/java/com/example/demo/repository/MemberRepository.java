package com.example.demo.repository;

import com.example.demo.entity.Member;
import com.example.demo.entity.MemberRole;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select member from Member member where member.id in :ids order by member.id")
    List<Member> lockForRoleUpdate(List<Long> ids);

    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByRole(MemberRole role);
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);
}
