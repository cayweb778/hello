package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table ( "ar_ap_expense" )
@ApiModel(value="费用主表",description="费用主表")
public class ArApExpense {

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
	@ApiModelProperty(value = "单据编号(BE-202204-0001)",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "制单人唯一码（操作员）",hidden = true)
	private String cmaker;
	@ApiModelProperty(value = "业务类型",hidden = true)
	private String ctype;
	@ApiModelProperty(value = "票据类型",hidden = true)
	private String pjType;
	@ApiModelProperty(value = "往来单位",hidden = true)
	private String cvencode;
	@ApiModelProperty(value = "记账方向（ar应收、ap应付）",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "业务部门唯一码",hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "业务员唯一码（员工档案）",hidden = true)
	private String cpersoncode;
	@ApiModelProperty(value = "现结金额",hidden = true)
	private String isum;
	@ApiModelProperty(value = "使用预付",hidden = true)
	private String useYf;
	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
	private String bcheck;
	@ApiModelProperty(value = "审核时间",hidden = true)
	private String bcheckTime;
	@ApiModelProperty(value = "审核人唯一码（操作员）",hidden = true)
	private String bcheckUser;
	@ApiModelProperty(value = "备注",hidden = true)
	private String cmemo;
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

	@ApiModelProperty(value = "制单日期", hidden = true)
	@CreatedDate
	private LocalDateTime createDate;

}
