package org.example.o_lim.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.o_lim.common.enums.Gender;
import org.example.o_lim.common.enums.RoleType;
import org.example.o_lim.dto.auth.request.SignUpRequestDto;
import org.example.o_lim.entity.base.BaseTimeEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(
        name="users",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_users_login_id", columnNames = "email"),
                @UniqueConstraint(name="uk_users_email", columnNames = "login_id"),
                @UniqueConstraint(name="uk_users_nickname", columnNames = "nickname")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

//    로그인 아이디
    @Column(name="login_id", nullable = false, length = 50)
    private String loginId;

//    비밀번호
    @Column(name="password", nullable = false, length = 255)
    private String password;

//    이메일
    @Column(name="email", nullable = false, length = 255)
    private String email;

//    닉네임
    @Column(name="nickname", nullable = false, length = 50)
    private String nickname;

//    성별
    @Column(name="gender", length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

//    권한
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

//    생성자(성별 미포함)
    public User(String name, String loginId, String password, String email, String nickname) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

//    생성자(성별 포함)
    public User(String name, String loginId, String password, String email, String nickname, Gender gender) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
    }

    //    User 생성 메서드
    public static User getUser(SignUpRequestDto request, String encoded) {
        Gender gender = request.gender();

        User user = gender != null ?
                new User(
                        request.name(),
                        request.loginId(),
                        encoded,
                        request.email(),
                        request.nickname(),
                        request.gender())
                :
                new User(
                        request.name(),
                        request.loginId(),
                        encoded,
                        request.email(),
                        request.nickname()
                );
        return user;
    }

//    비밀번호 재설정
    public void changePassword(String password) {
        this.password = password;
    }

//    프로필 수정을 위한 setter
    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

//    권한 부여/회수
    public void grantRole(Role role) {
        boolean exists = userRoles.stream().anyMatch(ur -> ur.getRole().equals(role));

        if(!exists) {
            userRoles.add(new UserRole(this, role));
        }
    }

    public void revokeRole(Role role) {
        userRoles.removeIf(ur -> ur.getRole().equals(role));
    }

//    JWT 활용
    public Set<RoleType> getRoleTypes() {
        return userRoles.stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toUnmodifiableSet());
    }
}
