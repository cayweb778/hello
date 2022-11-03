package org.boozsoft.service;

import org.boozsoft.domain.entity.SysLog;
import org.springbooz.core.tool.result.R;
import reactor.core.publisher.Mono;

public interface SysLogService {
    /** 系统保存日志 */
    Mono<R> save_log(SysLog sysLog);
}
