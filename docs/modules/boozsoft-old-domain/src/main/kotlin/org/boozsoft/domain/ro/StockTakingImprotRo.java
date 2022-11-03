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
public class StockTakingImprotRo {

    private String stockNum;

    private String unitName;

    private String stockBarcode;

    private String num;


    private String batchId;

    private String dpdate;

    private String dvdate;
}
