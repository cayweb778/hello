package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ClassName : pzbzVo
 * @Description : 去重后的凭证币种
 * @Author : miao
 * @Date: 2021-07-08 09:05
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="去重后的凭证币种",description="去重后的凭证币种")
public class pzbzVo {
    private String ccode;
    private String bz;
    private Integer order;
}
