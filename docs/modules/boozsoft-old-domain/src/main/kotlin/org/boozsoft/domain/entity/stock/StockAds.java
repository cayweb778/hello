package org.boozsoft.domain.entity.stock;

import lombok.Data;
import java.io.Serializable;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModel;
import org.springframework.data.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-10-29 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_ads" )
@ApiModel(value="",description="")
public class StockAds  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	private String tenantId;

	private String iyear;

	@ApiModelProperty(value = "行次唯一码")
	private String lineCode;

	@ApiModelProperty(value = "行次(从1开始记数并排序）")
	private String lineId;

	@ApiModelProperty(value = "单据类型(ZZ组装、CX拆卸)")
	private String adStyle;

	@ApiModelProperty(value = "属性（F父件、C子件）")
	private String fcStyle;

	private String ddate;

	private String ccode;

	@ApiModelProperty(value = "存货")
	private String cinvode;

	@ApiModelProperty(value = "计量单位")
	private String cgUnitId;
	@ApiModelProperty(value = "主计量单位")
	private String cunitid;

	private String cnumber;

	private String baseQuantity;

	@ApiModelProperty(value = "项目大类唯一码")
	private String citemClass;
	@ApiModelProperty(value = "项目")
	private String citem;

	@ApiModelProperty(value = "材料无税单价（10位小数点）")
	private String price;

	@ApiModelProperty(value = "材料无税金额（4位小数点）")
	private String icost;

	@ApiModelProperty(value = "费用分摊金额")
	private String fyftIsum;

	@ApiModelProperty(value = "合计金额")
	private String icostTotal;

	private String cdepcode;

	@ApiModelProperty(value = "业务员编码唯一码（人员档案）")
	private String cpersoncode;

	private String cmemo;

	@ApiModelProperty(value = "质检状态")
	private String cgspstate;

	@ApiModelProperty(value = "质检日期")
	private String sgspdate;

	@ApiModelProperty(value = "质检人（操作员）")
	private String sgspperson;

	private String bcheck;

	private String bcheckTime;

	private String bcheckUser;

	@ApiModelProperty(value = "出入库单号")
	private String rukuCode;
	@ApiModelProperty(value = "定额损耗")
	private String deshMoney;
	@ApiModelProperty(value = "实际损耗")
	private String sjshMoney;
}
