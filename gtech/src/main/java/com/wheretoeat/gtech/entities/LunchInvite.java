package com.wheretoeat.gtech.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lunch_invite", indexes = {@Index(name = "idx_invCode", columnList = "inviteCode")})
public class LunchInvite implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreatedDate
    @Column(columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP", updatable = false)
    private Instant createTime;

    @Column(columnDefinition = "DATETIME", updatable = false)
    private Instant stopTime;

    @NotNull
    private String inviteCode;

    @NotNull
    private String creator;

    private String decidedRestaurant;

    //public LunchInvite(String iCode, String cName) {
    //    inviteCode = iCode;
    //    creator = cName;
    //}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getStopTime() {
        return stopTime;
    }

    public void setStopTime(Instant stopTime) {
        this.stopTime = stopTime;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDecidedRestaurant() {
        return decidedRestaurant;
    }

    public void setDecidedRestaurant(String decidedRestaurant) {
        this.decidedRestaurant = decidedRestaurant;
    }
}
