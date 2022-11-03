package org.boozsoft.domain.ro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.Accvoucher;

import java.io.Serializable;
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
public class WriteOffRo extends Accvoucher {

    @ApiModelProperty(value = "核销后剩余金额")
    private String remainMoney;

    @ApiModelProperty(value = "核销金额")
    private BigDecimal money;

    @ApiModelProperty(value = "核销日期")
    private String hxdate;

    @ApiModelProperty(value = "核销人")
    private String username;

    @ApiModelProperty(value = "借方核销金额")
    private BigDecimal jmoney;

    @ApiModelProperty(value = "贷方核销金额")
    private BigDecimal dmoney;

    @ApiModelProperty(value = "客户简称")
    private String custname;

    @ApiModelProperty(value = "核销标记号")
    private String hxcode;
}
