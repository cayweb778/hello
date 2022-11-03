package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：银行日记账明细", description = "银行日记账明细")
@Table("bank_day_book")
@Data
public class BankDayBook {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "凭证日期", hidden = true)
    private String accvouchDate;
    @ApiModelProperty(value = "凭证唯一码", hidden = true)
    private String accvouchNum;
    @ApiModelProperty(value = "分录ID", hidden = true)
    private String vouchId;
    @ApiModelProperty(value = "票据日期", hidden = true)
    private String pjDate;
    @ApiModelProperty(value = "票据号", hidden = true)
    private String pjCode;
    @ApiModelProperty(value = "结算方式", hidden = true)
    private String settModes;
    @ApiModelProperty(value = "科目编码", hidden = true)
    private String ccode;
    @ApiModelProperty(value = "区间年月表（202105）", hidden = true)
    private String iyperiod;
    @ApiModelProperty(value = "对账状态(1.已对账)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "借方金额", hidden = true)
    private String md;
    @ApiModelProperty(value = "贷方金额", hidden = true)
    private String mc;
    @ApiModelProperty(value = "摘要", hidden = true)
    private String cdigest;
    @ApiModelProperty(value = "外币金额", hidden = true)
    private String nfrat;
    @ApiModelProperty(value = "外币方向", hidden = true)
    private String waiFangxiang;
    @ApiModelProperty(value = "对账标识号（唯一码）", hidden = true)
    private String statementNum;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong4;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong5;

}
