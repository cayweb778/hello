package org.boozsoft.domain.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("ftp_file" )
@ApiModel(value="文件上传记录表",description="文件上传记录表")
public class FtpFile {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "新名称", position = 1)
	private String newName;

	@ApiModelProperty(value = "原名称", position = 2)
	private String oldName;

	@ApiModelProperty(value = "文件大小", position = 3)
	private String size;

	@ApiModelProperty(value = "文件格式", position = 4)
	private String type;

	@ApiModelProperty(value = "文件存储路径", position = 5)
	private String url;

	@ApiModelProperty(value = "添加时间", position = 6)
	private String time;

	@ApiModelProperty(value = "哈希值", position = 6)
	private String hash;

	private String beiyong1;

	private String beiyong2;

	private String beiyong3;

	private String beiyong4;

	private String beiyong5;


}
