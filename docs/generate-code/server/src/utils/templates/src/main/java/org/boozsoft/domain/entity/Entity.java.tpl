package org.boozsoft.domain.entity.$$platformName$$;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "档案名：$$档案名$$", description = "$$描述信息$$")
@Table("$$表名$$")
@Data
public class $$RecordName$$ {
$$内容$$
}
