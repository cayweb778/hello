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

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("_app_group_file_encoding_rules_report" )
@ApiModel(value="单据档案编码规则",description="单据档案编码规则")
public class GroupReportEncodingRules {
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;

    @ApiModelProperty(value = "类型", position = 1)
    private String fileType;

    @ApiModelProperty(value = "编码方式", position = 2)
    private String codeWay;

    @ApiModelProperty(value = "编号允许手动修改", position = 3)
    private String isManual;

    @ApiModelProperty(value = "自动补空号", position = 4)
    private String autoNum;

    @ApiModelProperty(value = "流水号长度", position = 5)
    private String serialNumLength;

    @ApiModelProperty(value = "起始值", position = 6)
    private String serialNumStr;

    @ApiModelProperty(value = "分隔符", position = 7)
    private String delimiter;

    @ApiModelProperty(value = "前缀一", position = 8)
    private String prefixOne;

    @ApiModelProperty(value = "占位长度", position = 9)
    private String prefixOneLength;

    @ApiModelProperty(value = "自定义", position = 10)
    private String prefixOneCustomize;

    @ApiModelProperty(value = "流水号参照", position = 11)
    private String prefixOneIs;

    @ApiModelProperty(value = "前缀二", position = 12)
    private String prefixTwo;

    @ApiModelProperty(value = "占位长度", position = 13)
    private String prefixTwoLength;

    @ApiModelProperty(value = "自定义", position = 14)
    private String prefixTwoCustomize;

    @ApiModelProperty(value = "流水号参照", position = 15)
    private String prefixTwoIs;

    @ApiModelProperty(value = "前缀三", position = 16)
    private String prefixThree;

    @ApiModelProperty(value = "占位长度", position = 17)
    private String prefixThreeLength;

    @ApiModelProperty(value = "自定义", position = 18)
    private String prefixThreeCustomize;

    @ApiModelProperty(value = "流水号参照", position = 19)
    private String prefixThreeIs;

    @ApiModelProperty(value = "表名", position = 1)
    private String tableName;

    @ApiModelProperty(value = "显示效果", position = 1)
    private String showRules;

}
