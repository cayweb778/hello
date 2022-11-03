package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_currentstock" )
@ApiModel(value="现存量表",description="现存量表")
public class StockCurrentstock implements Serializable {

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
	@ApiModelProperty(value = "存货唯一码",hidden = true)
	private String invCode;
	@ApiModelProperty(value = "仓库唯一码【期初存储仓库】",hidden = true)
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
	@ApiModelProperty(value = "辅计量一换算率",hidden = true)
	private String cunitidF1;
	@ApiModelProperty(value = "辅计量二换算率",hidden = true)
	private String cunitidF2;
	@ApiModelProperty(value = "计量单位类型；单计量||多计量")
	private String cunitidType;
	@ApiModelProperty(value = "主计量数 （10位小数点）",hidden = true)
	private BigDecimal baseQuantity;
	@ApiModelProperty(value = "成本金额",hidden = true)
	private BigDecimal money;
	@ApiModelProperty(value = "成本单价",hidden = true)
	private BigDecimal price;
	@ApiModelProperty(value = "批次号",hidden = true)
	private String batchId;
	@ApiModelProperty(value = "批次入库日期",hidden = true)
	private String batchDate;
	@ApiModelProperty(value = "生产日期",hidden = true)
	private String dpdate;
	@ApiModelProperty(value = "失效日期",hidden = true)
	private String dvdate;
	@ApiModelProperty(value = "在途入库-采购到货主数量（10位小数） ",hidden = true)
	private BigDecimal ztrkQuantityCgdh;
	@ApiModelProperty(value = "在途入库-采购入库单主数量（10位小数） ",hidden = true)
	private BigDecimal ztrkQuantityCgrk;
	@ApiModelProperty(value = "在途入库-其他入库单主数量（10位小数） ",hidden = true)
	private BigDecimal ztrkQuantityQtrk;
	@ApiModelProperty(value = "在途入库-产成品入库单主数量（10位小数） ",hidden = true)
	private BigDecimal ztrkQuantityCcprk;
	@ApiModelProperty(value = "在途入库-借入单主数量（10位小数） ",hidden = true)
	private BigDecimal ztrkQuantityInt;
	@ApiModelProperty(value = "在途出库-销货单主数量（10位小数） ",hidden = true)
	private BigDecimal ztckQuantityXhd;
	@ApiModelProperty(value = "在途出库-销售出库单主数量（10位小数） ",hidden = true)
	private BigDecimal ztckQuantityXsck;
	@ApiModelProperty(value = "在途出库-材料出库单主数量（10位小数） ",hidden = true)
	private BigDecimal ztckQuantityClly;
	@ApiModelProperty(value = "在途出库-其他出库单主数量（10位小数） ",hidden = true)
	private BigDecimal ztckQuantityQtck;
	@ApiModelProperty(value = "在途出库-借出单主数量（10位小数）",hidden = true)
	private BigDecimal ztckQuantityOut;


	@ApiModelProperty(value = "乐观锁 1开启 0关闭",hidden = true)
	private Long revision;
	@ApiModelProperty(value = "锁创建时间",hidden = true)
	private String lockCreatedTime;
	@ApiModelProperty(value = "锁创建人",hidden = true)
	private String lockCreatedUser;
	@ApiModelProperty(value = "锁原因,单据首字母【KCQCYE】",hidden = true)
	private String lockReason;

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
	@ApiModelProperty(value = "",hidden = true)
	private String cfree6;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree7;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree8;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree9;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree10;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree11;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree12;

}
