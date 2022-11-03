package org.boozsoft.service;

import org.boozsoft.domain.entity.SysAccvoucherTemplate;
import reactor.core.publisher.Mono;

public interface AccvoucherTemplateService {
    Mono<SysAccvoucherTemplate> findByAccTemplateId(int id);
}
