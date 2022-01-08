package me.hyeonho.studyolle.account;

import lombok.RequiredArgsConstructor;
import me.hyeonho.studyolle.domain.Account;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
    }


    private Account saveNewAccount(SignUpForm signUpForm) {
        String rawPassword = signUpForm.getPassword();
        signUpForm.setPassword(passwordEncoder.encode(rawPassword));
        return accountRepository.save(signUpForm.toEntity());
    }

    private void sendSignUpConfirmEmail(Account account) {
        javaMailSender.send(createMaileMessage(account));
    }

    private SimpleMailMessage createMaileMessage(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디올래, 회원가입 인증"); //메일 제목
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail()); // 메일 본문
        return mailMessage;
    }


}
