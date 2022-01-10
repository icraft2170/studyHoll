package me.hyeonho.studyolle.settings.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.hyeonho.studyolle.domain.Account;

@Data
@NoArgsConstructor
public class Notifications {
    // 스터디 생성 알림 메일 여부
    private boolean studyCreatedByEmail;
    // 스터디 생성 알림 웹 여부
    private boolean studyCreatedByWeb;
    // 스터디 가입 결과 알림 이메일
    private boolean studyEnrollmentResultByEmail;
    // 스터디 가입 결과 알림 웹
    private boolean studyEnrollmentResultByWeb;
    // 스터디 업데이트 알림 이메일
    private boolean studyUpdatedByEmail;
    // 스터디 업데이트 알림
    private boolean studyUpdatedByWeb;


}
