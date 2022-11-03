package org.boozsoft.domain.entity.account;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

/**
 * @author : http://www.chiner.pro
 * @date : 2022-4-16
 * @desc : 打印模版设置
 */

@ApiModel(value = "数据表：打印模版设置", description = "打印模版设置")
@Table("acc_aging_range")
@Data
public class StockPrintSetting implements Serializable,Cloneable{
    /** id */
    private String id ;
    /** 来源 */
    private String fromId ;
    /** 租户名 */
    private String tenantId ;
    /** 档案名 */
    private String recrodName ;
    /** 模版ID */
    private Integer templateId ;
    /** 模版名 */
    private String templateName ;
    /** 0 系统模版 */
    private String templateType ;
    /** 值类型 (1,页眉 2,页尾 3.表) */
    private Integer fieldType ;
    /** 值名称 */
    private String fieldName ;
    /** 排序 (100,200,300) */
    private String sortNo ;
    /** 值宽度 */
    private String width ;

}