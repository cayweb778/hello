-- auto-generated definition
create table _app_origin_defined_file
(
    id           varchar not null
        constraint _app_origin_defined_file_pk
        primary key,
    defined_code varchar,
    defined_name varchar,
    remarks      varchar,
    flag         varchar,
    beiyong1     varchar,
    beiyong2     varchar,
    beiyong3     varchar,
    beiyong4     varchar,
    beiyong5     varchar,
    origin_id    varchar
);

comment on table _app_origin_defined_file is '自定义项设置';

comment on column _app_origin_defined_file.id is '主键';

comment on column _app_origin_defined_file.defined_code is '自定义项设置编码';

comment on column _app_origin_defined_file.defined_name is '自定义项设置名称';

comment on column _app_origin_defined_file.remarks is '备注';

comment on column _app_origin_defined_file.flag is '是否停用(1.启用;0停用)';

alter table _app_origin_defined_file
    owner to postgres;