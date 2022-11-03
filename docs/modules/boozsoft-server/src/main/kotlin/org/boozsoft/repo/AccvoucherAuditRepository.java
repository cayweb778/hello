package org.boozsoft.repo;

import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.entity.AccvoucherAudit;
import org.boozsoft.domain.ro.GeneralLedgerRo;
import org.boozsoft.domain.ro.WriteOffRo;
import org.boozsoft.domain.vo.*;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static org.boozsoft.rest.initialBalance.subject.SubjectInitialBalanceContorller.VIEW_CODE_KEMU;

public interface AccvoucherAuditRepository extends ReactiveCrudRepository<AccvoucherAudit, String> {

}

