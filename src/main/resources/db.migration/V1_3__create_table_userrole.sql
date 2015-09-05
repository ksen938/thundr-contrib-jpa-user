create table if NOT EXISTS app.user_role (
    user_id INT,
    role_name VARCHAR(100),
    CONSTRAINT FK_user FOREIGN KEY (user_id) REFERENCES user (user_id)
);