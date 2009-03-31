package com.sun.identity.admin.effect;

import com.icesoft.faces.context.effects.Pulsate;

public class MessageErrorEffect extends Pulsate {
    public MessageErrorEffect() {
        super();

        setDuration(2);
        setSubmit(false);
        setTransitory(false);
    }
}
