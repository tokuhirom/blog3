<#-- @ftlvariable name="page" type="java.lang.Integer" -->
<#-- @ftlvariable name="entries" type="me.geso.blog3.entity.Entry[]" -->
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>tokuhirom blog</title><!-- TODO configurable -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-F3w7mX95PdgyTmZZMECAngseQB83DfGTowi0iMjiWaeVhAn4FJkqJByhZMI3AhiU"
          crossorigin="anonymous">
    <style>
        #container {
            padding-left: 100px;
            padding-right: 100px;
        }

        .entry {
            margin-bottom: 30px;
        }
    </style>
</head>
<body>
<div id="container">
    <h1>Blog</h1>
    <#list entries as entry>
        <div class="entry">
            <h2>${entry.title}</h2>
            <p>
                ${entry.html()?no_esc}
            </p>
        </div>
    </#list>
    <nav aria-label="Page navigation example">
        <ul class="pagination">
            <#if page != 1>
                <li class="page-item"><a class="page-link" href="/?page=${page-1}">Previous</a></li>
            </#if>
            <li class="page-item"><a class="page-link" href="/?page=${page+1}">Next</a></li>
        </ul>
    </nav>
</div>
</body>
</html>