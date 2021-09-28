<#-- @ftlvariable name="_csrf" type="org.springframework.security.web.csrf.CsrfToken" -->
<#-- @ftlvariable name="entry" type="me.geso.blog3.entity.Entry" -->
<#import "/admin/__wrapper.ftlh" as wrapper>
<@wrapper.main>
    <h1>${entry.path}</h1>
    <form method="post" action="/admin/do_update">
        <input type="hidden" name="path" value="${entry.path}">
        <div>
            <label for="entry-title">Title</label>
            <input id="entry-title" type="text" name="title" value="${entry.title}" class="form-control"
                   required>
        </div>
        <div>
            <label for="entry-body">Body</label>
            <textarea id="entry-body" name="body" cols="80" rows="20"
                      class="form-control">${entry.body}</textarea>
        </div>
        <div>
            <label>
                <select name="status" class="form-select form-select-lg">
                    <option value="draft" <#if entry.status=="draft">selected</#if>>draft</option>
                    <option value="published" <#if entry.status=="published">selected</#if>>published</option>
                </select>
            </label>
        </div>
        <label>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <button type="submit" class="btn btn-primary form-control">Update</button>
        </label>
    </form>
</@wrapper.main>
