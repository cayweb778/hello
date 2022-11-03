package org.boozsoft.domain.vo.stock;

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

/**
 * @Description  
 * @Author  myh
 * @Date 2022-04-09 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="存货期初余额表",description="存货期初余额表")
public class StockBeginBalanceVo implements Serializable {

	private String id;
	private String iyear;
	@ApiModelProperty(value = "行次(从1开始记数并排序）")
	private Long iineId;

	@ApiModelProperty(value = "录入日期")
	private String ddate;

	@ApiModelProperty(value = "存货档案表ID")
	private String stockId;

	@ApiModelProperty(value = "存货分类")
	private String stockClass;

	@ApiModelProperty(value = "制单人")
	private String cmaker;
	private String username;

	@ApiModelProperty(value = "计量单位类型；单计量||多计量")
	private String cunitidType;

	@ApiModelProperty(value = "主计量单位编码")
	private String cunitid;

	@ApiModelProperty(value = "辅计量单位一编码")
	private String cunitidF1;

	@ApiModelProperty(value = "辅计量单位二编码")
	private String cunitidF2;

	@ApiModelProperty(value = "主计量数量 （10位小数点）")
	private BigDecimal baseQuantity;

	@ApiModelProperty(value = "辅计量一数量 （10位小数点）")
	private BigDecimal subQuantity1;

	@ApiModelProperty(value = "辅计量二数量 （10位小数点）")
	private BigDecimal subQuantity2;

	@ApiModelProperty(value = "项目大类唯一码")
	private String citemClass;

	@ApiModelProperty(value = "项目唯一码")
	private String citemCode;

	@ApiModelProperty(value = "仓库1级名称")
	private String cwhcode1;

	private String cwhcode2;

	private String cwhcode3;

	private String cwhcode4;

	private String cwhcode5;

	private String cwhcode6;

	@ApiModelProperty(value = "税率（6位小数点）")
	private BigDecimal itaxrate;

	@ApiModelProperty(value = "无税单价（10位小数点）")
	private BigDecimal price;

	@ApiModelProperty(value = "含税单价（10位小数点）")
	private BigDecimal taxprice;

	@ApiModelProperty(value = "本币税额（4位小数点）")
	private BigDecimal itaxprice;

	@ApiModelProperty(value = "本币无税金额（4位小数点）")
	private BigDecimal icost;

	@ApiModelProperty(value = "本币价税合计（4位小数点）")
	private BigDecimal isum;

	@ApiModelProperty(value = "备注")
	private String cmemo;

	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）")
	private String bcheck;

	@ApiModelProperty(value = "审核时间")
	private String bcheckTime;

	@ApiModelProperty(value = "审核人")
	private String bcheckUser;
	private String bcheckUserName;

	@ApiModelProperty(value = "生产日期")
	private String dpdate;

	@ApiModelProperty(value = "失效日期")
	private String dvdate;
	@ApiModelProperty(value = "期初存储仓库 是否独立仓库 1是 0否")
	private String cangkuDuli;
	@ApiModelProperty(value = "期初存储仓库ID")
	private String stockCangkuId;
	@ApiModelProperty(value = "级别仓库ID")
	private String stockCangkuRecordId;
	@ApiModelProperty(value = "批号")
	private String batchNumber;
	@ApiModelProperty(value = "入库单号")
	private String stockRukuNumber;
	@ApiModelProperty(value = "入库日期")
	private String stockRukuDate;


	private String stockBarcode;
	private String stockNum;
	private String stockName;
	private String stockGgxh;
	private String stockUnitName;
	private String stockUnitName1;
	private String stockUnitName2;
	private String cwhcodeJoin;

	private String cnumber;
	private String cgUnitId;

}
