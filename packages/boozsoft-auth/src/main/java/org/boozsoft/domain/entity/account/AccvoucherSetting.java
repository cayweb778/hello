package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@ApiModel(value = "档案名：常用凭证", description = "常用凭证")
@Table("accvoucher_setting")
@Data
public class AccvoucherSetting {

    @Id
    @ApiModelProperty(value = "主键", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "编码", hidden = true)
    private String ccode;
    @ApiModelProperty(value = "凭证说明", hidden = true)
    private String remarks;
    @ApiModelProperty(value = "凭证类别", hidden = true)
    private String ctype;
    @ApiModelProperty(value = "附单据数", hidden = true)
    private String cnum;
    @ApiModelProperty(value = "分类编码", hidden = true)
    private String classCode;

    @Transient
    @ApiModelProperty(value = "分录", hidden = true)
    private List<AccvoucherSettingEntry> table;

}
