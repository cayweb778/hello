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
@Table ("_app_group_sys_ocr_api" )
@ApiModel(value="OCR图像识别接口",description="OCR图像识别接口")
public class SysOcrApi {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	private String id;

	@ApiModelProperty(value = "api-id", position = 1)
	private String secretId;

	@ApiModelProperty(value = "api-key", position = 2)
	private String secretKey;

	@ApiModelProperty(value = "服务商名称", position = 3)
	private String groupName;


}
