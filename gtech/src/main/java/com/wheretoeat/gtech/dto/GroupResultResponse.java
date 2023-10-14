package com.wheretoeat.gtech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GroupResultResponse {
    private String inviteCode;
    private String finalRestaurant;
    private List<String> lunchers;
    private List<String> restaurants;

    public String getFinalRestaurant() {
        return finalRestaurant;
    }

    public void setFinalRestaurant(String finalRestaurant) {
        this.finalRestaurant = finalRestaurant;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public List<String> getLunchers() {
        return lunchers;
    }

    public void setLunchers(List<String> lunchers) {
        this.lunchers = lunchers;
    }

    public List<String> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<String> restaurants) {
        this.restaurants = restaurants;
    }
}
