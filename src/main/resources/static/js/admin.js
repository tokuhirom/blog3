function makeImageUploadable(textAreaElementId) {
    document.getElementById(textAreaElementId).addEventListener("paste", async function (e) {
        const el = document.activeElement;
        for (let i = 0; i < e.clipboardData.items.length; ++i) {
            const item = e.clipboardData.items[i];
            console.log("onPaste: kind!:", item.kind, "type:", item.type);

            if (item.kind === "text") {
                const [start, end] = [el.selectionStart, el.selectionEnd];
                el.setRangeText(item.getData("text"), start, end);
            } else if (item.kind === "file" && item.type.match(/^image\//)) {
                const response = await fetch("/upload_attachments", {
                    method: "POST",
                    body: item.getAsFile()
                })
                const url = await response.text();

                // insert the URL into the textarea.
                const [start, end] = [el.selectionStart, el.selectionEnd];
                el.setRangeText(`<img src="${url}">`, start, end);
            }
        }
    });
}
