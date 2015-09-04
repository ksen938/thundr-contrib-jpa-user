create table if NOT EXISTS glory.user_token (
user_token_id int AUTO_INCREMENT,
  user_id INT,
  token VARCHAR(100),
  PRIMARY KEY (user_token_id),
  CONSTRAINT FK_user_token FOREIGN KEY (user_id) REFERENCES user (user_id)
);