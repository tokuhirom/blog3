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


