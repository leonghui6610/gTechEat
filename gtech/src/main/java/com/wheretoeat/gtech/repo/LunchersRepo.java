package com.wheretoeat.gtech.repo;

import com.wheretoeat.gtech.entities.Lunchers;

import java.util.List;

public interface LunchersRepo extends BaseRepo<Lunchers, Long> {
    Lunchers findByInvitedNameAndInviteCode(String invitedName, String inviteCode);

    Lunchers findBySessId(String sessId);

    List<Lunchers> findByInviteCode(String inviteCode);
}
