package me.hyeonho.studyolle.settings.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.hyeonho.studyolle.domain.Account;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Data
public class Profile {

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    @Length(max = 50)
    private String location;

    private String profileImage;

}

