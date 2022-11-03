package org.springbooz.datasource.r2dbc.annotation;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2021/3/25 1:43 下午
 */
@AllArgsConstructor
@Data
public class R2dbcRouterDatasourceModel {
    String databaseName;
    String schemaName;
    String user;
}
