package com.sun.identity.entitlement;

public abstract class VirtualSubject implements EntitlementSubject {
    public enum VirtualId {
        ANY_USER,
        AUTHENTICATED;

        public VirtualSubject newVirtualSubject() {
            switch (this) {
                case ANY_USER:
                    return new AnyUserSubject();

                case AUTHENTICATED:
                    return new AuthenticatedESubject();

                default:
                    throw new AssertionError("undefined virtual ID:" + this);
            }
        }
    }

    public abstract VirtualId getVirtualId();
}
