-- auto-generated definition
create table _app_group_expenditure_class
(
    id           varchar not null
        constraint _app_group_expenditure_class_pk
        primary key,
    unique_code varchar,
    ec_code varchar,
    ec_name      varchar,
    parent_id         varchar,
    unique_code_user     varchar,
    create_date     varchar,
    flag     varchar,
    bend    varchar
);
budget_source
comment on table _app_group_expenditure_class is '支出功能分类';
comment on column _app_group_expenditure_class.id is '主键';
comment on column _app_group_expenditure_class.unique_code is '唯一标识';
comment on column _app_group_expenditure_class.ec_code is '编码';
comment on column _app_group_expenditure_class.ec_name is '名称';
comment on column _app_group_expenditure_class.parent_id is '上级id';
comment on column _app_group_expenditure_class.unique_code_user is '创建人';
comment on column _app_group_expenditure_class.bend is '是否末级1末级';
comment on column _app_group_expenditure_class.create_date is '创建日期';
comment on column _app_group_expenditure_class.flag is '是否停用(1.启用;0停用)';

alter table _app_group_expenditure_class
    owner to postgres;