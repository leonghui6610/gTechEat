package com.wheretoeat.gtech.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ShowGroupFormData {

    @NotNull
    @Size(min = 1, max = 50)
    private String restaurant;
    //@NotNull
    //@Size(min = 1, max = 200)
    //private String sessId;

    //private boolean stopSess;
    public String toParameters() {
        return restaurant;
    }
}
