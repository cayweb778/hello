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
@Table ( "stock_borrows" )
@ApiModel(value="借入借出zi表",description="借入借出zi表")
public class StockBorrows  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@Transient
	private String tenantId;

	private String iyear;

	private String ccode;

	@ApiModelProperty(value = "行次唯一码")
	private String lineCode;

	@ApiModelProperty(value = "行次(从1开始记数并排序）")
	private String lineId;

	@ApiModelProperty(value = "存货编码")
	private String cinvode;

	@ApiModelProperty(value = "部门编码")
	private String cdeptcode;

	@ApiModelProperty(value = "人员唯一码(人员档案）")
	private String cmaker;

	@ApiModelProperty(value = "计量单位编码")
	private String cgUnitId;

	@ApiModelProperty(value = "数量")
	private String cnumber;

	@ApiModelProperty(value = "主计量")
	private String cunitid;
	@ApiModelProperty(value = "主计量数 （10位小数点）")
	private String baseQuantity;

	@ApiModelProperty(value = "本币无税金额（4位小数点）")
	private String icost;

	@ApiModelProperty(value = "预计归还日期")
	private String yjdate;

	@ApiModelProperty(value = "累计入库主数量")
	private String rukuSum;

	@ApiModelProperty(value = "累计还回（归还）主数量")
	private String ljhhSum;

	@ApiModelProperty(value = "累计转换主数量")
	private String ljzhSum;
	@ApiModelProperty(value = "审核状态（1审核，1或其他未审）")
	private String bcheck;

	@ApiModelProperty(value = "审核人（操作员ID)")
	private String bcheckUser;

	@ApiModelProperty(value = "审核时间")
	private String bcheckTime;
	private String cmemo;
	@ApiModelProperty(value = "单据类型(借入借用JRJY、借入还回JRHH、借出借用JCYJ、借出归还JCGH、借入转换JRZH、借出转换JCZH)")
	private String borrowStyle;
	@ApiModelProperty(value = "批次")
	private String batchId;
}
