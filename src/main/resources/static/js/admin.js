function makeImageUploadable(textAreaElementId, callbackId) {
    document.getElementById(textAreaElementId).addEventListener("paste", function (e) {
        const el = document.activeElement;
        for (let i = 0; i < e.clipboardData.items.length; ++i) {
            const item = e.clipboardData.items[i];
            console.log("onPaste: kind:", item.kind, "type:", item.type);

            if (item.kind === "text") {
                const [start, end] = [el.selectionStart, el.selectionEnd];
                el.setRangeText(item.getData("text"), start, end);
            } else if (item.kind === "file" && item.type.match(/^image\//)) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    console.log("Uploading image");
                    callbackWs(callbackId, e.target.result)
                };
                reader.readAsDataURL(item.getAsFile());
            }
        }
    });
}
