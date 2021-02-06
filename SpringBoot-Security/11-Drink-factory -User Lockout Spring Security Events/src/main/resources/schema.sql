create table persistent_logins (username varchar(64) not null,
                                token varchar(64) not null,
                                series varchar(64) primary key,
                                last_used timestamp not null);

