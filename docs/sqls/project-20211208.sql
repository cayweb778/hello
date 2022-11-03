alter table project
    add item_code varchar;

comment on column project.item_code is '项目大类';