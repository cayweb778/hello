package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：银行对账表", description = "银行对账表")
@Table("bank_statement")
@Data
public class BankStatement {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "对账日期", hidden = true)
    private String statementDate;
    @ApiModelProperty(value = "会计科目编码", hidden = true)
    private String kemuCode;
    @ApiModelProperty(value = "对方单位", hidden = true)
    private String duifangUnit;
    @ApiModelProperty(value = "结算方式", hidden = true)
    private String settModes;
    @ApiModelProperty(value = "票号", hidden = true)
    private String piaohao;
    @ApiModelProperty(value = "借方金额", hidden = true)
    private String jie;
    @ApiModelProperty(value = "贷方金额", hidden = true)
    private String dai;
    @ApiModelProperty(value = "方向", hidden = true)
    private String fangxiang;
    @ApiModelProperty(value = "对账标识(1.已对账)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "对账标识号", hidden = true)
    private String statementNum;
    @ApiModelProperty(value = "摘要", hidden = true)
    private String remarks;
    @ApiModelProperty(value = "币种", hidden = true)
    private String currencyId;
    @ApiModelProperty(value = "对账期初（年度+00表示，如202100）", hidden = true)
    private String qichu;
    @ApiModelProperty(value = "年度", hidden = true)
    private String iyear;
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
