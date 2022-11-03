package org.boozsoft.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_sys_notice" )
@ApiModel(value="系统通知信息表",description="系统通知信息表")
public class SysNotice {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	private String id;

	@ApiModelProperty(value = "账套唯一编码", position = 1)
	private String corpUniqueCode;

	@ApiModelProperty(value = "操作员唯一编码", position = 2)
	private String userUniqueCode;

	@ApiModelProperty(value = "通知类型名称（协作、审核和告知）", position = 3)
	private String noticeType;

	@ApiModelProperty(value = "发起时间", position = 4)
	private String noticeTime;

	@ApiModelProperty(value = "通知内容", position = 5)
	private String noticeContent;

	@ApiModelProperty(value = "单据名称", position = 6)
	private String noticeName;

	@ApiModelProperty(value = "单据（或档案）编码", position = 7)
	private String noticeCode;

	@ApiModelProperty(value = "接收角色唯一码", position = 8)
	private String receiveRole;

	@ApiModelProperty(value = "接收部门唯一码（账套唯一编码相关联，不可为空）", position = 9)
	private String receiveDept;

	@ApiModelProperty(value = "接收人员唯一码", position = 10)
	private String receiveUser;

	@ApiModelProperty(value = "是否处理，0未处理，1已处理", position = 11)
	private String noticeState;


}
