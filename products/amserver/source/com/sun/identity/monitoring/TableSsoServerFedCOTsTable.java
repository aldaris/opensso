package com.sun.identity.monitoring;

//
// Generated by mibgen version 5.1 (05/20/05) when compiling SUN-OPENSSO-SERVER-MIB.
//

// java imports
//
import java.io.Serializable;
import java.util.Vector;

// jmx imports
//
import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.sun.management.snmp.SnmpCounter;
import com.sun.management.snmp.SnmpCounter64;
import com.sun.management.snmp.SnmpGauge;
import com.sun.management.snmp.SnmpInt;
import com.sun.management.snmp.SnmpUnsignedInt;
import com.sun.management.snmp.SnmpIpAddress;
import com.sun.management.snmp.SnmpTimeticks;
import com.sun.management.snmp.SnmpOpaque;
import com.sun.management.snmp.SnmpString;
import com.sun.management.snmp.SnmpStringFixed;
import com.sun.management.snmp.SnmpOid;
import com.sun.management.snmp.SnmpNull;
import com.sun.management.snmp.SnmpValue;
import com.sun.management.snmp.SnmpVarBind;
import com.sun.management.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.management.snmp.agent.SnmpIndex;
import com.sun.management.snmp.agent.SnmpMib;
import com.sun.management.snmp.agent.SnmpMibTable;
import com.sun.management.snmp.agent.SnmpMibSubRequest;
import com.sun.management.snmp.agent.SnmpTableEntryFactory;
import com.sun.management.snmp.agent.SnmpTableCallbackHandler;
import com.sun.management.snmp.agent.SnmpTableSupport;

/**
 * The class is used for implementing the "SsoServerFedCOTsTable" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.42.2.230.3.1.1.2.1.18.1.
 */
public class TableSsoServerFedCOTsTable extends SnmpTableSupport implements Serializable {

    /**
     * Constructor for the table. Initialize metadata for "TableSsoServerFedCOTsTable".
     * The reference on the MBean server is not updated so the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public TableSsoServerFedCOTsTable(SnmpMib myMib) {
        super(myMib);
    }

    /**
     * Constructor for the table. Initialize metadata for "TableSsoServerFedCOTsTable".
     * The reference on the MBean server is updated so the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public TableSsoServerFedCOTsTable(SnmpMib myMib, MBeanServer server) {
        this(myMib);
        this.server = server;
    }


    // ------------------------------------------------------------
    // 
    // Implements the "createNewEntry" method defined in "SnmpTableSupport".
    // See the "SnmpTableSupport" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void createNewEntry(SnmpMibSubRequest req, SnmpOid rowOid,
                 int depth, SnmpMibTable meta)
        throws SnmpStatusException {
        final SnmpIndex index = buildSnmpIndex(rowOid);
        final Vector v = index.getComponents();
        SnmpOid oid;
        try  {
            final SnmpOid oid0 = (SnmpOid) v.elementAt(0);
            final SnmpOid oid1 = (SnmpOid) v.elementAt(1);
            ObjectName objname = null;
            if (server != null)
                objname = buildNameFromIndex( index );

            // Note that when using standard metadata,
            // the returned object must implement the "SsoServerFedCOTsEntryMBean"
            // interface.
            //
            final Object entry =
                 createSsoServerFedCOTsEntryMBean(req, rowOid, depth, objname, meta,
                    oid0.toInteger(),
                    oid1.toInteger());
            if (server != null) {
                server.registerMBean(entry, objname);
            }
            meta.addEntry(rowOid,objname,entry);
        } catch(SnmpStatusException e) {
            throw e;
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
        } catch(Exception e) {
            throw new SnmpStatusException(e.getMessage());
        }
    }



    // ------------------------------------------------------------
    // 
    // Implements the "getRegisteredTableMeta" method defined in "SnmpTableSupport".
    // See the "SnmpTableSupport" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    protected SnmpMibTable getRegisteredTableMeta(SnmpMib mib)  {
        return mib.getRegisteredTableMeta("SsoServerFedCOTsTable");
    }


    // ------------------------------------------------------------
    // 
    // Implements the "removeEntryCb" method defined in "SnmpTableSupport".
    // See the "SnmpTableSupport" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void removeEntryCb(int pos, SnmpOid row, ObjectName name,
                Object entry, SnmpMibTable meta)
            throws SnmpStatusException {
        try  {
            super.removeEntryCb(pos,row,name,entry,meta);
            if (server != null && name != null)
                server.unregisterMBean(name);
        } catch (Exception x) { }
    }


    /**
     * Add a new entry to the table.
     * 
     * If the associated metadata requires ObjectNames
     * a new ObjectName will be generated using "buildNameFromIndex".
     * 
     * This method calls "addEntry" from "SnmpTableSupport".
     * See the "SnmpTableSupport" Javadoc API for more details.
     * 
     **/

    public synchronized void addEntry(SsoServerFedCOTsEntryMBean entry)
        throws SnmpStatusException {
        SnmpIndex index = buildSnmpIndex(entry);
        super.addEntry(index, (Object) entry);
    }


    /**
     * Add a new entry to the table.
     * 
     * This method calls "addEntry" from "SnmpTableSupport".
     * See the "SnmpTableSupport" Javadoc API for more details.
     * 
     **/

    public synchronized void addEntry(SsoServerFedCOTsEntryMBean entry, ObjectName name)
        throws SnmpStatusException {
        SnmpIndex index = buildSnmpIndex(entry);
        super.addEntry(index, name, (Object) entry);
    }


    /**
     * Return the entries stored in the table.
     * 
     * This method calls "getBasicEntries" from "SnmpTableSupport".
     * See the "SnmpTableSupport" Javadoc API for more details.
     * 
     **/

    public synchronized SsoServerFedCOTsEntryMBean[] getEntries() {
        Object[] array = getBasicEntries();
        SsoServerFedCOTsEntryMBean[] result = new SsoServerFedCOTsEntryMBean[array.length];
        java.lang.System.arraycopy(array,0, result,0, array.length);
        return result;
    }


    /**
     * Remove the specified entry from the table.
     * 
     * This method calls "removeEntry" from "SnmpTableSupport".
     * See the "SnmpTableSupport" Javadoc API for more details.
     * 
     **/

    public void removeEntry(SsoServerFedCOTsEntryMBean entry)
        throws SnmpStatusException {
        SnmpIndex index = buildSnmpIndex(entry);
        super.removeEntry(index, entry) ;
    }


    // ------------------------------------------------------------
    // 
    // Implements the "buildNameFromIndex" method defined in "SnmpTableSupport".
    // See the "SnmpTableSupport" Javadoc API for more details.
    // 
    // ------------------------------------------------------------


    public ObjectName buildNameFromIndex(SnmpIndex index)
        throws SnmpStatusException {
        Vector v = index.getComponents();
        SnmpOid oid;
        try  {
            oid = (SnmpOid) v.elementAt(0);
            String _keySsoServerRealmIndex = oid.toInteger().toString();
            oid = (SnmpOid) v.elementAt(1);
            String _keyFedCOTIndex = oid.toInteger().toString();
            return new ObjectName("TableSsoServerFedCOTsTable:name=com.sun.identity.monitoring.SsoServerFedCOTsEntry" + ",SsoServerRealmIndex=" + _keySsoServerRealmIndex + ",FedCOTIndex=" + _keyFedCOTIndex);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
        } catch(Exception e) {
            throw new SnmpStatusException(e.getMessage());
        }
    }

    /**
     * Build index for "SsoServerFedCOTsEntry".
     */
    public SnmpIndex buildSnmpIndex(SsoServerFedCOTsEntryMBean entry)
        throws SnmpStatusException {
        SnmpOid[] oids = new SnmpOid[2];
        SnmpValue val = null;
        val = new SnmpInt(entry.getSsoServerRealmIndex());
        oids[0] = val.toOid();
        val = new SnmpInt(entry.getFedCOTIndex());
        oids[1] = val.toOid();
        return new SnmpIndex(oids);
    }

    /**
     * Build index for "SsoServerFedCOTsEntry".
     */
    public SnmpOid buildOidFromIndex(SnmpIndex index)
        throws SnmpStatusException {
        SnmpOid oid = new SnmpOid();
        if (index.getNbComponents() != 2)
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        try {
            Vector v = index.getComponents();
            SnmpInt.appendToOid((SnmpOid)v.elementAt(0), oid);
            SnmpInt.appendToOid((SnmpOid)v.elementAt(1), oid);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        }
        return oid;
    }

    /**
     * Build index for "SsoServerFedCOTsEntry".
     */
    public SnmpOid buildOidFromIndexVal(Integer aSsoServerRealmIndex, Integer aFedCOTIndex)
        throws SnmpStatusException  {
        SnmpOid oid = new SnmpOid();
        try {
            SnmpInt.appendToOid(new SnmpInt(aSsoServerRealmIndex).toOid(), oid);
            SnmpInt.appendToOid(new SnmpInt(aFedCOTIndex).toOid(), oid);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        }
        return oid;
    }

    /**
     * Build index for "SsoServerFedCOTsEntry".
     */
    public SnmpIndex buildSnmpIndex(long[] index, int start)
        throws SnmpStatusException {
        SnmpOid[] oids = new SnmpOid[2];
        int pos = start;
        oids[0] = SnmpInt.toOid(index, pos);
        pos = SnmpInt.nextOid(index, pos);
        oids[1] = SnmpInt.toOid(index, pos);
        return new SnmpIndex(oids);
    }


    /**
     * Factory method for "SsoServerFedCOTsEntry" entry MBean class.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @return An instance of the MBean class generated for the
     *         "SsoServerFedCOTsEntry" conceptual row.
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "SsoServerFedCOTsEntryMBean"
     * interface.
     */

    public Object createSsoServerFedCOTsEntryMBean(SnmpMibSubRequest req,
                SnmpOid rowOid, int depth, ObjectName entryObjName,
                SnmpMibTable meta, Integer  aSsoServerRealmIndex, Integer  aFedCOTIndex)
            throws SnmpStatusException  {

        // Note that when using standard metadata,
        // the returned object must implement the "SsoServerFedCOTsEntryMBean"
        // interface.
        //
        SsoServerFedCOTsEntry entry = new SsoServerFedCOTsEntry(theMib);
        entry.SsoServerRealmIndex = aSsoServerRealmIndex;
        entry.FedCOTIndex = aFedCOTIndex;
        return entry;
    }


    /**
     * Reference to the MBean server.
     */
    protected MBeanServer server;

}