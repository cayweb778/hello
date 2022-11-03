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
@Table("fa_evaluate_sub")
@Data
public class FaEvaluateSub {

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
    @ApiModelProperty(value = "原值", hidden = true)
    private String originalValue;
    @ApiModelProperty(value = "累计折旧", hidden = true)
    private String accumulatedDepreciation;
    @ApiModelProperty(value = "净值", hidden = true)
    private String netWorth;
    @ApiModelProperty(value = "使用年限", hidden = true)
    private String serviceLife;
    @ApiModelProperty(value = "净残值率", hidden = true)
    private String netWorthRate;
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
