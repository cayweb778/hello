package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：自定义项设置", description = "自定义项设置")
@Table("_app_group_defined_file")
@Data
public class GroupDefinedFile {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "自定义项设置编码", hidden = true)
    private String definedCode;
    @ApiModelProperty(value = "自定义项设置名称", hidden = true)
    private String definedName;
    @ApiModelProperty(value = "备注", hidden = true)
    private String remarks;
    @ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "字段属性（1.文本；2.日期；3.整数；4.小数；5.是否）", hidden = true)
    private String shuxing;
    @ApiModelProperty(value = "数据类型", hidden = true)
    private String ctype;
    @ApiModelProperty(value = "小数位数", hidden = true)
    private String numWeishu;
    @ApiModelProperty(value = "数据范围(1.通用,0.模块)", hidden = true)
    private String scope;
    @ApiModelProperty(value = "模块(ZW总账,FA固定资产,STOCK存货)", hidden = true)
    private String model;
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
