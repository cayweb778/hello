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
 * @Date 2022-05-16 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_wuliu" )
@ApiModel(value="物流主表",description="")
public class StockWuliu  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	private String tenantId;

	private String iyear;

	private String ccode;

	@ApiModelProperty(value = "单据日期")
	private String ddate;

	@ApiModelProperty(value = "业务部门唯一码")
	private String cdepcode;

	@ApiModelProperty(value = "业务员编码唯一码（员工档案）")
	private String cpersoncode;

	@ApiModelProperty(value = "销售客户唯一码")
	private String cvencode;

	@ApiModelProperty(value = "联系人及电话")
	private String cuserTel;

	@ApiModelProperty(value = "送货地址")
	private String cAddress;

	@ApiModelProperty(value = "物流公司（供应商唯一码）")
	private String wuliuSup;

	@ApiModelProperty(value = "物流单号")
	private String wuliuId;

	@ApiModelProperty(value = "物流联系电话")
	private String wuliuTel;

	@ApiModelProperty(value = "备注")
	private String cmemo;

	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）")
	private String bcheck;

	@ApiModelProperty(value = "审核时间")
	private String bcheckTime;

	@ApiModelProperty(value = "制单人唯一码（操作员）")
	private String cmaker;

	@ApiModelProperty(value = "制单日期")
	private String cmakeDate;

	@ApiModelProperty(value = "主数量合计",hidden = true)
	private String squantity;
	@ApiModelProperty(value = "计量一合计",hidden = true)
	private String squantity1;
	@ApiModelProperty(value = "计量一合计",hidden = true)
	private String squantity2;
	@ApiModelProperty(value = "出库单",hidden = true)
	private String stockSaleousingCode;

	@Transient
	@ApiModelProperty(value = "JSON子集",hidden = true)
	private String entryList;

}
