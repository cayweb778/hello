package org.boozsoft.domain.entity.invoice;


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
@Table ("invoice_ocr_record" )
@ApiModel(value="发票ocr识别记录表",description="发票ocr识别记录表")
public class InvoiceOcrRecord {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "图片id", position = 1)
	private String fileId;

	@ApiModelProperty(value = "录入时间", position = 2)
	private String createTime;

	@ApiModelProperty(value = "识别人唯一码", position = 3)
	private String userSingleNum;

	@ApiModelProperty(value = "识别单据类型", position = 4)
	private String type;

	@ApiModelProperty(value = "发票json", position = 5)
	private String invoiceJson;


}
