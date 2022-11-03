package org.boozsoft.service;

import reactor.core.publisher.Mono;

public interface AccTemplateService {
    // 集团
    Mono save(String id,String uniqueAccStandard, String templateId,String jici);
    // 组织
    Mono findByZZCodeKeMuAndsave(String id,String uniqueAccStandard, String templateId,String jici,String iyear,String orgUnique);
}
