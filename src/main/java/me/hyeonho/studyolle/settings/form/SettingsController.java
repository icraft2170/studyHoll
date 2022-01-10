package me.hyeonho.studyolle.settings.form;

import lombok.RequiredArgsConstructor;
import me.hyeonho.studyolle.account.AccountRepository;
import me.hyeonho.studyolle.account.AccountService;
import me.hyeonho.studyolle.account.CurrentUser;
import me.hyeonho.studyolle.domain.Account;
import me.hyeonho.studyolle.settings.validatior.NicknameValidator;
import me.hyeonho.studyolle.settings.validatior.PasswordFormValidator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {
    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";
    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/settings/password";
    static final String SETTINGS_NOTIFICATION_VIEW_NAME = "settings/notifications";
    static final String SETTINGS_NOTIFICATION_URL = "/settings/notifications";
    static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
    static final String SETTINGS_ACCOUNT_URL = "/settings/account";


    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

    @GetMapping(SETTINGS_PROFILE_URL)
    public String updateProfileForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account,Profile.class));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile
            , Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String passwordUpdateForm(@CurrentUser Account account,Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account,PasswordForm.class));
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String passwordUpdate(@CurrentUser Account account, @Valid @ModelAttribute PasswordForm passwordForm,
                                 Errors errors,Model model,RedirectAttributes attributes){

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다");
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }

    @GetMapping(SETTINGS_NOTIFICATION_URL)
    public String notificationForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account,Notifications.class));
        return SETTINGS_NOTIFICATION_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATION_URL)
    public String notification(@CurrentUser Account account, @ModelAttribute @Valid Notifications notifications
                                ,Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_NOTIFICATION_VIEW_NAME;
        }
        accountService.updateNotification(account, notifications);
        attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");
        return "redirect:" + SETTINGS_NOTIFICATION_URL;
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccountForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccount(@CurrentUser Account account, @ModelAttribute @Valid NicknameForm nicknameForm
            ,Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }
        accountService.updateAccount(account, nicknameForm);
        attributes.addFlashAttribute("message", "닉네임을 변경했습니다.");
        return "redirect:" + SETTINGS_ACCOUNT_URL;
    }

    @GetMapping("/email-login")
    public String emailLoginForm(){
        return "account/email-login";
    }

    @PostMapping("/email-login")
    public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes){
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "유효한 이메일 주소가 아닙니다.");
            return "account/email-login";
        }
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "이메일 로그인은 한 시간후 부터 이용할 수 있습니");
            return "account/email-login";
        }

        accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message", "이메일 인증 메일을 발송하였습니다");
        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(String token,String email, Model model){
        Account account = accountRepository.findByEmail(email);
        String view = "account/logged-in-by-email";

        if (account == null || !account.isValidToken(token)) {
            model.addAttribute("error", "로그인 할 수 없습니다");
            return view;
        }
        accountService.login(account);
        return view;
    }




}
