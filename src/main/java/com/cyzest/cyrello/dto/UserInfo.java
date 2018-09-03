package com.cyzest.cyrello.dto;

import com.cyzest.cyrello.dao.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    private String id;
    private String email;
    private LocalDateTime registerDate;

    public UserInfo(User user) {
        Assert.notNull(user, "user must not be null");
        this.id = user.getId();
        this.email = user.getEmail();
        this.registerDate = user.getRegisterDate();
    }

}
