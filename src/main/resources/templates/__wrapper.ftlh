<#-- @ftlvariable name="page" type="java.lang.Integer" -->
<#-- @ftlvariable name="entries" type="me.geso.blog3.entity.Entry[]" -->
<#macro main>
    <!doctype html>
    <html lang="ja">
    <head>
        <meta charset="utf-8">
        <title>tokuhirom blog</title><!-- TODO configurable -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-F3w7mX95PdgyTmZZMECAngseQB83DfGTowi0iMjiWaeVhAn4FJkqJByhZMI3AhiU"
              crossorigin="anonymous">
        <link href="/css/main.css" rel="stylesheet">
    </head>
    <body>
    <div id="container">
        <h1 class="title"><a href="/">Blog</a></h1>
        <#nested/>
    </div>
    </body>
    </html>
</#macro>
