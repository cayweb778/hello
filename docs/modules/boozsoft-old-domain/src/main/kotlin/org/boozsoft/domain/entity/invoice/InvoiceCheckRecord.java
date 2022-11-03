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
@Table ("invoice_check_record" )
@ApiModel(value="发票验真记录",description="发票验真记录")
public class InvoiceCheckRecord {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "发票代码", position = 1)
	private String invoiceCode;

	@ApiModelProperty(value = "发票号码", position = 2)
	private String invoiceNum;

	@ApiModelProperty(value = "操作人唯一码", position = 3)
	private String userSingleNum;

	@ApiModelProperty(value = "操作时间", position = 4)
	private String time;

	@ApiModelProperty(value = "开票日期", position = 5)
	private String invoiceTime;

	@ApiModelProperty(value = "开票金额", position = 6)
	private String invoiceMoney;

	@ApiModelProperty(value = "校验码", position = 7)
	private String invoiceCheckCode;

	@ApiModelProperty(value = "返回验真结果", position = 7)
	private String flag;
}
