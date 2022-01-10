package me.hyeonho.studyolle.settings.validatior;

import lombok.RequiredArgsConstructor;
import me.hyeonho.studyolle.account.AccountRepository;
import me.hyeonho.studyolle.settings.form.NicknameForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nickNameForm = (NicknameForm) target;
        if (accountRepository.existsByNickname(nickNameForm.getNickname())) {
            errors.rejectValue("nickname","wrong.value", "입력하신 닉네임을 사용할 수 없습니다.");
        }
    }
}
