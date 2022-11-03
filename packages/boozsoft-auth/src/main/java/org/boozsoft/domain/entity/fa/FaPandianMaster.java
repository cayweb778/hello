package org.boozsoft.domain.entity.fa;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：资产盘点主表", description = "资产盘点主表")
@Table("fa_pandian_master")
@Data
public class FaPandianMaster {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "管理代码")
    private String manageCode;
    @ApiModelProperty(value = "盘点截止日期")
    private String endDate;
    @ApiModelProperty(value = "单据编号")
    private String pdId;
    @ApiModelProperty(value = "盘点方式")
    private String pdMethod;
    @ApiModelProperty(value = "盘点范围")
    private String pdRange;
    @ApiModelProperty(value = "盘点部门唯一码")
    private String pdDept;
    @ApiModelProperty(value = "职员唯一码")
    private String pdUsername;
    @ApiModelProperty(value = "监督盘点部门唯一码")
    private String pdjdDept;
    @ApiModelProperty(value = "监督盘点职员唯一码")
    private String pdjdUsername;
    @ApiModelProperty(value = "是否做过盘盈处理（1为是，其他为否）")
    private String isPy;
    @ApiModelProperty(value = "是否做过盘亏处理（1为是，其他为否）")
    private String isPk;
    @ApiModelProperty(value = "状态0 暂存 1正常")
    private String status;
    @ApiModelProperty(value = "审核人（操作员名称）")
    private String verifyUsername;
    @ApiModelProperty(value = "审核时间")
    private String verifyTime;
    @ApiModelProperty(value = "制单人")
    private String makerUsername;
    @ApiModelProperty(value = "制单时间")
    private String makerTime;
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
