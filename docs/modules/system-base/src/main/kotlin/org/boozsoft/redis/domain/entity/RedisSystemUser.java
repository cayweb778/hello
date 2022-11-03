package org.boozsoft.redis.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash("abcdd")
public class RedisSystemUser {

    @Id
    String id;
    @Indexed
    String token;
    String abc;
    String user;
    String username;
    String roles;
    String permissions;
}