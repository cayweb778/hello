package org.boozsoft.service;

import org.springbooz.core.tool.result.R;
import reactor.core.publisher.Mono;

public interface SysZoneService {
    Mono<R> findAll();
}
