package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaCardMaster;
import org.boozsoft.domain.entity.fa.FaPandianMaster;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaPandianMasterRepository extends ReactiveCrudRepository<FaPandianMaster, String> {
    Mono<FaPandianMaster> findFirstByEndDateOrderByPdIdDesc(String date);
    Flux<FaPandianMaster> findAllByManageCodeOrderByMakerTimeAscPdIdAsc(String code);
    Flux<FaPandianMaster> findAllByManageCodeAndMakerTimeLikeOrderByMakerTimeAscPdIdAsc(String code,String yearMonth);
}
