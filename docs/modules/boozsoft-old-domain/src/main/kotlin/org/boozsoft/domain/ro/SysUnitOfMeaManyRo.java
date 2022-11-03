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
public class SysUnitOfMeaManyRo {

    private String unitNameg;
    private String unitType;
    private String unitName;
    private String unitName1;
    private String conversionRate1;
    private String conversionExplain1;
    private String unitName2;
    private String conversionRate2;
    private String conversionExplain2;
}
