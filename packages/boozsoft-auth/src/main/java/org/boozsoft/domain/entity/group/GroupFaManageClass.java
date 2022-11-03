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
@Table("_app_group_fa_manage_class")
@ApiModel(value = "资产管理类别表", description = "资产管理类别表")
public class GroupFaManageClass {
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;

    @ApiModelProperty(value = "资产管理ID", position = 2)
    private String superiorId;

    @ApiModelProperty(value = "管理代码1~99", position = 1)
    private String manageCode;

    @ApiModelProperty(value = "管理名称", position = 5)
    private String manageName;

    @ApiModelProperty(value = "类别创建时间")
    private String createTime;
}
