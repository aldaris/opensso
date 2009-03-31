package com.sun.identity.admin.effect;

import com.icesoft.faces.context.effects.Highlight;

public class InputFieldErrorEffect extends Highlight {
    public InputFieldErrorEffect() {
        super();

        setStartColor("#ff0000");
        setSubmit(false);
        setTransitory(false);
    }
}
