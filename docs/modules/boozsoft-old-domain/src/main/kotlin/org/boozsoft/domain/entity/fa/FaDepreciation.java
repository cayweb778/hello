package org.boozsoft.domain.entity.fa;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：资产卡片折旧表", description = "资产卡片折旧表")
@Table("fa_depreciation")
@Data
public class FaDepreciation {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "公司唯一码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "管理代码", hidden = true)
    private String manageCode;
    @ApiModelProperty(value = "卡片唯一码", hidden = true)
    private String cardUnique;
    @ApiModelProperty(value = "年度（自然年度）", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "月份（自然月份）", hidden = true)
    private String imonth;
    @ApiModelProperty(value = "本月折旧", hidden = true)
    private String zjBy;
    @ApiModelProperty(value = "累计折旧", hidden = true)
    private String zjLj;
    @ApiModelProperty(value = "本年折旧", hidden = true)
    private String zjBn;
    @ApiModelProperty(value = "本月工作量", hidden = true)
    private String gzlBy;
    @ApiModelProperty(value = "累计工作量", hidden = true)
    private String gzlLj;
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
