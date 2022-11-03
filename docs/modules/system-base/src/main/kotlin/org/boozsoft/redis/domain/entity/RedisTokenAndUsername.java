package org.boozsoft.redis.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash("tokenAndUsername")
public class RedisTokenAndUsername {

    @Id
    String id;
    @Indexed
    String username;
    String token;
}