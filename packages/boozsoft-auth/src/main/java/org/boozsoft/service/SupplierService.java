package org.boozsoft.service;

import org.boozsoft.domain.vo.CustomerVo;
import reactor.core.publisher.Flux;

public interface SupplierService {
    Flux<CustomerVo> findAllCustomerByDatabase(String test);
}
