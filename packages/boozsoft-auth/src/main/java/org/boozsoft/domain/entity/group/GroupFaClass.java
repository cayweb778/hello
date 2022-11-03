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
@Table("_app_group_fa_class" )
@ApiModel(value="资产分类表",description="资产分类表")
public class GroupFaClass {
    @ApiModelProperty(value = "主键id", position = 0)
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;



    @ApiModelProperty(value = "唯一编码 ", position = 2)
    private String uniqueCustclass;

    @ApiModelProperty(value = "分类编码 ", position = 3)
    private String faClassId;

    @ApiModelProperty(value = "分类级次，与会计科目级次类似 ", position = 4)
    private String faClassGrade;

    @ApiModelProperty(value = "资产分类名称 ", position = 5)
    private String faCclassName;

    @ApiModelProperty(value = "客户分类级次编码（默认3位） ", position = 6)
    private String faGradeCode;

    @ApiModelProperty(value = "上级ID ", position = 7)
    private String parentId;

    @ApiModelProperty(value = "是否末级分类（1是，0否） ", position = 8)
    private String cusBend;

    @ApiModelProperty(value = "是否停用(1.启用;0停用) ", position = 9)
    private String flag;

    @ApiModelProperty(value = "使用年限（月） ", position = 10)
    private String faYears;

    @ApiModelProperty(value = "残值率 ", position = 11)
    private String faResRate;

    @ApiModelProperty(value = "折旧方法 ", position = 12)
    private String faDepMethod;

    @ApiModelProperty(value = "摊销方法 ", position = 13)
    private String faAmortization;

    @ApiModelProperty(value = "资产属性 ", position = 14)
    private String faProperty;

    @ApiModelProperty(value = "卡片样式 ", position = 15)
    private String faCardStyle;

    @ApiModelProperty(value = "进项税率 ", position = 16)
    private String faInputRate;

    private String beiyong1;

    private String beiyong2;

    private String beiyong3;

    private String beiyong4;

    private String beiyong5;

}
