package org.boozsoft.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.util.*;
/**
 * @Author Jesse
 * @Description TODO
 * @Date 2021/1/29 上午11:30
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class VoucherBusCheckVo{
    private Integer selectNumber;
    private Integer passNumber;
    private Integer successNumber;
    private Integer errorNumber;
    private  List<Map<String,String>> errorList = new ArrayList<>();
}