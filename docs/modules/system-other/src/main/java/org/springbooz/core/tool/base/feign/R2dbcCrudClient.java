package org.springbooz.core.tool.base.feign;

import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

/**
 * ss
 * @param <T>  ss
 * @param <S> ss
 */
public class R2dbcCrudClient<T extends ReactiveCrudRepository<S, String>, S> extends CrudClient<T, S> implements ICrudClient<S> {
    /**
     * ss
     */
    @Autowired
    public DatabaseClient client;
    /**
     * ss
     */
    @Autowired
    public R2dbcEntityTemplate template;
    /**
     * ss
     */
    @Autowired
    public T repository;
    /**
     * ss
     */
    public Mono<R> save(@RequestBody S object) {
        return repository.save(object)
                .map(obj -> R.ok().setMessage("保存成功").setResult(obj))
                .onErrorReturn(R.error());
    }
    /**
     * ss
     */
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }
    /**
     * ss
     */
    public Mono<R> get(String id) {
        return repository.findById(id).map(obj -> R.ok().setResult(obj));
    }
    /**
     * ss
     */
    public Mono<R> list() {
        return repository.findAll().collectList().map(R::page);
    }


}
