<#-- @ftlvariable name="page" type="java.lang.Integer" -->
<#-- @ftlvariable name="entries" type="me.geso.blog3.entity.Entry[]" -->
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
        <link href="/css/main.css" rel="stylesheet">
        <!-- Google Tag Manager -->
        <script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
        new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
        j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
        'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
        })(window,document,'script','dataLayer','GTM-PZTXDRW');</script>
        <!-- End Google Tag Manager -->
    </head>
    <body>
    <!-- Google Tag Manager (noscript) -->
        <noscript><iframe src="https://www.googletagmanager.com/ns.html?id=GTM-PZTXDRW"
        height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
    <!-- End Google Tag Manager (noscript) -->
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
    </body>
    </html>
</#macro>
