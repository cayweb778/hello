package org.boozsoft.domain.entity.fa;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：资产处置子表", description = "资产处置子表")
@Table("fa_assets_minus_sub")
@Data
public class FaAssetsMinusSub {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "主表ID", hidden = true)
    private String superiorId;
    @ApiModelProperty(value = "资产卡片唯一码", hidden = true)
    private String assetsUniqueCode;
    @ApiModelProperty(value = "资产卡片系统编号", hidden = true)
    private String sysId;
    @ApiModelProperty(value = "资产卡片编码", hidden = true)
    private String assetsCode;
    @ApiModelProperty(value = "资产卡片名称", hidden = true)
    private String assetsName;
    @ApiModelProperty(value = "规格型号", hidden = true)
    private String speciType;
    @ApiModelProperty(value = "数量", hidden = true)
    private String bookAmount;
    @ApiModelProperty(value = "计量单位", hidden = true)
    private String measureUnit;
    @ApiModelProperty(value = "原值", hidden = true)
    private String yuanzhi;
    @ApiModelProperty(value = "本月计提", hidden = true)
    private String jtBy;
    @ApiModelProperty(value = "累计折旧", hidden = true)
    private String zjLj;
    @ApiModelProperty(value = "净值", hidden = true)
    private String jingzhi;
    @ApiModelProperty(value = "处置方式", hidden = true)
    private String minusMethod;
    @ApiModelProperty(value = "清理收入", hidden = true)
    private String clearIncome;
    @ApiModelProperty(value = "清理费用", hidden = true)
    private String clearFee;

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
