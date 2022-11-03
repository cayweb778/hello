-- auto-generated definition
create table _app_origin_defined_record
(
    id           varchar not null
        constraint _app_origin_defined_record_pk
        primary key,
    defined_code varchar,
    record_code  varchar,
    record_name  varchar,
    flag         varchar,
    remarks      varchar,
    beiyong1     varchar,
    beiyong2     varchar,
    beiyong3     varchar,
    beiyong4     varchar,
    beiyong5     varchar,
    origin_id    varchar
);

comment on table _app_origin_defined_record is '自定义项档案';

comment on column _app_origin_defined_record.id is '主键';

comment on column _app_origin_defined_record.defined_code is '自定义项设置编码';

comment on column _app_origin_defined_record.record_code is '档案编码';

comment on column _app_origin_defined_record.record_name is '档案名称';

comment on column _app_origin_defined_record.flag is '是否停用(1.启用;0停用)';

comment on column _app_origin_defined_record.remarks is '说明';

alter table _app_origin_defined_record
    owner to postgres;
