package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "档案名：常用凭证分录", description = "常用凭证分录")
@Table("accvoucher_setting_entry")
@Data
public class AccvoucherSettingEntry {

    @Id
    @ApiModelProperty(value = "主键", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "常用凭证id", hidden = true)
    private String parentId;
    @ApiModelProperty(value = "序号", hidden = true)
    private Integer num;
    @ApiModelProperty(value = "摘要", hidden = true)
    private String cdigest;
    @ApiModelProperty(value = "科目", hidden = true)
    private String ccode;
    @ApiModelProperty(value = "借方", hidden = true)
    private String md;
    @ApiModelProperty(value = "贷方", hidden = true)
    private String mc;

}
