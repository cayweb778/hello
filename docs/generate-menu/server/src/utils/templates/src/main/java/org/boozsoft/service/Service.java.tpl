package org.boozsoft.service.$$platformName$$;

import org.boozsoft.domain.entity.$$platformName$$.$$RecordName$$;
import org.springbooz.core.tool.result.R;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import org.springframework.data.domain.Pageable;

public interface $$RecordName$$Service {
    Flux<$$RecordName$$> findAll(Pageable pageable);

    Mono<$$RecordName$$> findById(String id);

    Mono<$$RecordName$$> save($$RecordName$$ entity);

    Mono<Void> deleteById(String id);
}