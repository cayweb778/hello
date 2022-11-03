package org.boozsoft.domain.entity.group;

import lombok.Data;
import java.io.Serializable;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModel;
import org.springframework.data.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-03-17 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "_app_group_task" )
@ApiModel(value="集团任务管理表",description="集团任务管理表")
public class GroupTask implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "操作员唯一编码")
	private String caozuoUnique;

	@ApiModelProperty(value = "时间")
	private String time;

	@ApiModelProperty(value = "功能模块")
	private String functionModule;

	@ApiModelProperty(value = "档案编码 改成 组织唯一码")
	private String recordNum;

	@ApiModelProperty(value = "状态 1，正常，0异常")
	private String state;

	@ApiModelProperty(value = "任务年度")
	private String iyear;

	@ApiModelProperty(value = "任务月份")
	private String imonth;

	@ApiModelProperty(value = "操作动作（增、删、改、停启用）")
	private String method;
}
