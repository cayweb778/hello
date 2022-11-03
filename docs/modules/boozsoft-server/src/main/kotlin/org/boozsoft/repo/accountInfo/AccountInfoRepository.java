package org.boozsoft.repo.accountInfo;

import org.boozsoft.domain.entity.accountInfo.AccoutInfo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountInfoRepository extends ReactiveCrudRepository<AccoutInfo, String> {

}

