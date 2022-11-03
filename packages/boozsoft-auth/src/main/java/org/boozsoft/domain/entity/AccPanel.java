package org.boozsoft.domain.entity;


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

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("acc_panel" )
@ApiModel(value="总账面板设置",description="总账面板设置")
public class AccPanel {

	@ApiModelProperty(value = "id", position = 0)
	@Id	@CreatedBy	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "操作员唯一编码（系统方案不需要）", position = 1)
	private String caozuoUnique;

	@ApiModelProperty(value = "年度", position = 2)
	private String panelIyear;

	@ApiModelProperty(value = "显示名称", position = 3)
	private String panelName;

	@ApiModelProperty(value = "科目公式（加减）", position = 4)
	private String panelFormula;
	@ApiModelProperty(value = "分离后的科目", position = 4)
	private String splitCode;
	@ApiModelProperty(value = "分离后的符号", position = 4)
	private String splitFuhao;
	@ApiModelProperty(value = "取值方式：余额、借方余额、贷方余额、借方累计发生、贷方累计发生", position = 5)
	private String panelDataRange;

	@ApiModelProperty(value = "币种", position = 6)
	private String panelBz;

	@ApiModelProperty(value = "显示币种单位", position = 7)
	private String panelBzUnit;

	@ApiModelProperty(value = "1系统方案、2个人方案", position = 8)
	private String panelFlag;

	@ApiModelProperty(value = "排序号", position = 9)
	private Integer panelOrder;

	@ApiModelProperty(value = "模块简称", position = 8)
	private String panelType;

	@ApiModelProperty(value = "租户ID", position = 10)
	@Transient
	private String tenantId;


}
