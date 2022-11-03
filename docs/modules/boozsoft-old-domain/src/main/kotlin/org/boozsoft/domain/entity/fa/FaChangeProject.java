package org.boozsoft.domain.entity.fa;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：资产卡片项目变动表", description = "资产卡片项目变动表")
@Table("fa_change_project")
@Data
public class FaChangeProject {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "公司唯一码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "管理代码", hidden = true)
    private String manageCode;
    @ApiModelProperty(value = "卡片唯一码", hidden = true)
    private String cardUnique;
    @ApiModelProperty(value = "项目唯一码", hidden = true)
    private String projectUnique;
    @ApiModelProperty(value = "所占比例（%，正整数）", hidden = true)
    private String proportion;
    @ApiModelProperty(value = "变动时间", hidden = true)
    private String cdate;
    @ApiModelProperty(value = "操作员唯一码", hidden = true)
    private String cuserId;
    @ApiModelProperty(value = "变动年", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "变动月", hidden = true)
    private String imonth;
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

    @ApiModelProperty(value = "变动关联ID", hidden = true)
    private String superiorId;
    @ApiModelProperty(value = "是否多项目 1 是", hidden = true)
    private String isMany;
}
