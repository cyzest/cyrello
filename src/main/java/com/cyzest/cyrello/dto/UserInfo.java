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
    private LocalDateTime regDate;

    public UserInfo(User user) {
        Assert.notNull(user, "user must not be null");
        this.id = user.getId();
        this.regDate = user.getRegDate();
    }

}
