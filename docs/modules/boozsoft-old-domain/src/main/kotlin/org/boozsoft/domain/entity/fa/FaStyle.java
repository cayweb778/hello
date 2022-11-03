package org.boozsoft.domain.entity.fa;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：资产卡片样式表体", description = "资产卡片样式表体")
@Table("fa_style")
@Data
public class FaStyle {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "公司唯一码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "样式唯一码", hidden = true)
    private String styleUnique;
    @ApiModelProperty(value = "栏目名称", hidden = true)
    private String cname;
    @ApiModelProperty(value = "显示名称", hidden = true)
    private String showName;
    @ApiModelProperty(value = "字段名称", hidden = true)
    private String titleName;
    @ApiModelProperty(value = "名称长度（%）", hidden = true)
    private String nameLength;
    @ApiModelProperty(value = "输入框长度（%）", hidden = true)
    private String inputLength;
    @ApiModelProperty(value = "是否显示", hidden = true)
    private String isShow;
    @ApiModelProperty(value = "表体类型（基本信息、价值信息、折旧信息等）", hidden = true)
    private String titleType;
    @ApiModelProperty(value = "排序号", hidden = true)
    private String num;
    @ApiModelProperty(value = "页签名称", hidden = true)
    private String tabName;
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
