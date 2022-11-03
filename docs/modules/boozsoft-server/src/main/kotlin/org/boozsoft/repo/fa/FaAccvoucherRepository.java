package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaAccvoucher;
import org.boozsoft.domain.entity.fa.FaEvaluateMaster;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaAccvoucherRepository extends ReactiveCrudRepository<FaAccvoucher, String> {
}
