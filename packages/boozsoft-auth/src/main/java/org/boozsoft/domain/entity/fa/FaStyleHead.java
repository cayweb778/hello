package org.boozsoft.domain.entity.fa;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：资产卡片样式表头", description = "资产卡片样式表头")
@Table("fa_style_head")
@Data
public class FaStyleHead {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "公司唯一码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "样式唯一码", hidden = true)
    private String styleUnique;
    @ApiModelProperty(value = "样式编码", hidden = true)
    private String styleCode;
    @ApiModelProperty(value = "样式名称", hidden = true)
    private String styleName;
    @ApiModelProperty(value = "说明", hidden = true)
    private String remarks;
    @ApiModelProperty(value = "是否系统", hidden = true)
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

}
