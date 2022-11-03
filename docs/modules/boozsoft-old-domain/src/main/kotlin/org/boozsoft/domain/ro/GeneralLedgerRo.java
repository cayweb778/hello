package org.boozsoft.domain.ro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.Accvoucher;

import java.math.BigDecimal;

/**
 * 视图实体类
 *
 * @author lz
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "总账列表对象", description = "总账列表对象")
public class GeneralLedgerRo extends Accvoucher {

    @ApiModelProperty(value = "对应序号")
    private String dnum;

    @ApiModelProperty(value = "科目名称")
    private String ccodeName;

    @ApiModelProperty(value = "数量核算计量单位名称", position = 19)
    private String menterage;

    @ApiModelProperty(value = "币种 3位国际代码", position = 27)
    private String currencytype;

    @ApiModelProperty(value = "是否末级科目（1是，0否）")
    private String bend;

    @ApiModelProperty(value = "科目方向（1：借，0：贷）")
    private String bprogerty;

    @ApiModelProperty(value = "科目级次（从1至最大30）")
    private String igrade;

    @ApiModelProperty(value = "部门编码（不允许重复）")
    private String dcode;
    @ApiModelProperty(value = "部门名称")
    private String dname;

    @ApiModelProperty(value = "项目编码（不允许重复）")
    private String pcode;
    @ApiModelProperty(value = "项目名称")
    private String pname;
    @ApiModelProperty(value = "是否现金流量科目")
    private Boolean isXj;

    private String value;
    private String label;

}
