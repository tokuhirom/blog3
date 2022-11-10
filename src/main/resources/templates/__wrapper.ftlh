<#-- @ftlvariable name="page" type="java.lang.Integer" -->
<#-- @ftlvariable name="entries" type="blog3.entity.Entry[]" -->
<#macro main subtitle="">
    <!doctype html>
    <html lang="ja">
    <head>
        <meta charset="utf-8">
        <#if subtitle != "">
            <title>${subtitle} - tokuhirom blog</title><!-- TODO configurable -->
        <#else>
            <title>tokuhirom blog</title><!-- TODO configurable -->
        </#if>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
            integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
            crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/prismjs@1/themes/prism-solarizedlight.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/prismjs@1/themes/prism.min.css">
        <link href="/css/main.css" rel="stylesheet">
        <!-- Global site tag (gtag.js) - Google Analytics -->
        <script async src="https://www.googletagmanager.com/gtag/js?id=G-N48P264GB5"></script>
        <script>
            window.dataLayer = window.dataLayer || [];
            function gtag(){dataLayer.push(arguments);}
            gtag('js', new Date());
            gtag('config', 'G-N48P264GB5');
        </script>
    </head>
    <body>
    <div id="container">
        <h1 class="title"><a href="/">Blog</a></h1>

        <form method="get" action="/search">
            <label>
                <input type="text" name="q">
            </label>
            <button type="submit" class="btn btn-primary">Search</button>
        </form>

        <#nested/>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/prismjs@1/prism.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/prismjs@1/plugins/autoloader/prism-autoloader.min.js"></script>
    </body>
    </html>
</#macro>
