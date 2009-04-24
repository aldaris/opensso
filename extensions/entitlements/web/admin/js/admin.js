function typeSubmit(element) {
    iceSubmitPartial(
        document.getElementById("form"),
        element,
        MouseEvent.CLICK
        );
}

function resizeFrame(f) {
    f.style.height = f.contentWindow.document.body.scrollHeight+'px';
}
