package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：银行对账单模板栏目", description = "银行对账单模板栏目")
@Table("bank_template_column")
@Data
public class BankTemplateColumn {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "主表id", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String templateId;
    @ApiModelProperty(value = "系统字段名称", hidden = true)
    private String systemField;
    @ApiModelProperty(value = "对应表字段名称", hidden = true)
    private String tableField;
    @ApiModelProperty(value = "银行对账单名称", hidden = true)
    private String billField;
    @ApiModelProperty(value = "类型", hidden = true)
    private String billType;
    @ApiModelProperty(value = "样式", hidden = true)
    private String billStyle;
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
