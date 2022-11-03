package org.boozsoft.domain.entity.stock;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@Table( "stock_transfer" )
@ApiModel(value="调拨单主表",description="调拨单主表")
public class StockTransfer {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @Transient
    @ApiModelProperty(value = "公司唯一码",hidden = true)
    private String tenantId;
    @ApiModelProperty(value = "年度标志",hidden = true)
    private String iyear;
    @ApiModelProperty(value = "单据编号",hidden = true)
    private String ccode;
    @ApiModelProperty(value = "制单人",hidden = true)
    private String cmaker;
    @ApiModelProperty(value = "单据日期",hidden = true)
    private String ddate;
    @ApiModelProperty(value = "库管员编码唯一码（员工档案）",hidden = true)
    private String cwhcodeUser;
    @ApiModelProperty(value = "部门编码",hidden = true)
    private String cdepcode;
    @ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
    private String bcheck;
    @ApiModelProperty(value = "审核时间",hidden = true)
    private String bcheckTime;
    @ApiModelProperty(value = "审核人",hidden = true)
    private String bcheckUser;
    @ApiModelProperty(value = "备注",hidden = true)
    private String cmemo;
    @ApiModelProperty(value = "业务员编码",hidden = true)
    private String cpersoncode;
    @ApiModelProperty(value = "单据类型id （DBCKD调拨出库单）",hidden = true)
    private String billStyle;
    @ApiModelProperty(value = "调出仓库唯一码",hidden = true)
    private String cwhcode;
    @ApiModelProperty(value = "仓库级次一名称",hidden = true)
    private String cwhcode1;
    @ApiModelProperty(value = "仓库级次二名称",hidden = true)
    private String cwhcode2;
    @ApiModelProperty(value = "仓库级次三名称",hidden = true)
    private String cwhcode3;
    @ApiModelProperty(value = "仓库级次四名称",hidden = true)
    private String cwhcode4;
    @ApiModelProperty(value = "仓库级次五名称",hidden = true)
    private String cwhcode5;
    @ApiModelProperty(value = "仓库级次六名称",hidden = true)
    private String cwhcode6;

    @ApiModelProperty(value = "调入仓库唯一码",hidden = true)
    private String cwhcoderk;
    @ApiModelProperty(value = "仓库级次一名称",hidden = true)
    private String cwhcoderk1;
    @ApiModelProperty(value = "仓库级次二名称",hidden = true)
    private String cwhcoderk2;
    @ApiModelProperty(value = "仓库级次三名称",hidden = true)
    private String cwhcoderk3;
    @ApiModelProperty(value = "仓库级次四名称",hidden = true)
    private String cwhcoderk4;
    @ApiModelProperty(value = "仓库级次五名称",hidden = true)
    private String cwhcoderk5;
    @ApiModelProperty(value = "仓库级次六名称",hidden = true)
    private String cwhcoderk6;

    @ApiModelProperty(value = "",hidden = true)
    private String cfree1;
    @ApiModelProperty(value = "",hidden = true)
    private String cfree2;
    @ApiModelProperty(value = "",hidden = true)
    private String cfree3;
    @ApiModelProperty(value = "",hidden = true)
    private String cfree4;
    @ApiModelProperty(value = "",hidden = true)
    private String cfree5;

    @Transient
    @ApiModelProperty(value = "JSON子集",hidden = true)
    private String entryList;
    @Transient
    private List<StockTransfers> stsList;
}

