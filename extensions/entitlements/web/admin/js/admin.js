var submitTimeout = null;

function doSubmit(element) {
        iceSubmitPartial(
            document.getElementById("form"),
            element,
            MouseEvent.CLICK
        );
        submitTimeout = null;
        setFocus(element);
}

function submitNow(element) {
    if (submitTimeout != null) {
        clearTimeout(submitTimeout);
    }
    submitTimeout = setTimeout(function(){doSubmit(element)}, 1000);
}

function resizeFrame(f) {
    f.style.height = f.contentWindow.document.body.scrollHeight+'px';
}

function focusWizardStep(step) {
    href = '#wstep'+step;
    window.location.href = href;
}
