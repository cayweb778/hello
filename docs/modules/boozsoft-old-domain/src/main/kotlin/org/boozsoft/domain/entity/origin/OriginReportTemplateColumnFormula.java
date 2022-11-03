package org.boozsoft.domain.entity.origin;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：财务报表模板栏目公式", description = "财务报表模板栏目公式")
@Table("_app_origin_report_template_column_formula")
@Data
public class OriginReportTemplateColumnFormula {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "模板id", hidden = true)
    private String templateId;
    @ApiModelProperty(value = "栏目id", hidden = true)
    private String columnId;
    @ApiModelProperty(value = "科目编码", hidden = true)
    private String ccode;
    @ApiModelProperty(value = "科目名称", hidden = true)
    private String ccodeName;
    @ApiModelProperty(value = "符号", hidden = true)
    private String fuhao;
    @ApiModelProperty(value = "方向", hidden = true)
    private String fangxiang;
    @ApiModelProperty(value = "取数规则（余额；借方发生额；贷方发生额）", hidden = true)
    private String rules;
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
    @ApiModelProperty(value = "组织id", hidden = true)
    private String originId;

}
