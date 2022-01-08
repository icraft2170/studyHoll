package me.hyeonho.studyolle.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        return "account/sign-up";
    }



}
