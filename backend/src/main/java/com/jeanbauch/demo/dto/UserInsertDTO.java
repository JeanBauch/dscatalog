package com.jeanbauch.demo.dto;

import com.jeanbauch.demo.services.validation.UserInsertValid;

@UserInsertValid
public class UserInsertDTO extends UserDTO {

    private String password;

    UserInsertDTO() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
