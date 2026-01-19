package com._labor.fakecord.domain.mappper.Impl;

import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.dto.AccountDto;
import com._labor.fakecord.domain.entity.Account;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.mappper.AccountMapper;
import com._labor.fakecord.domain.mappper.UserMapper;


@Component
public class AccountMapperImpl implements AccountMapper{

  private final UserMapper userMapper;

  public AccountMapperImpl(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public Account fromDto(AccountDto dto) {
    if (null == dto) return null;

    Account account = new Account();
    account.setId(dto.id());
    account.setLogin(dto.login());
    account.setEmail(dto.email());

    if (null != dto.userDto()) {
      User user = userMapper.fromDto(dto.userDto());

      user.setAccount(account);
      account.setUser(user);
    }

    return account;
  }

  @Override
  public AccountDto toDto(Account account) {
    if (null == account) return null;

    return new AccountDto(
      account.getId(),
      account.getLogin(), 
      account.getEmail(), 
      userMapper.toDto(account.getUser())
    );
  }
  
}
