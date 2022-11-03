package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：财务收据表头回滚表", description = "财务收据表头回滚表")
@Table("receipt_header_rollback")
@Data
public class ReceiptHeaderRollback {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "唯一编码", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long uniqueCode;
    @ApiModelProperty(value = "收据编码", hidden = true)
    private String receCode;
    @ApiModelProperty(value = "收据名称", hidden = true)
    private String receName;
    @ApiModelProperty(value = "财务收据分类", hidden = true)
    private String receClass;
    @ApiModelProperty(value = "开票日期", hidden = true)
    private String receDate;
    @ApiModelProperty(value = "购方名称", hidden = true)
    private String buyName;
    @ApiModelProperty(value = "销方名称", hidden = true)
    private String salesName;
    @ApiModelProperty(value = "开票人", hidden = true)
    private String recePsn;
    @ApiModelProperty(value = "合计金额", hidden = true)
    private String totalAmount;
    @ApiModelProperty(value = "对应凭证号（唯一标识）", hidden = true)
    private String vouchCode;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "变动日期", hidden = true)
    private String biandongDate;
    @ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", hidden = true)
    private String biandongMethod;
    @ApiModelProperty(value = "操作员姓名", hidden = true)
    private String biandongName;
    @ApiModelProperty(value = "操作员唯一标识", hidden = true)
    private String biandongUniqueCode;
    @ApiModelProperty(value = "变动id", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String biandongId;

}
