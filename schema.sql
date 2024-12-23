create table entry (
                       title varchar(255),
                       path varchar(255),
                       body longtext,
                       status varchar(255),
                       format varchar(255) default 'mkdn',
                       createdAt datetime DEFAULT CURRENT_TIMESTAMP,
                       updatedAt datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       primary key (path)
) engine=innodb default charset=utf8mb4;
