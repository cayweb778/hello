package org.springbooz.core.tool.base.feign;

import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * 增删改查
 * @param <T> ss
 * @param <S> ss
 */
public class CrudClient<T extends ReactiveCrudRepository<S,String>,S> implements ICrudClient<S> {
    /**
     * ss
     */
    @Autowired
    public T repository;
    /**
     * ss
     */
    public Mono save(S object) {
        return repository.save(object).map(o -> R.ok().setResult(o));
    }
    /**
     * ss
     */
    public Mono<Void> delete(String id) {
        repository.deleteById(id);
        return null;
    }
    /**
     * ss
     */
    public Mono get(String id) {
        return repository.findById(id);
    }
    /**
     * ss
     */
    public Mono<R> list() {
        return repository.findAll().collectList().map(R::page);
    }

}
