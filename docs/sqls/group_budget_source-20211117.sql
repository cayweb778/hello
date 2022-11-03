-- auto-generated definition
create table _app_group_budget_source
(
    id           varchar not null
        constraint _app_group_budget_source_pk
        primary key,
    bs_code varchar,
    bs_name      varchar,
    create_date     varchar,
    create_code     varchar,
    flag     varchar,
    stop_date   varchar
);

comment on table _app_group_budget_source is '支出功能分类';
comment on column _app_group_budget_source.id is '主键';
comment on column _app_group_budget_source.bs_code is '编码';
comment on column _app_group_budget_source.bs_name is '名称';
comment on column _app_group_budget_source.create_date is '创建日期';
comment on column _app_group_budget_source.create_code is '创建人';
comment on column _app_group_budget_source.stop_date is '停用日期';
comment on column _app_group_budget_source.flag is '是否停用(1.启用;0停用)';

alter table _app_group_budget_source
    owner to postgres;