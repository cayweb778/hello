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

@ApiModel(value = "数据表：会计准则对应报表", description = "会计准则对应报表")
@Table("_app_group_report_acc")
@Data
public class GroupReportAcc {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "会计准则唯一码", hidden = true)
    private String accStandard;
    @ApiModelProperty(value = "报表名称对应字段（资产负债表、利润表、现金流量表）", hidden = true)
    private String reportName;
    @ApiModelProperty(value = "报表中文名称（资产负债表、利润表、现金流量表）", hidden = true)
    private String reportChName;
    @ApiModelProperty(value = "排序号", hidden = true)
    private Integer num;

}
