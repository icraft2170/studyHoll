package me.hyeonho.studyolle.account;

import lombok.RequiredArgsConstructor;
import me.hyeonho.studyolle.domain.Account;
import me.hyeonho.studyolle.settings.form.NicknameForm;
import me.hyeonho.studyolle.settings.form.Notifications;
import me.hyeonho.studyolle.settings.form.Profile;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        String rawPassword = signUpForm.getPassword();
        signUpForm.setPassword(passwordEncoder.encode(rawPassword));
        return accountRepository.save(signUpForm.toEntity());
    }

    public void sendSignUpConfirmEmail(Account account) {
        account.generateEmailCheckToken();
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

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account)
                , account.getPassword()
                , List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile,account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        Account findAccount = accountRepository.findByNickname(account.getNickname());
        findAccount.setPassword(passwordEncoder.encode(newPassword));
    }

    public void updateNotification(Account account, Notifications notifications) {
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }

    public void updateAccount(Account account, NicknameForm nickNameForm) {
        modelMapper.map(nickNameForm,account);
        accountRepository.save(account);
        login(account);
    }


    public void sendLoginLink(Account account) {
        account.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("스터디올래, 로그인 링크");
        mailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        javaMailSender.send(mailMessage);
    }
}
