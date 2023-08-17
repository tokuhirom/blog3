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


## Production environment

### (Retired) Environment v3

k8s based.

    blog.64p.org(name) -> ingress-nginx -> ktor-server(8180)
    blog-admin.64p.org(name) -> ingress-nginx(basic auth) -> kweb-server(8280)

### Environment v4(2023/08)

v3's cost is too high for me. it's fun, but too expensive. about $60/mo.

    blog.64p.org(Route53, CNAME) →blog3(DigitalOcean apps, dockerhub's tokuhirom/blog3's 8180 port)
    blog-admin.64p.org(Route53, CNAME) →blog3-admin(DigitalOcean apps, dockerhub's tokuhirom/blog3's 8280 port)


New estimated cost:

    - 1 x Managed database $15/mo
    - 2 x digital ocean apps 1GB RAM 1vCPU, $10.00/mo
    ----
    - Total: $40/mo

Note:

  * I need to set CNAME domain into DigitalOcean's `App settings`.
  * DigitalOceanApps automatically enable the TLS termination.

