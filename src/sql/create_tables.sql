CREATE DATABASE assessment ENCODING 'UTF8';
\c assessment;
set timezone = 'Asia/Taipei';
show timezone;
create table users(
    user_id serial not null primary key ,
    nickname varchar(20) not null unique,
    phone varchar(10) not null unique,
    passwords varchar(64) not null
);
create table articles(
    article_id serial not null primary key ,
    user_id int not null,
    article_title varchar(50) not null unique,
    article_content varchar(4000),
    status varchar(1) default '1',
    create_time timestamp default current_timestamp,
    update_time timestamp
)