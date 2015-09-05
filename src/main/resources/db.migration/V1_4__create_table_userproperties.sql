create table if NOT EXISTS app.user_properties (
  user_id int,
  name varchar(100),
  value varchar(100),
  primary key (user_id, name)
);