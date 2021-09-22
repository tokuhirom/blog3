<#-- @ftlvariable name="entries" type="me.geso.blog3.entity.Entry[]" -->
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>tokuhirom blog</title><!-- TODO configurable -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-F3w7mX95PdgyTmZZMECAngseQB83DfGTowi0iMjiWaeVhAn4FJkqJByhZMI3AhiU"
          crossorigin="anonymous">
</head>
<body>
<h1>Blog</h1>
<#list entries as entry>
    <div>
        <h2>${entry.title}</h2>
        <p>
            ${entry.body?no_esc}
        </p>
    </div>
</#list>
</body>
</html>