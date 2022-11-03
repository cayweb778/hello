package org.boozsoft.service.impl;

import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.repo.SysLogRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;
import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    SysLogRepository sysLogRepository;
    @Autowired
    R2dbcRouterLoader router;

    @Override
    
    public Mono<R> save_log(SysLog sysLog) {
//        Mono bind = router.bind(() -> {
//                    return sysLogRepository.save(sysLog).map(o -> R.ok().setResult(o));
//                },
//                R2dbcRouterBuilder.r2dbcRouterBuider().database("boozsoft-nc").schema("booznc-group").build()
//        );
//        return bind;
        return sysLogRepository.save(sysLog).map(o -> R.ok().setResult(o));
    }
}
