package org.boozsoft.domain.ro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.Accvoucher;

/**
 * 视图实体类
 *
 * @author lz
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)

public class StockCostAccRo{
    private  String stockNum;
    private  String stockCangku;
    private  String batchId;
    private  String price;//成本单价
    private  String uimid;//计量单位id
}
