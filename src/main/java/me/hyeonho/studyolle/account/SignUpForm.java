package me.hyeonho.studyolle.account;

import lombok.Data;
import me.hyeonho.studyolle.domain.Account;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.*;


@Data
public class SignUpForm {
    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9_-]{3,20}$")
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;

    public Account toEntity(){
        return Account.builder()
                .email(this.getEmail())
                .nickname(this.getNickname())
                .password(this.getPassword()) // Todo encoding 해야함
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
    }
}
