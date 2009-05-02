function submitNow(element) {
    iceSubmitPartial(
        document.getElementById("form"),
        element,
        MouseEvent.CLICK
    );
}

function resizeFrame(f) {
    f.style.height = f.contentWindow.document.body.scrollHeight+'px';
}

function focusWizardStep(step) {
    href = '#wstep'+step;
    window.location.href = href;
}