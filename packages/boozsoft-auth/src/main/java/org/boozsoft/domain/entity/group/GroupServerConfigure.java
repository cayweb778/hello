package org.boozsoft.domain.entity.group;

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

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "_app_group_server_configure" )
@ApiModel(value="NC应用服务器配置",description="NC应用服务器配置")
public class GroupServerConfigure implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@ApiModelProperty(value = "操作系统")
	private String osName;
	@ApiModelProperty(value = "数据库")
	private String databaseName;
	@ApiModelProperty(value = "管理系统")
	private String managementSystem;
	@ApiModelProperty(value = "数据备份")
	private String dataBackup;
	@ApiModelProperty(value = "安全防护")
	private String security;
	@ApiModelProperty(value = "外网IP地址")
	private String extranetIp;
	@ApiModelProperty(value = "内网IP地址")
	private String intranetIp;

	@ApiModelProperty(value = "服务器结构")
	private String serverStructure;
	@ApiModelProperty(value = "品牌型号")
	private String brandInfo;
	@ApiModelProperty(value = "处理器")
	private String processor;
	@ApiModelProperty(value = "内存")
	private String memoryCard;
	@ApiModelProperty(value = "存储")
	private String memoryStorage;
	@ApiModelProperty(value = "网卡")
	private String networkCard;
	@ApiModelProperty(value = "电源")
	private String powerSupply;
}
