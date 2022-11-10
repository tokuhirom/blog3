<#-- @ftlvariable name="page" type="java.lang.Integer" -->
<#-- @ftlvariable name="entries" type="blog3.entity.Entry[]" -->
<#import "/admin/__wrapper.ftlh" as wrapper>
<@wrapper.main>
    <div>
        <a href="/admin/create" class="btn btn-primary">Create</a>
    </div>

    <table class="table table-bordered">
        <tr>
            <th>Title</th>
            <th>Status</th>
            <th>Created</th>
        </tr>
        <#list entries as entry>
            <tr>
                <td><a href="/admin/update?path=${entry.path?url}">${entry.title}</a></td>
                <td>${entry.status}</td>
                <td>${entry.createdAt}</td>
            </tr>
        </#list>
    </table>
    <nav aria-label="Page navigation example">
        <ul class="pagination">
            <#if page != 1>
                <li class="page-item"><a class="page-link" href="/?page=${page-1}">Previous</a></li>
            </#if>
            <li class="page-item"><a class="page-link" href="/?page=${page+1}">Next</a></li>
        </ul>
    </nav>
</@wrapper.main>
