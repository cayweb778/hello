package org.boozsoft.domain.ro;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 视图实体类
 *
 * @author lz
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
public class SubjectBalanceRo {
    private Integer id;
    private String md;
    private String mc;
    private String yue;
    private String name;
    private String flag;
    private String fx;
    private String cwOrys;  // 1预算0财务
}
