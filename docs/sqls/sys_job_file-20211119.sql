-- auto-generated definition
create table sys_job_file
(
    id           varchar not null
        constraint sys_job_file_pk
        primary key,
    unique_code varchar,
    ec_code varchar,
    ec_name      varchar,
    parent_id         varchar,
    unique_code_user     varchar,
    create_date     varchar,
    flag     varchar,
    ec_level     varchar,
    bend    varchar,
    tenant_id varchar default CURRENT_USER
);

comment on table sys_job_file is '政府支出经济分类';
comment on column sys_job_file.id is '主键';
comment on column _app_group_sys_job_file.unique_code is '唯一标识';
comment on column sys_job_file.ec_code is '编码';
comment on column sys_job_file.ec_name is '名称';
comment on column sys_job_file.parent_id is '上级id';
comment on column sys_job_file.unique_code_user is '创建人';
comment on column sys_job_file.ec_level is '级次';
comment on column sys_job_file.bend is '是否末级1末级';
comment on column sys_job_file.create_date is '创建日期';
comment on column sys_job_file.flag is '是否停用(1.启用;0停用)';

alter table sys_job_file
    owner to postgres;

ALTER TABLE sys_job_file ENABLE ROW LEVEL SECURITY;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjjdkjyx-001-2021";

create policy "tenent-bjjdkjyx-001-2021-table-sys_job_file" on sys_job_file  for all to "bjjdkjyx-001-2021"  using (tenant_id  = 'bjjdkjyx-001-2021');

