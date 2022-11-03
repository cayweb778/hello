package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 视图实体类
 *
 * @author Chill
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "ArApWriteOffVo对象", description = "ArApWriteOffVo对象")
public class ArApWriteOffVo implements Serializable {
    @ApiModelProperty(value = "单据类型",hidden = true)
    private String billStyle;
    @ApiModelProperty(value = "单据日期",hidden = true)
    private String ddate;
    @ApiModelProperty(value = "单据编码",hidden = true)
    private String ccode;
    @ApiModelProperty(value = "客户",hidden = true)
    private String cvencode;
    @ApiModelProperty(value = "结算客户",hidden = true)
    private String cvencodeJs;
    @ApiModelProperty(value = "单据金额",hidden = true)
    private String isum;
    @ApiModelProperty(value = "核销金额",hidden = true)
    private String hxIsum;
    @ApiModelProperty(value = "核销单据类型",hidden = true)
    private String hxStyle;
    @ApiModelProperty(value = "核销单据日期",hidden = true)
    private String hxDate;
    @ApiModelProperty(value = "核销单据编号",hidden = true)
    private String hxCode;
    @ApiModelProperty(value = "核销金额",hidden = true)
    private String hxMoney;
    @ApiModelProperty(value = "折扣金额",hidden = true)
    private String idiscount;
    @ApiModelProperty(value = "是否收款单期初（1期初；0或空非期初）",hidden = true)
    private String skdQichu;
    @ApiModelProperty(value = "是否应收单期初（1期初；0或空非期初）",hidden = true)
    private String ysdQichu;
}
