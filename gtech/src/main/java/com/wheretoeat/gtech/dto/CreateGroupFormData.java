package com.wheretoeat.gtech.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateGroupFormData {
    @NotNull
    @Size(min = 3, max = 40)
    private String creator;

    public String toParameters() {
        return creator;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
