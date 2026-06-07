create table files (
    id serial not null primary key,
    object_id varchar(100) not null unique
);