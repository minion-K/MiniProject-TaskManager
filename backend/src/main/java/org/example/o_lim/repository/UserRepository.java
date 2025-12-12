package org.example.o_lim.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.example.o_lim.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public interface UserWithRolesProjection {
        String getUsername();
        String getUserNickname();
        String getUserLoginId();
        String getRoleName();
    }

    @Query(value = """
        SELECT
            name as Username,
            nickname as UserNickname,
            login_id as UserLoginId,
            role_name as RoleName
        FROM (
            SELECT 
                u.name,
                u.nickname,
                u.login_id,
                r.role_name, 
                ROW_NUMBER() OVER(
                    PARTITION BY u.id
                    ORDER BY CASE r.role_name
                        WHEN 'ADMIN' THEN 1
                        WHEN 'MANAGER' THEN 2
                        ELSE 3
                    END
                ) as rn
            FROM users u
            LEFT JOIN  user_roles r
            ON u.id = r.user_id
            ) t
        WHERE rn = 1            
    """, nativeQuery = true)
    List<UserWithRolesProjection> findAllUsersWithRole_Native();

    @EntityGraph(attributePaths = "userRoles")
    Optional<User> findByLoginId(String id);

    @EntityGraph(attributePaths = "userRoles")
    Optional<User> findWithRolesById(Long id);

    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(@Email @NotBlank(message = "이메일을 입력해주세요") String email);
}
