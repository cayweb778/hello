package org.boozsoft.redis.repo;

import org.boozsoft.redis.domain.entity.RedisSystemUser;
import org.boozsoft.redis.domain.entity.RedisTokenAndUsername;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

public interface RedisTokenAndUsernameRepository extends CrudRepository<RedisTokenAndUsername,String>, QueryByExampleExecutor<RedisTokenAndUsername> {
    List<RedisTokenAndUsername> findAllByUsername( String username);

    RedisTokenAndUsername deleteByUsername( String s);
    List<RedisTokenAndUsername> deleteAllByUsername( String s);
    List<RedisTokenAndUsername> deleteAllByToken( String s);
}