# blog3

## Development
 
Important configuration properties

 - spring.datasource.url
 - spring.security.user.name
 - spring.security.user.password
 - spring.security.user.roles=ADMIN

Environment vars:

 - `AWS_ACCESS_KEY_ID`
 - `AWS_SECRET_KEY`

## Run locally

    docker run --name mysql-server -e MYSQL_ROOT_PASSWORD=yourpassword -p 3306:3306 -d mysql:8

create following table in the mysql server:

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

    SPRING_SECURITY_USER_NAME=admin SPRING_SECURITY_USER_PASSWORD=admin SPRING_DATASOURCE_URL=jdbc:mysql://root:yourpassword@127.0.0.1:3306/blog ./gradlew bootRun



