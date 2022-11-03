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

@Data
@Table ( "stock_change" )
@Accessors(chain = true)
@ApiModel(value="形态转换单主表",description="形态转换单主表")
public class StockChange {

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
	@ApiModelProperty(value = "业务员编码唯一码（员工档案）",hidden = true)
	private String cpersoncode;
	@ApiModelProperty(value = "想目编码",hidden = true)
	private String projectcode;
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
	@ApiModelProperty(value = "费用金额",hidden = true)
	private String fymoney;
	@ApiModelProperty(value = "差异金额",hidden = true)
	private String cymoney;
	@ApiModelProperty(value = "单据类型id （XTZHCKD形态转换出库单）",hidden = true)
	private String billStyle;
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

}
