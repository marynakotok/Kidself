DROP TABLE request;
DROP TABLE task;
DROP TABLE geolocation;
DROP TABLE zone;
DROP TABLE family;
DROP TABLE parent;
DROP TABLE child;
DROP TABLE chat_rooms;
DROP TABLE messages;
DROP TABLE all_users;


CREATE OR REPLACE TABLE all_users (
  code_user varchar(32),
  gcm_registration_id text NOT NULL DEFAULT '',
  CONSTRAINT PK_all_users PRIMARY KEY (code_user)
);

CREATE OR REPLACE TABLE messages (
  message_id int(11) AUTO_INCREMENT,
  code_family varchar(32) NOT NULL,
  code_user varchar(32) NOT NULL,
  created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  message text NOT NULL,
  CONSTRAINT PK_message_id PRIMARY KEY (message_id),
  CONSTRAINT FK_user_id FOREIGN KEY (code_user) REFERENCES all_users (code_user) MATCH SIMPLE ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_family_id FOREIGN KEY (code_family) REFERENCES family (parent_1) MATCH SIMPLE ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE OR REPLACE TABLE child (
  code_child varchar(32),
  name varchar(15) NOT NULL,
  sex varchar(1) NOT NULL,
  bd date NOT NULL,
  created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_login timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT PK_child PRIMARY KEY (code_child),
  CONSTRAINT child_sex CHECK (sex IN ('M', 'F')),
  CONSTRAINT FK_child FOREIGN KEY (code_child) REFERENCES all_users (code_user) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE OR REPLACE TABLE parent (
  code_parent varchar(32),
  name varchar(15) NOT NULL,
  sex varchar(1),
  bd date NOT NULL,
  email varchar(30),
  password_parent varchar(32) NOT NULL,
  created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_login timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT parent_sex CHECK (sex IN ('M', 'F')),
  CONSTRAINT PK_parent PRIMARY KEY (code_parent),
  CONSTRAINT UQ_parent UNIQUE (email),
  CONSTRAINT FK_parent FOREIGN KEY (code_parent) REFERENCES all_users (code_user) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE OR REPLACE TABLE family (
  parent_1 varchar(32) NOT NULL,
  parent_2 varchar(32),
  child_1 varchar(32),
  child_2 varchar(32),
  child_3 varchar(32),
  child_4 varchar(32),
  child_5 varchar(32),
  child_6 varchar(32),
  CONSTRAINT PK_child PRIMARY KEY (parent_1),
  CONSTRAINT FK_family_parent_1 FOREIGN KEY (parent_1) REFERENCES parent (code_parent) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_family_parent_2 FOREIGN KEY (parent_2) REFERENCES parent (code_parent) MATCH SIMPLE
ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT FK_family_child_1 FOREIGN KEY (child_1) REFERENCES child (code_child) MATCH SIMPLE
ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT FK_family_child_2 FOREIGN KEY (child_2) REFERENCES child (code_child) MATCH SIMPLE
ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT FK_family_child_3 FOREIGN KEY (child_3) REFERENCES child (code_child) MATCH SIMPLE
ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT FK_family_child_4 FOREIGN KEY (child_4) REFERENCES child (code_child) MATCH SIMPLE
ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT FK_family_child_5 FOREIGN KEY (child_5) REFERENCES child (code_child) MATCH SIMPLE
ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT FK_family_child_6 FOREIGN KEY (child_6) REFERENCES child (code_child) MATCH SIMPLE
ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE password_reset_request (
sno int(11) NOT NULL AUTO_INCREMENT,
email varchar(30) NOT NULL,
encrypted_temp_password varchar(256) NOT NULL,
salt varchar(10) NOT NULL,
created_at datetime DEFAULT NULL,
PRIMARY KEY (sno)
);

CREATE OR REPLACE TABLE zone (
  id varchar(6),
  name varchar(15) NOT NULL,
  longitude decimal (10,3) NOT NULL,
  latitude decimal (10,3) NOT NULL,
  radius decimal(10,3) NOT NULL,
  CONSTRAINT PK_zone PRIMARY KEY (id)
);

CREATE OR REPLACE TABLE geolocation (
  id varchar(6),
  address varchar(30), 
  longitude decimal (10,3) NOT NULL,
  latitude decimal (10,3) NOT NULL,
  child varchar(5) NOT NULL,
  CONSTRAINT PK_geolocation PRIMARY KEY (id)
);


CREATE OR REPLACE TABLE task (
  id varchar(6),
  title varchar(20) NOT NULL, 
  description varchar(100) NOT NULL,
  parent varchar(32) NOT NULL,
  child varchar(32) NOT NULL,
  status_task varchar(1) NOT NULL DEFAULT 'N',
  start_time time NOT NULL,
  end_time time NOT NULL,
  created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT PK_task PRIMARY KEY (id),
  CONSTRAINT task_status CHECK (status IN ('N', 'Y')),
  CONSTRAINT FK_task_child FOREIGN KEY (child) REFERENCES child (code_child) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_task_parent FOREIGN KEY (parent) REFERENCES parent (code_parent) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE CASCADE
);


CREATE OR REPLACE TABLE request (
  id varchar(6),
  title varchar(20) NOT NULL,
  reason varchar(100) NOT NULL,
  status_request varchar(1) NOT NULL DEFAULT 'P',
  parent varchar(32) NOT NULL,
  child varchar(32) NOT NULL,
  created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT request_status CHECK (status_request IN ('A', 'D', 'P')),
  CONSTRAINT PK_request PRIMARY KEY (id),
  CONSTRAINT FK_request_parent FOREIGN KEY (parent) REFERENCES parent (code_parent) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT FK_request_child FOREIGN KEY (child) REFERENCES child (code_child) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE CASCADE
);
