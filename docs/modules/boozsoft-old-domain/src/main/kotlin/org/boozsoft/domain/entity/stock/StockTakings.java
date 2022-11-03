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

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Table ( "stock_takings" )
@ApiModel(value="盘点单子表",description="盘点单子表")
public class StockTakings  implements Serializable{

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
	@ApiModelProperty(value = "现存量id",hidden = true)
	private String currentId;
	@ApiModelProperty(value = "存货编码",hidden = true)
	private String cinvode;
	@ApiModelProperty(value = "盘点单编号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "行次唯一码",hidden = true)
	private String lineCode;
	@ApiModelProperty(value = "盘点主数量 （10位小数点）",hidden = true)
	private BigDecimal quantityPd;
	@ApiModelProperty(value = "盘点1数量 （10位小数点）",hidden = true)
	private BigDecimal subPandian1;
	@ApiModelProperty(value = "计盘点2数量 （10位小数点）",hidden = true)
	private BigDecimal subPandian2;
	@ApiModelProperty(value = "盈亏主数量(正数表示盘盈，负数表示盘亏)",hidden = true)
	private BigDecimal quantityUk;
	@ApiModelProperty(value = "盈亏数量1(正数表示盘盈，负数表示盘亏)",hidden = true)
	private BigDecimal quantityUk1;
	@ApiModelProperty(value = "盈亏数量2(正数表示盘盈，负数表示盘亏)",hidden = true)
	private BigDecimal quantityUk2;
	@ApiModelProperty(value = "计量单位类型；单计量||多计量")
	private String cunitidType;
	@ApiModelProperty(value = "主计量数 （10位小数点）",hidden = true)
	private BigDecimal baseQuantity;
	@ApiModelProperty(value = "计量一数 （10位小数点）",hidden = true)
	private BigDecimal subQuantity1;
	@ApiModelProperty(value = "计量二数 （10位小数点）",hidden = true)
	private BigDecimal subQuantity2;
	@ApiModelProperty(value = "主计量",hidden = true)
	private String cunitid;
	@ApiModelProperty(value = "辅计量一",hidden = true)
	private String cunitidF1;
	@ApiModelProperty(value = "辅计量二",hidden = true)
	private String cunitidF2;
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
	@ApiModelProperty(value = "本币无税金额（4位小数点）",hidden = true)
	private String icost;
	@ApiModelProperty(value = "批次号",hidden = true)
	private String batchId;
	@ApiModelProperty(value = "批次入库日期",hidden = true)
	private String batchDate;
	@ApiModelProperty(value = "生产日期",hidden = true)
	private String dpdate;
	@ApiModelProperty(value = "失效日期",hidden = true)
	private String dvdate;
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
	private String uimtype;
	@Transient
	private String uimid;
	@Transient
	private String price;
}
