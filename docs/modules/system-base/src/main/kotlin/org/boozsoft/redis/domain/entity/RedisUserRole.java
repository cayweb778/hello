package org.boozsoft.redis.domain.entity;//package org.boozsoft.redis.domain.entity;
//
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.redis.core.RedisHash;
//@Data
//@RedisHash("user_role")
//public class RedisUserRole {
//
//    private static final long serialVersionUID = 1L;
//
//    /**
//     * 主键
//     */
//
//    private String id;
//    private Long targetId;
//
//    /**
//     * 租户ID
//     */
//    private String tenantId;
//
//    /**
//     * 父主键
//     */
//    @JsonSerialize(using = ToStringSerializer.class)
//    private Long parentId;
//
//    /**
//     * 角色名
//     */
//    private String roleName;
//
//    /**
//     * 排序
//     */
//    private Integer sort;
//
//    /**
//     * 角色别名
//     */
//    private String roleAlias;
//
//    /**
//     * 是否已删除
//     */
//    private Integer isDeleted;
//}
