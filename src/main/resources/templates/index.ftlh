<#-- @ftlvariable name="page" type="java.lang.Integer" -->
<#-- @ftlvariable name="entries" type="me.geso.blog3.entity.Entry[]" -->
<#import "/__wrapper.ftlh" as wrapper>
<@wrapper.main>
    <#list entries as entry>
        <#include "__entry.ftlh">
        <#include "__ads.ftlh">
    </#list>
    <nav aria-label="Page navigation example">
        <ul class="pagination">
            <#if page != 1>
                <li class="page-item"><a class="page-link" href="/?page=${page-1}">Previous</a></li>
            </#if>
            <li class="page-item"><a class="page-link" href="/?page=${page+1}">Next</a></li>
        </ul>
    </nav>

    <p>
        <a href="https://github.com/tokuhirom/blog3/commit/${gitShortCommitId}">${gitShortCommitId}
            @${gitBranch} ${gitCommitTime}</a>
    </p>

</@wrapper.main>
