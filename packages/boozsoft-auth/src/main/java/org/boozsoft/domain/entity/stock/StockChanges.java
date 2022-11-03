package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Table ( "stock_changes" )
@Accessors(chain = true)
@ApiModel(value="形态转换单子表",description="形态转换单子表")
public class StockChanges {

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

	@ApiModelProperty(value = "单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "单据编号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "部门唯一码",hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "仓库唯一码",hidden = true)
	private String cwhcode;
	@ApiModelProperty(value = "存货唯一码",hidden = true)
	private String cinvode;
	@ApiModelProperty(value = "是否独立仓库（ 1是 0否）",hidden = true)
	private String cangkuDuli;
	@ApiModelProperty(value = "批次",hidden = true)
	private String batchId;
	@ApiModelProperty(value = "制单人（操作员）",hidden = true)
	private String cmaker;
	@ApiModelProperty(value = "主计量单位编码",hidden = true)
	private String cunitid;
	@ApiModelProperty(value = "计量单位编码",hidden = true)
	private String unitId;
	private String cunitidF1;
	@ApiModelProperty(value = "辅计量单位二编码",hidden = true)
	private String cunitidF2;
	@ApiModelProperty(value = "主计量数 （10位小数点）",hidden = true)
	private String baseQuantity;
	@ApiModelProperty(value = "计量一数 （10位小数点）",hidden = true)
	private String subQuantity1;
	@ApiModelProperty(value = "计量二数 （10位小数点）",hidden = true)
	private String subQuantity2;
	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
	private String bcheck;
	@ApiModelProperty(value = "审核时间",hidden = true)
	private String bcheckTime;
	@ApiModelProperty(value = "审核人（操作员）",hidden = true)
	private String bcheckUser;
	@ApiModelProperty(value = "单据类型id （XTZHCKD形态转换出库单）",hidden = true)
	private String billStyle;
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

	@ApiModelProperty(value = "无税单价（10位小数点）",hidden = true)
	private String price;
	@ApiModelProperty(value = "无税金额（10位小数点）",hidden = true)
	private String icost;
	@ApiModelProperty(value = "费用金额 （10位小数点）",hidden = true)
	private String fyprice;

	@ApiModelProperty(value = "备注",hidden = true)
	private String cmemo;
	@ApiModelProperty(value = "生产日期",hidden = true)
	private String dpdate;
	@ApiModelProperty(value = "失效日期",hidden = true)
	private String dvdate;
	@ApiModelProperty(value = "1转换后0转换前",hidden = true)
	private String flgs;
	@ApiModelProperty(value = "当时填写的现存量数 （10位小数点）",hidden = true)
	private String xcl;

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
	@ApiModelProperty(value = "cwhcode1名称Join",hidden = true)
	private String cwhcodeNameJoin;
	@Transient
	private String kcKylCg;
	@Transient
	private String oldBaseQuantity;
}
