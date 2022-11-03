package org.boozsoft.domain.entity.fa;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Optional;

@ApiModel(value = "数据表：资产评估主表", description = "资产评估主表")
@Table("fa_evaluate_master")
@Data
public class FaEvaluateMaster {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "管理ID", hidden = true)
    private String manageCode;
    @ApiModelProperty(value = "评估日期", hidden = true)
    private String pgDate;
    @ApiModelProperty(value = "单据编号", hidden = true)
    private String pgId;
    @ApiModelProperty(value = "评估说明", hidden = true)
    private String pgMethod;
    @ApiModelProperty(value = "制单人（操作员名称）")
    private String makerUsername;
    @ApiModelProperty(value = "制单时间", hidden = true)
    private String makerTime;
    @ApiModelProperty(value = "审核人（操作员名称）")
    private String verifyUsername;
    @ApiModelProperty(value = "审核时间")
    private String verifyTime;

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
