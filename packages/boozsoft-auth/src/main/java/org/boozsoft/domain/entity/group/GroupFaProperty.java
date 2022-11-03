package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("_app_group_fa_property")
@ApiModel(value = "资产属性表", description = "资产属性表")
public class GroupFaProperty {

    @ApiModelProperty(value = "主键id", position = 0)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    private String id;

    @ApiModelProperty(value = "唯一编码 ", position = 2)
    private String uniqueCustclass;

    @ApiModelProperty(value = "属性编码 ", position = 3)
    private String peopertyId;

    @ApiModelProperty(value = "资产分类名称 ", position = 4)
    private String peopertyName;

    @ApiModelProperty(value = "是否计提(1.是;0否) ", position = 5)
    private String flag;

    @ApiModelProperty(value = "计提方式 ", position = 6)
    private String faDepMethod;

    @ApiModelProperty(value = " 状态 ", position = 7)
    private String status;

    private String beiyong1;

    private String beiyong2;

    private String beiyong3;

    private String beiyong4;

    private String beiyong5;

}
