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
@Table ("task" )
@ApiModel(value="task: table",description="task: table")
public class Task {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "操作员唯一编码", position = 1)
	private String caozuoUnique;

	@ApiModelProperty(value = "时间", position = 2)
	private String time;

	@ApiModelProperty(value = "功能模块【会计科目,期初余额,凭证记账,月末结账,科目编辑,转账生成,新增凭证,凭证修改,凭证导入,客户信息】", position = 3)
	private String functionModule;
	@ApiModelProperty(value = "操作模块：存货管理stock", position = 8)
	private String caozuoModule;

	@ApiModelProperty(value = "档案编码", position = 4)
	private String recordNum;

	@ApiModelProperty(value = "状态 1，正常，0异常", position = 5)
	private String state;

	@ApiModelProperty(value = "任务年度", position = 6)
	private String iyear;

	@ApiModelProperty(value = "任务月份", position = 7)
	private String imonth;

	@ApiModelProperty(value = "操作动作（增、删、改、停启用）", position = 8)
	private String method;

	@ApiModelProperty(value = "租户ID", position = 8)
	@Transient
	private String tenantId;

	@ApiModelProperty(value = "操作员名称", position = 1)
	@Transient
	private String username;
}
