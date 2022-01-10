package me.hyeonho.studyolle.settings.form;

import me.hyeonho.studyolle.account.AccountRepository;
import me.hyeonho.studyolle.account.AccountService;
import me.hyeonho.studyolle.account.SignUpForm;
import me.hyeonho.studyolle.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("hero");
        signUpForm.setEmail("hero@email.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
    }

    @WithUserDetails(value = "hero", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정")
    @Test
    void updateProfileForm() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("account"));
    }

    @WithUserDetails(value = "hero", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account hero = accountRepository.findByNickname("hero");
        assertEquals(bio,hero.getBio());

    }

    @WithUserDetails(value = "hero", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "35자 이상인 경우에는 어떻게 될까? 35자 이상인 경우에는 어떻게 될까? 35자 이상인 경우에는 어떻게 될까? 35자 이상인 경우에는 어떻게 될까? 35자 이상인 경우에는 어떻게 될까? 35자 이상인 경우에는 어떻게 될까? 35자 이상인 경우에는 어떻게 될까?";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors());

        Account hero = accountRepository.findByNickname("hero");
        assertNull(hero.getBio());

    }

    @WithUserDetails(value = "hero", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 수정하기 - 입력값 에러")
    @Test
    void updatePassword_fail() throws Exception {
        String newPassword = "12345678";
        String newPasswordConfirm = "12345679";
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", newPassword)
                        .param("newPasswordConfirm", newPasswordConfirm)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));

        Account hero = accountRepository.findByNickname("hero");
        assertNotEquals(hero.getPassword(),newPassword);

    }

    @WithUserDetails(value = "hero", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 수정하기 - 성공")
    @Test
    void updatePassword_success() throws Exception {
        String newPassword = "12345678";
        String newPasswordConfirm = "12345678";
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", newPassword)
                        .param("newPasswordConfirm", newPasswordConfirm)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account hero = accountRepository.findByNickname("hero");
        assertTrue(passwordEncoder.matches(newPassword, hero.getPassword()));

    }

    @WithUserDetails(value = "hero", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 수정폼")
    @Test
    void updatePassword_form() throws Exception {
        String newPassword = "12345678";
        String newPasswordConfirm = "12345678";
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));

        Account hero = accountRepository.findByNickname("hero");
        assertTrue(passwordEncoder.matches(newPassword, hero.getPassword()));

    }

    @WithUserDetails(value = "hero", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("닉네임 수정 폼")
    @Test
    void updateAccountForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithUserDetails(value = "hero", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateAccount_success() throws Exception {
        String newNickname = "hero2";
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                        .param("nickname", newNickname)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname(newNickname));
    }

    @WithUserDetails(value = "hero", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("닉네임 수정하기 - 입력값 에러")
    @Test
    void updateAccount_failure() throws Exception {
        String newNickname = "¯\\_(ツ)_/¯";
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                        .param("nickname", newNickname)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

}


