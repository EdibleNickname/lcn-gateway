package com.can.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
public class User {

    private Integer userId;

    private String userName;

    private String password;

    private String email;

    private int sex;

    private String headPortraits;

    private Date birthday;

    private String introduce;

    private Date lastPasswordResetDate;

    private Date registDate;

    private Boolean enable;

    private Set<Role> roleSet;
}