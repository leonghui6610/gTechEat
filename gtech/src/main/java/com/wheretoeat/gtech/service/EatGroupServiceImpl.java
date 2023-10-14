package com.wheretoeat.gtech.service;

import com.wheretoeat.gtech.dto.GroupInitResponse;
import com.wheretoeat.gtech.dto.GroupResultResponse;
import com.wheretoeat.gtech.entities.LunchInvite;
import com.wheretoeat.gtech.entities.Lunchers;
import com.wheretoeat.gtech.repo.LunchInviteRepo;
import com.wheretoeat.gtech.repo.LunchersRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EatGroupServiceImpl {

    private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    private Map<String, Long> createGroupTime = new HashMap<>();

    private Map<String, Long> joinGroupTime = new HashMap<>();
    @Autowired
    private LunchInviteRepo inviteRepo;

    @Autowired
    private LunchersRepo lunchersRepo;

    @Transactional
    public GroupInitResponse startGroup(String creatorName) throws Exception {
        Long creatorLastCreate = createGroupTime.get(creatorName);
        if ((creatorLastCreate == null || System.currentTimeMillis() - creatorLastCreate > 1000 * 10 * 60) && StringUtils.isNotBlank(creatorName)) {
            String inviteName = generateInviteCode(creatorName);
            GroupInitResponse resp = new GroupInitResponse(generateInviteCode(creatorName), generateToken(creatorName + inviteName));
            LunchInvite invite = LunchInvite.builder().inviteCode(resp.getInviteCode()).creator(creatorName).createTime(Instant.now()).build();
            LunchInvite inv = inviteRepo.save(invite);
            Lunchers luncher = Lunchers.builder().inviteCode(resp.getInviteCode()).invitedName(creatorName).createTime(Instant.now()).sessId(resp.getSessId()).build();
            lunchersRepo.save(luncher);
            createGroupTime.put(creatorName, System.currentTimeMillis());
            return resp;
        } else {
            log.error("{} Not allow to create", creatorName);
            throw new Exception("Not allow to create");
        }
    }

    @Transactional
    public GroupInitResponse inviteJoin(String inviteCode, String inviteName) throws Exception {
        LunchInvite inv = inviteRepo.findByInviteCodeIgnoreCase(inviteCode);
        String sessId = null;
        if (inv != null && inv.getStopTime() == null && StringUtils.isBlank(inv.getDecidedRestaurant()) && StringUtils.isNotBlank(inviteName) && StringUtils.isNotBlank(inviteCode)) {
            Lunchers findExist = lunchersRepo.findByInvitedNameAndInviteCode(inviteName, inviteCode);
            if (findExist == null) {
                sessId = generateToken(inviteName + inviteName);
                Lunchers luncher = Lunchers.builder().inviteCode(inviteCode).invitedName(inviteName).createTime(Instant.now()).sessId(sessId).build();
                lunchersRepo.save(luncher);
            } else {
                sessId = findExist.getSessId();
            }
            return new GroupInitResponse(inviteCode, sessId);
        } else {
            log.error("{} Invite code does not exist or has stopped or decided on restaurant {}", inviteCode, inv.getDecidedRestaurant());
            throw new Exception(inviteCode + " Invite code does not exist or has stopped and/or decided on restaurant <h2>[" + inv.getDecidedRestaurant() + "]</h2] username [" + inviteName + "]");
        }
    }

    @Transactional
    public boolean stopGroupSession(String inviteCode, String sessId) throws Exception {
        boolean stopSuccess = false;
        Lunchers owner = lunchersRepo.findBySessId(sessId);
        LunchInvite invite = inviteRepo.findByInviteCodeIgnoreCase(inviteCode);
        if (owner == null || invite == null || !(owner.getInvitedName().equals(invite.getCreator()))) {
            throw new Exception("Only creator allowed to stop the group session");
        }
        List<Lunchers> lucnhers = lunchersRepo.findByInviteCode(inviteCode);
        List<Lunchers> filtered = lucnhers.stream().filter(e -> StringUtils.isNotBlank(e.getRestaurant())).collect(Collectors.toList());
        String saveRestaurant = "noDecisionSessionStopped";
        if (!filtered.isEmpty()) {
            Random rand = new Random();
            Lunchers randomElement = filtered.get(rand.nextInt(filtered.size()));
            if (StringUtils.isNotBlank(randomElement.getRestaurant())) {
                saveRestaurant = randomElement.getRestaurant();
            }
        }
        invite.setStopTime(Instant.now());
        invite.setDecidedRestaurant(saveRestaurant);
        inviteRepo.save(invite);
        stopSuccess = true;
        return stopSuccess;
    }

    public GroupResultResponse showPolling(String inviteCode, String sessId) throws Exception {
        List<Lunchers> allInGroup = lunchersRepo.findByInviteCode(inviteCode);
        GroupResultResponse resp = null;
        Optional<Lunchers> lunchUser = allInGroup.stream().filter(e -> e.getSessId().equalsIgnoreCase(sessId)).findFirst();
        if (!lunchUser.isPresent()) {
            log.error("{} User session is not valid", sessId);
            throw new Exception("user session is not valid");
        }
        if (allInGroup != null && !allInGroup.isEmpty()) {
            List<String> restaurants = allInGroup.stream().map(Lunchers::getRestaurant).collect(Collectors.toList());
            List<String> users = allInGroup.stream().map(Lunchers::getInvitedName).collect(Collectors.toList());
            LunchInvite invite = inviteRepo.findByInviteCodeIgnoreCase(inviteCode);
            resp = new GroupResultResponse(inviteCode, invite.getDecidedRestaurant(), users, restaurants);
        } else {
            log.error("{} Invite code is not available", inviteCode);
            throw new Exception("invite code is not available");
        }
        return resp;
    }

    public boolean submitRestaurant(String sessId, String restaurant) throws Exception {
        Lunchers lunchUser = lunchersRepo.findBySessId(sessId);
        if (lunchUser == null) {
            log.error("{} User session is not valid", sessId);
            throw new Exception("user session is not valid");
        }
        LunchInvite invite = inviteRepo.findByInviteCodeIgnoreCase(lunchUser.getInviteCode());
        if (invite == null || invite.getStopTime() != null) {
            log.error("{} InviteCode is invalid", lunchUser.getInviteCode());
            throw new Exception(" InviteCode is invalid");
        }
        if (StringUtils.isBlank(restaurant)) {
            return true;
        }
        lunchUser.setRestaurant(restaurant);
        lunchersRepo.save(lunchUser);
        return true;
    }

    private String generateInviteCode(String creatorName) {
        String icode = null;
        LunchInvite inviteExist = null;
        do {
            icode = RandomStringUtils.random(8, creatorName + getTimeMiliSecString()).toUpperCase();
            inviteExist = inviteRepo.findByInviteCodeIgnoreCase(icode);

        } while (inviteExist != null);
        return RandomStringUtils.random(8, creatorName + getTimeMiliSecString()).toUpperCase();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private String getTimeMiliSecString() {
        long miliSec = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        return Long.toString(miliSec);
    }
}
