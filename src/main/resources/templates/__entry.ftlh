<#-- @ftlvariable name="entry" type="me.geso.blog3.entity.Entry" -->
<div class="entry">
    <h2><a href="/entry/${entry.path}">${entry.title}</a></h2>
    <p>
        ${entry.html()?no_esc}
    </p>
    <div class="entry-footer">
        <div>Created: ${entry.createdAt}</div>
        <div>Updated: ${entry.updatedAt!'-'}</div>
    </div>
</div>
