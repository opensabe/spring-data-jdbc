create table sys.t_activity (
    id varchar(64),
    config json,
    platforms json,
    times json,
    online tinyint,
    primary key (id)
);
create table sys.t_user (
    id varchar(64),
    `name` varchar(32),
    age int,
    email varchar(32),
    create_time datetime(3) default current_timestamp(3),
    update_time datetime(3) default current_timestamp(3) on update current_timestamp(3),
    primary key (id)
);

create table sys.t_user_his (
    id varchar(64),
    `name` varchar(32),
    age int,
    email varchar(32),
    create_time datetime(3) default current_timestamp(3),
    update_time datetime(3) default current_timestamp(3) on update current_timestamp(3),
    primary key (id)
);

create table sys.t_user_role (
    user_id varchar(64),
    role_id varchar(64),
    role_name varchar(12),
    user_name varchar(12),
    primary key (user_id, role_id)
);

create table sys.t_role (
    id int auto_increment,
    `name` varchar(12),
    primary key(id)
);