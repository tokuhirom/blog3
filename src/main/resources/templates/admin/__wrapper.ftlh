<#-- @ftlvariable name="page" type="java.lang.Integer" -->
<#-- @ftlvariable name="entries" type="blog3.entity.Entry[]" -->
<#macro main subtitle="">
    <!doctype html>
    <html lang="ja">
    <head>
        <meta charset="utf-8">
        <#if subtitle != "">
            <title>${subtitle} - tokuhirom blog ADMIN</title><!-- TODO configurable -->
        <#else>
            <title>tokuhirom blog ADMIN</title><!-- TODO configurable -->
        </#if>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
            integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
            crossorigin="anonymous">
        <link href="/css/main.css" rel="stylesheet">
    </head>
    <body>
    <div id="container">
        <h1 class="title"><a href="/admin/">Blog Admin</a></h1>

        <#nested/>
    </div>
    </body>
    </html>
</#macro>
