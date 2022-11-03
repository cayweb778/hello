package org.boozsoft.domain.entity.share;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel(value = "数据表：财务报表数据", description = "财务报表数据")
@Table("report_data")
@Data
public class ReportData {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "报表名称（资产负债表、利润表、现金流量表）", hidden = true)
    private String reportName;
    @ApiModelProperty(value = "模板id", hidden = true)
    private String templateId;
    @ApiModelProperty(value = "模板名称", hidden = true)
    private String templateName;
    @ApiModelProperty(value = "会计准则", hidden = true)
    private String accStandard;
    @ApiModelProperty(value = "科目模板id", hidden = true)
    private String kemuTemplateId;
    @ApiModelProperty(value = "标题名称", hidden = true)
    private String titleName;
    @ApiModelProperty(value = "报表编码", hidden = true)
    private String dataCode;
    @ApiModelProperty(value = "查询类型（1.月；2.季度；3.年）", hidden = true)
    private String dataType;
    @ApiModelProperty(value = "年度", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "季度", hidden = true)
    private String jidu;
    @ApiModelProperty(value = "会计区间", hidden = true)
    private String iperiod;
    @ApiModelProperty(value = "查询日期", hidden = true)
    private String idate;
    @ApiModelProperty(value = "创建人", hidden = true)
    private String createUser;
    @ApiModelProperty(value = "创建日期", hidden = true)
    @CreatedDate
    private LocalDateTime createDate;
    @ApiModelProperty(value = "审核人", hidden = true)
    private String editUser;
    @ApiModelProperty(value = "审核日期", hidden = true)
    private String editDate;
    @ApiModelProperty(value = "审核状态（1.审核）", hidden = true)
    private String flag;
    @ApiModelProperty(value = "锁定状态（1.锁定）", hidden = true)
    private String lockFlag;
    @ApiModelProperty(value = "锁定人", hidden = true)
    private String lockUser;
    @ApiModelProperty(value = "锁定日期", hidden = true)
    private String lockDate;
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
    @ApiModelProperty(value = "财务报表数据栏目", hidden = true)
    private List<ReportDataColumn> table;
    @Transient
    @ApiModelProperty(value = "财务报表数据资产栏目", hidden = true)
    private List<ReportDataColumn> zcTable;
    @Transient
    @ApiModelProperty(value = "财务报表数据负债栏目", hidden = true)
    private List<ReportDataColumn> fzTable;

}
