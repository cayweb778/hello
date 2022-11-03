package org.boozsoft.domain.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_sys_log" )
@ApiModel(value="系统日志表",description="系统日志表")
public class SysLog {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "操作时间", position = 1)
	private String operationDate;

	@ApiModelProperty(value = "账户唯一编码", position = 2)
	private String uniqueCode;

	@ApiModelProperty(value = "账户姓名", position = 3)
	private String userName;

	@ApiModelProperty(value = "账套号", position = 4)
	private String accId;

	@ApiModelProperty(value = "年度", position = 5)
	private String iyear;

	@ApiModelProperty(value = "执行功能（菜单代码）", position = 6)
	private String functionCode;

	@ApiModelProperty(value = "操作内容（具体到档案编码或凭证年月日和号码）", position = 7)
	private String operationCont;

	@ApiModelProperty(value = "客户端IP或主机名", position = 8)
	private String client;

	@ApiModelProperty(value = "退出时间", position = 9)
	private String signoutDate;
	@ApiModelProperty(value = "1、修改2、删除3、停用、4查看、5导入、6导出、7打印", position = 9)
	private String logMethod;

}
