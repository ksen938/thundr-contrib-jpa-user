
create table IF NOT EXISTS app.user (
    user_id int AUTO_INCREMENT,
    username varchar(100),
    email VARCHAR(100),
    created DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME,
    PRIMARY KEY (user_id),
    UNIQUE INDEX (username)
);