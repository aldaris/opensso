package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlResourceParts implements Serializable {

    private static Pattern PART_PATTERN = Pattern.compile("(\\*)|([^\\*])+");
    private List<UrlResourcePart> urlResourceParts;

    public UrlResourceParts(UrlResource ur) {
        urlResourceParts = new ArrayList<UrlResourcePart>();

        Matcher m = PART_PATTERN.matcher(ur.getName());
        while (m.find()) {
            UrlResourcePart urp = new UrlResourcePart();
            urp.setPart(m.group(0));
            urlResourceParts.add(urp);
        }
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        for (UrlResourcePart urp : getUrlResourceParts()) {
            if (urp.getPart().equals("*")) {
                String val = urp.getValue();
                if (val == null || val.length() == 0) {
                    throw new AssertionError("part value(s) are null");
                } else {
                    b.append(val);
                }
            } else {
                b.append(urp.getPart());
            }
        }

        return b.toString();
    }

    public UrlResource getUrlResource() {
        UrlResource ur = new UrlResource();
        ur.setName(toString());

        return ur;
    }

    public List<UrlResourcePart> getUrlResourceParts() {
        return urlResourceParts;
    }

    public boolean isValid() {
        for (UrlResourcePart urp : getUrlResourceParts()) {
            if (urp.getPart().equals("*")) {
                if (urp.getValue() == null || urp.getValue().length() == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
