package org.boozsoft.domain.entity.share;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：现金流量项目对应科目", description = "现金流量项目对应科目")
@Table("project_cash_code")
@Data
public class ProjectCashCode {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "项目编码", hidden = true)
    private String projectCode;
    @ApiModelProperty(value = "年度", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "对应借方科目编码（末级科目）", hidden = true)
    private String jieCode;
    @ApiModelProperty(value = "对应贷方科目编码（末级科目）", hidden = true)
    private String daiCode;
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
