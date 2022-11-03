package org.boozsoft.redis.repo;

import org.boozsoft.redis.domain.entity.RedisSystemUser;
import org.boozsoft.repo.entity.Jigou;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.List;
import java.util.Optional;

public interface RedisSystemUserRepository extends CrudRepository<RedisSystemUser,String>, QueryByExampleExecutor<RedisSystemUser> {
    public RedisSystemUser findByToken(String token);
    public List<RedisSystemUser> findAllByUsername(String token);
    public RedisSystemUser findByAbc(String abc);

    RedisSystemUser deleteByUsername(String username);
    List<RedisSystemUser> deleteAllByUsername(String username);
    List<RedisSystemUser> deleteAllByToken(String username);
}