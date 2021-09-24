<#import "/admin/__wrapper.ftlh" as wrapper>
<@wrapper.main>
    <h1>Create new entry</h1>
    <form method="post" action="/admin/do_create">
        <div>
            <label for="entry-title">Title</label>
            <input id="entry-title" type="text" name="title" class="form-control"
                   required>
        </div>
        <div>
            <label for="entry-body">Body</label>
            <textarea id="entry-body" name="body" cols="80" rows="20"
                      class="form-control"></textarea>
        </div>
        <div>
            <label>
                <select name="status" class="form-select form-select-lg">
                    <option value="draft">draft</option>
                    <option value="published">published</option>
                </select>
            </label>
        </div>
        <label>
            <button type="submit" class="btn btn-primary form-control">Create</button>
        </label>
    </form>
</@wrapper.main>
