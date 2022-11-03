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

import java.math.BigDecimal;

@Data
@Table( "stock_transfers" )
@ApiModel(value="调拨单子表",description="调拨单子表")
public class StockTransfers {

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
    @ApiModelProperty(value = "行次唯一码",hidden = true)
    private String lineCode;
    @ApiModelProperty(value = "行次(从1开始记数并排序）",hidden = true)
    private String lineId;
    @ApiModelProperty(value = "单据类型id （DBCKD调拨出库单）",hidden = true)
    private String billStyle;
    @ApiModelProperty(value = "入库类别（收发方式中的收方向编码）",hidden = true)
    private String bstyle;
    @ApiModelProperty(value = "单据日期",hidden = true)
    private String ddate;
    @ApiModelProperty(value = "单据编号",hidden = true)
    private String ccode;
    @ApiModelProperty(value = "存货唯一码",hidden = true)
    private String cinvode;
    @ApiModelProperty(value = "批次",hidden = true)
    private String batchId;
    @ApiModelProperty(value = "制单人（操作员）",hidden = true)
    private String cmaker;
    @ApiModelProperty(value = "主计量单位编码",hidden = true)
    private String cunitid;
    @ApiModelProperty(value = "辅计量单位一编码",hidden = true)
    private String cunitidF1;
    @ApiModelProperty(value = "辅计量单位二编码",hidden = true)
    private String cunitidF2;
    @ApiModelProperty(value = "计量单位编码",hidden = true)
    private String unitId;
    @ApiModelProperty(value = "主计量数 （10位小数点）",hidden = true)
    private String baseQuantity;
    @ApiModelProperty(value = "计量一数 （10位小数点）",hidden = true)
    private String subQuantity1;
    @ApiModelProperty(value = "计量二数 （10位小数点）",hidden = true)
    private String subQuantity2;
    @ApiModelProperty(value = "项目大类唯一码",hidden = true)
    private String citemClass;
    @ApiModelProperty(value = "项目唯一码",hidden = true)
    private String citemcode;
    @ApiModelProperty(value = "税率（6位小数点）",hidden = true)
    private String itaxrate;
    @ApiModelProperty(value = "无税单价（10位小数点）",hidden = true)
    private String price;
    @ApiModelProperty(value = "本币无税金额（4位小数点）",hidden = true)
    private String icost;
    @ApiModelProperty(value = "部门唯一码",hidden = true)
    private String cdepcode;
    @ApiModelProperty(value = "备注",hidden = true)
    private String cmemo;
    @ApiModelProperty(value = "生产日期",hidden = true)
    private String dpdate;
    @ApiModelProperty(value = "失效日期",hidden = true)
    private String dvdate;
    @ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
    private String bcheck;
    @ApiModelProperty(value = "审核时间",hidden = true)
    private String bcheckTime;
    @ApiModelProperty(value = "审核人（操作员）",hidden = true)
    private String bcheckUser;
    @ApiModelProperty(value = "数量",hidden = true)
    private String cnumber;
    @ApiModelProperty(value = "计量单位编码",hidden = true)
    private String cgUnitId;

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


}

