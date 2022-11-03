package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "账套月末结账", description = "账套月末结账")
@Table("acc_close")
@Data
public class AccClose {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty(value = "会计年度")
    private String iyear;

    @ApiModelProperty(value = "会计月份")
    private String imonth;

    @ApiModelProperty(value = "会计期间")
    private String period;

    @ApiModelProperty(value = "总账")
    private String gl;

    @ApiModelProperty(value = "结账操作员")
    private String glUser;

    @ApiModelProperty(value = "结账时间")
    private String glTime;

    @ApiModelProperty(value = "应付")
    private String ap;

    @ApiModelProperty(value = "结账操作员")
    private String apUser;

    @ApiModelProperty(value = "结账时间")
    private String apTime;

    @ApiModelProperty(value = "应收")
    private String ar;

    @ApiModelProperty(value = "结账操作员")
    private String arUser;

    @ApiModelProperty(value = "结账时间")
    private String arTime;

    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;


}
