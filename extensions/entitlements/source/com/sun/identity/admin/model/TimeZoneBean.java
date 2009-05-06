package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.TimeZone;

public class TimeZoneBean implements Serializable {
    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }
}
