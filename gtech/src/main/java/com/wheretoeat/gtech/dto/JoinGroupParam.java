package com.wheretoeat.gtech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinGroupParam {
    private String user;
    private String inviteCode;
}
