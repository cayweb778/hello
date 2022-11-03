package org.boozsoft.domain.entity.share;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@ApiModel(value = "数据表：财务报表模板栏目", description = "财务报表模板栏目")
@Table("report_template_column")
@Data
public class ReportTemplateColumn {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "模板id", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String templateId;
    @ApiModelProperty(value = "序号", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long xuhao;
    @ApiModelProperty(value = "栏目显示名称", hidden = true)
    private String columnShowName;
    @ApiModelProperty(value = "显示级次(1~4)", hidden = true)
    private String jici;
    @ApiModelProperty(value = "行次（一个模板内不允许重复）", hidden = true)
    private String hangci;
    @ApiModelProperty(value = "所属类型(仅资产负债表使用；资产、负债、权益)", hidden = true)
    private String columnType;
    @ApiModelProperty(value = "计算方式（1.行次；2.科目）", hidden = true)
    private String formulaMethod;
    @ApiModelProperty(value = "取值方式（余额；借方余额；贷方余额）", hidden = true)
    private String valueMethod;
    @ApiModelProperty(value = "计算公式", hidden = true)
    private String formulaCount;
    @ApiModelProperty(value = "是否系统模板（1.是）", hidden = true)
    private String sysFlag;
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
    @Transient
    @ApiModelProperty(value = "财务报表模板栏目", hidden = true)
    private List<ReportTemplateColumnFormula> formulaTable;

}
