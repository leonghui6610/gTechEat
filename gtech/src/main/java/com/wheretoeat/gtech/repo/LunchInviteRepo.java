package com.wheretoeat.gtech.repo;

import com.wheretoeat.gtech.entities.LunchInvite;

public interface LunchInviteRepo extends BaseRepo<LunchInvite, Long> {
    LunchInvite findByInviteCodeIgnoreCase(String inviteCode);
}
