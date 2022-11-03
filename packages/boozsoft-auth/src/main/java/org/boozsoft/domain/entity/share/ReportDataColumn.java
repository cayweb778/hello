package org.boozsoft.domain.entity.share;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：财务报表数据栏目", description = "财务报表数据栏目")
@Table("report_data_column")
@Data
public class ReportDataColumn {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "数据主表", hidden = true)
    private String dataId;
    @ApiModelProperty(value = "模板id", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String templateId;
    @ApiModelProperty(value = "序号", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long xuhao;
    @ApiModelProperty(value = "栏目显示名称", hidden = true)
    private String columnShowName;
    @ApiModelProperty(value = "显示级次(1~4)", hidden = true)
    private String jici;
    @ApiModelProperty(value = "行次（一个模板内不允许重复）", hidden = true)
    private String hangci;
    @ApiModelProperty(value = "所属类型(仅资产负债表使用；资产、负债、权益)", hidden = true)
    private String columnType;
    @ApiModelProperty(value = "期末余额", hidden = true)
    private String qimoMoney;
    @ApiModelProperty(value = "年初余额", hidden = true)
    private String nianchuMoney;
    @ApiModelProperty(value = "本月金额", hidden = true)
    private String benyueMoney;
    @ApiModelProperty(value = "本年累计金额", hidden = true)
    private String bennianLeijiMoney;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong4;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong5;

}
