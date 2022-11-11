# blog3

## Development
 
Important configuration properties

 - spring.datasource.url
 - spring.security.user.name
 - spring.security.user.password
 - spring.security.user.roles=ADMIN

## admin site

    blog.64p.org(name) -> ingress-nginx -> ktor-server(8180)
    blog-admin.64p.org(name) -> ingress-nginx(basic auth) -> kweb-server(8280)
