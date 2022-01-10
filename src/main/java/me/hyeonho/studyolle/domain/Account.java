package me.hyeonho.studyolle.domain;

import lombok.*;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    //이메일 인증 여부
    private boolean emailVerified;

    //이메일 검증 토큰 값
    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGenerateAt;

    // 인증된 가입 날짜.
    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    // 직업
    private String occupation;

    // 지역
    private String location; // varchar(255)

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    // 스터디 생성 알림 메일 여부
    private boolean studyCreatedByEmail;
    // 스터디 생성 알림 웹 여부
    private boolean studyCreatedByWeb = true;
    // 스터디 가입 결과 알림 이메일
    private boolean studyEnrollmentResultByEmail;
    // 스터디 가입 결과 알림 웹
    private boolean studyEnrollmentResultByWeb = true;
    // 스터디 업데이트 알림 이메일
    private boolean studyUpdatedByEmail;
    // 스터디 업데이트 알림
    private boolean studyUpdatedByWeb = true;


    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGenerateAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGenerateAt.isBefore(LocalDateTime.now().minusHours(1));
    }
}
