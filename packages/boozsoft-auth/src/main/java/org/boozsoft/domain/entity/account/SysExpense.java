package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ( "sys_expense" )
@ApiModel(value="费用档案表",description="费用档案表")
public class SysExpense {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @Transient
    @ApiModelProperty(value = "公司唯一码",hidden = true)
    private String tenantId;
    @ApiModelProperty(value = "费用编码",hidden = true)
    private String ccode;
    @ApiModelProperty(value = "费用名称",hidden = true)
    private String cname;
    @ApiModelProperty(value = "费用类型(CGFY采购费用、XSFY销售费用、SCFY生产费用、QTFY其他费用)",hidden = true)
    private String ctype;
    @ApiModelProperty(value = "税率",hidden = true)
    private String itax;
    @ApiModelProperty(value = "是否分摊",hidden = true)
    private String isFentan;
    @ApiModelProperty(value = "分摊方式(1按金额、2按数量)",hidden = true)
    private String fentanType;
    @ApiModelProperty(value = "状态",hidden = true)
    private String flag;
    @ApiModelProperty(value = "",hidden = true)
    private String cfree1;
    @ApiModelProperty(value = "",hidden = true)
    private String cfree2;
    @ApiModelProperty(value = "",hidden = true)
    private String cfree3;
    @ApiModelProperty(value = "",hidden = true)
    private String cfree4;
    @ApiModelProperty(value = "",hidden = true)
    private String cfree5;

}


