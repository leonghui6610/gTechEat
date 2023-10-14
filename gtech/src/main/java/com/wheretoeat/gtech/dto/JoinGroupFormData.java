package com.wheretoeat.gtech.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JoinGroupFormData {

    @NotNull
    @Size(min = 3, max = 40)
    private String username;

    @NotNull
    @Size(min = 8, max = 8)
    private String inviteCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public JoinGroupParam toParameters() {
        return new JoinGroupParam(this.username, this.inviteCode);
    }
}
