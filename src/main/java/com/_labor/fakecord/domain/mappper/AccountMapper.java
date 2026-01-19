package com._labor.fakecord.domain.mappper;

import com._labor.fakecord.domain.dto.AccountDto;
import com._labor.fakecord.domain.entity.Account;

public interface AccountMapper {
  Account fromDto(AccountDto dto);
  AccountDto toDto(Account account);
}
