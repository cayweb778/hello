package org.boozsoft.domain.entity;


import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("data_log" )
@ApiModel(value="data_log: table",description="data_log: table")
public class DataLog {

	@Id
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

	@ApiModelProperty(value = "1、修改2、删除3、停用、4查看、5导入、6导出、7打印", position = 9)
	private String logMethod;


}
