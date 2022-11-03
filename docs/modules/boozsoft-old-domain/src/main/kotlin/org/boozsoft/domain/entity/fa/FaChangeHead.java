package org.boozsoft.domain.entity.fa;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：资产卡片变动单表头", description = "资产卡片变动单表头")
@Table("fa_change_head")
@Data
public class FaChangeHead {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "公司唯一码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "管理代码", hidden = true)
    private String manageCode;
    @ApiModelProperty(value = "单据编号", hidden = true)
    private String changeCode;
    @ApiModelProperty(value = "变动日期", hidden = true)
    private String changeDate;
    @ApiModelProperty(value = "年", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "月", hidden = true)
    private String imonth;
    @ApiModelProperty(value = "变动原因", hidden = true)
    private String changeCause;
    @ApiModelProperty(value = "变动内容", hidden = true)
    private String changeContent;
    @ApiModelProperty(value = "变动字段1", hidden = true)
    private String changeTitle1;
    @ApiModelProperty(value = "变动字段2", hidden = true)
    private String changeTitle2;
    @ApiModelProperty(value = "变动字段3", hidden = true)
    private String changeTitle3;

    @ApiModelProperty(value = "制单人（操作员名称）")
    private String makerUsername;
    @ApiModelProperty(value = "制单时间")
    private String makerTime;

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
