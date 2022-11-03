package org.boozsoft.domain.entity.fa;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：资产盘点子表", description = "资产盘点子表")
@Table("fa_pandian_sub")
@Data
public class FaPandianSub {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "主表ID", hidden = true)
    private String superiorId;
    @ApiModelProperty(value = "卡片唯一码", hidden = true)
    private String cardUnique;
    @ApiModelProperty(value = "年度", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "月份", hidden = true)
    private String imonth;
    @ApiModelProperty(value = "账面数量", hidden = true)
    private String bookAmount;
    @ApiModelProperty(value = "实际数量", hidden = true)
    private String realityAmount;
    @ApiModelProperty(value = "存放账面数量", hidden = true)
    private String debookAmount;
    @ApiModelProperty(value = "存放实际数量", hidden = true)
    private String derealityAmount;
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
