package com.wheretoeat.gtech.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ShowGroupParamData {


    private String restaurant;

    private String sessId;
    private boolean stopSess;

    public String getRestaurant() {
        return restaurant;
    }

    public String getSessId() {
        return sessId;
    }

    public boolean isStopSess() {
        return stopSess;
    }
}
