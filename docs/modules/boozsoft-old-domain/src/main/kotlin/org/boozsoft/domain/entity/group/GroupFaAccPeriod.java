package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：资产期间管理表", description = "资产期间管理表")
@Table("_app_group_fa_acc_period")
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupFaAccPeriod {
    @ApiModelProperty(value = "主键id", position = 0)
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "资产唯一码", position = 1)
    private String uniqueCode;
    @ApiModelProperty(value = "类别唯一码", position = 1)
    private String classUniqueCode;
    @ApiModelProperty(value = "年度")
    private String iyear;
    @ApiModelProperty(value = "月份")
    private String imonth;
    @ApiModelProperty(value = "是否计提折旧（1是，0否，默认值为0）")
    private String isZhejiu;
    @ApiModelProperty(value = "月末结账前必须完成制单（1是，0否,默认值为0）")
    private String isFilledIn;
    @ApiModelProperty(value = "结账（1是，0否,默认值为0）")
    private String isSettle;


    private String beiyong1;
    private String beiyong2;
    private String beiyong3;
    private String beiyong4;
    private String beiyong5;

}


