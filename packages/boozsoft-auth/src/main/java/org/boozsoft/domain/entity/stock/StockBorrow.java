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
 * @Date 2022-10-14 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_borrow" )
@ApiModel(value="借入借出主表",description="借入借出主表")
public class StockBorrow  implements Serializable {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	private String tenantId;

	private String iyear;

	@ApiModelProperty(value = "单据类型(借入借用JRJY、借入还回JRHH、借出借用JCYJ、借出归还JCGH、借入转换JRZH、借出转换JCZH)")
	private String borrowStyle;

	@ApiModelProperty(value = "转换类型（转换成赠送ZS、借入转换成采购CG、借出转换成销售XS）")
	private String changeStyle;

	@ApiModelProperty(value = "单据编号")
	private String ccode;

	@ApiModelProperty(value = "单据日期")
	private String ddate;

	@ApiModelProperty(value = "制单人（ 操作员）")
	private String cmaker;

	@ApiModelProperty(value = "制单时间（年月日时分秒）")
	private String cmakerTime;

	@ApiModelProperty(value = "业务员编码唯一码（人员档案）")
	private String cdepcode;
	private String cpersoncode;

	@ApiModelProperty(value = "借用单位（单位类型对应唯一码，其他则记录内容）")
	private String unitValue;

	@ApiModelProperty(value = "单位类型（客户、供应商、人员、项目、其他）")
	private String unitType;

	@ApiModelProperty(value = "费用金额（4位小数点）")
	private String feiyongJe;

	@ApiModelProperty(value = "是否期初单据")
	private String isQichu;

	@ApiModelProperty(value = "是否关闭")
	private String isClose;

	@ApiModelProperty(value = "审核状态（1审核，1或其他未审）")
	private String bcheck;

	@ApiModelProperty(value = "审核人（操作员ID)")
	private String bcheckUser;

	@ApiModelProperty(value = "审核时间")
	private String bcheckTime;

	@ApiModelProperty(value = "备注")
	private String cmemo;

	@Transient
	@ApiModelProperty(value = "JSON子集",hidden = true)
	private String entryList;
}
