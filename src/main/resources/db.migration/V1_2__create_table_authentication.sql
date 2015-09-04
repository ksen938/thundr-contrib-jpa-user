create table IF NOT EXISTS glory.authentication (
  authentication_id int auto_increment,
  username varchar(100),
  password varchar(100),
  salt VARBINARY(100),
  iterations int,
  digest varchar(100),
  PRIMARY KEY (authentication_id),
    UNIQUE KEY (username)
);