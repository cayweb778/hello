package org.boozsoft.domain.entity.fa;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：资产处置主表", description = "资产处置主表")
@Table("fa_assets_minus_master")
@Data
public class FaAssetsMinusMaster {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "管理ID", hidden = true)
    private String manageCode;
    @ApiModelProperty(value = "制单日期", hidden = true)
    private String createDate;
    @ApiModelProperty(value = "处置日期", hidden = true)
    private String handleDate;
    @ApiModelProperty(value = "单据编号", hidden = true)
    private String czId;
    @ApiModelProperty(value = "处置方式", hidden = true)
    private String czMethod;
    @ApiModelProperty(value = "处置人", hidden = true)
    private String czUser;
    @ApiModelProperty(value = "处置部门", hidden = true)
    private String czDept;
    @ApiModelProperty(value = "制单人（操作员名称）")
    private String makerUsername;
    @ApiModelProperty(value = "审核人（操作员名称）")
    private String verifyUsername;
    @ApiModelProperty(value = "审核时间")
    private String verifyTime;
    @ApiModelProperty(value = "状态")
    private String status;


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
