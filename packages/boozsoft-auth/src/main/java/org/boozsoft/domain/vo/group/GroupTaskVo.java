package org.boozsoft.domain.vo.group;

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
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

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
public class GroupTaskVo implements Serializable {


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

	private String username;
}
