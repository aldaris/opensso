This submission modifies the LDAPsdk classes to provide support for the RFC's
3771 (Intermediate Response Message) and 4533 (Content Synchrionization
Operation). RFC 4533 is dependent on RFC 3771. RFC 4533 is used by OpenLDAP for
content synchronization (replication) and can also be used as an extended
search operation. OpenSSO requires that its configured LDAP server support LDAP
Persistent Search. By adding support for RFC 4533 to the LDAPsdk, the OpenSSO
ldapv3 plugin is then modified to use this as an alternative extended search
mechanism.
