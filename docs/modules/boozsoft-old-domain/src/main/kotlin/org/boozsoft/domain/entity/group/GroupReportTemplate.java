package org.boozsoft.domain.entity.group;

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

@ApiModel(value = "数据表：财务报表模板", description = "财务报表模板")
@Table("_app_group_report_template")
@Data
public class GroupReportTemplate {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "报表名称（资产负债表、利润表、现金流量表）", hidden = true)
    private String reportName;
    @ApiModelProperty(value = "模板名称", hidden = true)
    private String templateName;
    @ApiModelProperty(value = "会计准则", hidden = true)
    private String accStandard;
    @ApiModelProperty(value = "标题名称", hidden = true)
    private String titleName;
    @ApiModelProperty(value = "科目模板id", hidden = true)
    private String kemuTemplateId;
    @ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "自定义菜单1", hidden = true)
    private String menu1;
    @ApiModelProperty(value = "自定义菜单2", hidden = true)
    private String menu2;
    @ApiModelProperty(value = "自定义菜单3", hidden = true)
    private String menu3;
    @ApiModelProperty(value = "自定义菜单4", hidden = true)
    private String menu4;
    @ApiModelProperty(value = "自定义菜单5", hidden = true)
    private String menu5;
    @ApiModelProperty(value = "自定义菜单6（报表编码）", hidden = true)
    private String menu6;
    @ApiModelProperty(value = "是否系统模板（1.是）", hidden = true)
    private String sysFlag;
    @ApiModelProperty(value = "排序号", hidden = true)
    private Integer num;
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
    private List<GroupReportTemplateColumn> table;
    @Transient
    @ApiModelProperty(value = "财务报表模板栏目", hidden = true)
    private List<GroupReportTemplateColumn> zcTable;
    @Transient
    @ApiModelProperty(value = "财务报表模板栏目", hidden = true)
    private List<GroupReportTemplateColumn> fzTable;

}
