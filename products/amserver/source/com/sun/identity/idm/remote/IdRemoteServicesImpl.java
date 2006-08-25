/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: IdRemoteServicesImpl.java,v 1.5 2006-08-25 21:20:54 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.idm.remote;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.security.auth.callback.Callback;

import com.iplanet.am.sdk.AMHashMap;
import com.iplanet.am.sdk.AMSDKBundle;
import com.sun.identity.shared.debug.Debug;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdOperation;
import com.sun.identity.idm.IdRepo;
import com.sun.identity.idm.IdServices;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchOpModifier;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.shared.jaxrpc.SOAPClient;
import com.sun.identity.sm.SchemaType;
import com.sun.identity.common.CaseInsensitiveHashMap;

/*
 * Class that implements the remote services that are needed for IdRepo.
 */
public class IdRemoteServicesImpl implements IdServices {

    // TODO: Use a different JAX-RPC interface
    protected static final String SDK_SERVICE = "DirectoryManagerIF";

    protected static final String AMSR_COUNT = "__count";

    protected static final String AMSR_RESULTS = "__results";

    protected static final String AMSR_CODE = "__errorCode";

    protected static final String AMSR_ATTRS = "__attrs";

    private SOAPClient client;

    private static Debug debug = Debug.getInstance("amIdmClient");

    private static IdServices instance;

    protected static Debug getDebug() {
        return debug;
    }

    protected static synchronized IdServices getInstance() {
        if (instance == null) {
            getDebug().message("IdRemoteServicesImpl.getInstance(): "
                    + "Creating new Instance of IdRemoteServicesImpl()");
            instance = new IdRemoteServicesImpl();
        }
        return instance;
    }

    protected IdRemoteServicesImpl() {
        client = new SOAPClient(SDK_SERVICE);
    }

    /**
     * Returns <code>true</code> if the data store has successfully
     * authenticated the identity with the provided credentials. In case the
     * data store requires additional credentials, the list would be returned
     * via the <code>IdRepoException</code> exception.
     * 
     * @param orgName
     *            realm name to which the identity would be authenticated
     * @param credentials
     *            Array of callback objects containing information such as
     *            username and password.
     * 
     * @return <code>true</code> if data store authenticates the identity;
     *         else <code>false</code>
     */
    public boolean authenticate(String orgName, Callback[] credentials) {
        if (getDebug().messageEnabled()) {
            getDebug().message("IdRemoteServicesImpl.authenticate(): "
                    + " Not supported for remote clients");
        }

        // Not supported for remote
        return false;
    }

    public AMIdentity create(SSOToken token, IdType type, String name,
            Map attrMap, String amOrgName) throws IdRepoException, SSOException
    {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, attrMap, amOrgName };
            String univid = (String) client.send(client.encodeMessage(
                    "create_idrepo", objs), null);
            return IdUtils.getIdentity(token, univid);
        } catch (RemoteException rex) {
            getDebug().error(
                    "IdRemoteServicesImpl.create_idrepo: caught exception=",
                    rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error(
                    "IdRemoteServicesImpl.create_idrepo: caught exception=",
                    ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    public void delete(SSOToken token, IdType type, String name,
            String orgName, String amsdkDN) throws IdRepoException,
            SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, orgName, amsdkDN };
            client.send(client.encodeMessage("delete_idrepo", objs), null);
        } catch (RemoteException rex) {
            getDebug().error(
                    "IdRemoteServicesImpl.create_idrepo: caught exception=",
                    rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error(
                    "IdRemoteServicesImpl.create_idrepo: caught exception=",
                    ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public Map getAttributes(SSOToken token, IdType type, String name,
            Set attrNames, String amOrgName, String amsdkDN, boolean isString)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, attrNames, amOrgName, amsdkDN };
            Map res = ((Map) client.send(client.encodeMessage(
                    "getAttributes1_idrepo", objs), null));
            if (res != null) {
                Map res2 = new AMHashMap();
                Iterator it = res.keySet().iterator();
                while (it.hasNext()) {
                    Object attr = it.next();
                    res2.put(attr, res.get(attr));
                }
                res = res2;
            }
            return res;
        } catch (RemoteException rex) {
            getDebug().error("IdRemoteServicesImpl.getAttributes1_idrepo: " 
                    + "caught exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error("IdRemoteServicesImpl.getAttributes1_idrepo: " 
                    + "caught exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public Map getAttributes(SSOToken token, IdType type, String name,
            String amOrgName, String amsdkDN) throws IdRepoException,
            SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, amOrgName, amsdkDN };
            Map res = ((Map) client.send(client.encodeMessage(
                    "getAttributes2_idrepo", objs), null));
            if (res != null) {
                Map res2 = new AMHashMap();
                Iterator it = res.keySet().iterator();
                while (it.hasNext()) {
                    Object attr = it.next();
                    res2.put(attr, res.get(attr));
                }
                res = res2;
            }
            return res;
        } catch (RemoteException rex) {
            getDebug().error("IdRemoteServicesImpl.getAttributes2_idrepo: " 
                    + "caught exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error("IdRemoteServicesImpl.getAttributes2_idrepo: " 
                    + "caught exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public void removeAttributes(SSOToken token, IdType type, String name,
            Set attrNames, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, attrNames, amOrgName, amsdkDN };
            client.send(client.encodeMessage("removeAttributes_idrepo", objs),
                    null);
        } catch (RemoteException rex) {
            getDebug().error("IdRemoteServicesImpl.removeAttributes_idrepo: " 
                    + "caught exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error(
                    "IdRemoteServicesImpl.removeAttributes_idrepo: caught "
                    + "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public IdSearchResults search(SSOToken token, IdType type, String pattern,
            IdSearchControl ctrl, String amOrgName) throws IdRepoException,
            SSOException {
        IdSearchOpModifier modifier = ctrl.getSearchModifier();
        Map avMap = ctrl.getSearchModifierMap();
        int filterOp;
        if (modifier.equals(IdSearchOpModifier.AND)) {
            filterOp = IdRepo.AND_MOD;
        } else {
            filterOp = IdRepo.OR_MOD;
        }
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    pattern, new Integer(ctrl.getTimeOut()),
                    new Integer(ctrl.getMaxResults()),
                    ctrl.getReturnAttributes(),
                    new Boolean(ctrl.isGetAllReturnAttributesEnabled()),
                    new Integer(filterOp), avMap,
                    new Boolean(ctrl.isRecursive()), amOrgName };
            Map idresults = ((Map) client.send(client.encodeMessage(
                    "search2_idrepo", objs), null));
            return mapToIdSearchResults(token, type, amOrgName, idresults);
        } catch (RemoteException rex) {
            getDebug().error(
                    "IdRemoteServicesImpl.search2_idrepo: caught exception=",
                    rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error(
                    "IdRemoteServicesImpl.search2_idrepo: caught exception=",
                    ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    public void setAttributes(SSOToken token, IdType type, String name,
            Map attributes, boolean isAdd, String amOrgName, String amsdkDN,
            boolean isString) throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, attributes, new Boolean(isAdd), amOrgName, amsdkDN,
                    new Boolean(isString) };
            client.send(client.encodeMessage("setAttributes2_idrepo", objs),
                    null);

        } catch (RemoteException rex) {
            getDebug().error("IdRemoteServicesImpl.setAttributes_idrepo: " 
                    + "caught exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error("IdRemoteServicesImpl.setAttributes_idrepo: " 
                    + "caught exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public void assignService(SSOToken token, IdType type, String name,
            String serviceName, SchemaType stype, Map attrMap,
            String amOrgName, String amsdkDN) throws IdRepoException,
            SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, serviceName, stype.getType(), attrMap, amOrgName,
                    amsdkDN };
            client.send(client.encodeMessage("assignService_idrepo", objs),
                    null);

        } catch (RemoteException rex) {
            getDebug().error("IdRemoteServicesImpl.assignService_idrepo: " 
                    + "caught exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error("IdRemoteServicesImpl.assignService_idrepo: " 
                    + "caught exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    public Set getAssignedServices(SSOToken token, IdType type, String name,
            Map mapOfServiceNamesAndOCs, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, mapOfServiceNamesAndOCs, amOrgName, amsdkDN };
            return ((Set) client.send(client.encodeMessage(
                    "getAssignedServices_idrepo", objs), null));

        } catch (RemoteException rex) {
            getDebug().error(
                    "IdRemoteServicesImpl.getAssignedServices_idrepo: caught "
                            + "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error(
                    "IdRemoteServicesImpl.getAssignedServices_idrepo: caught "
                            + "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public Map getServiceAttributes(SSOToken token, IdType type, String name,
            String serviceName, Set attrNames, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, serviceName, attrNames, amOrgName, amsdkDN };
            return ((Map) client.send(client.encodeMessage(
                    "getServiceAttributes_idrepo", objs), null));

        } catch (RemoteException rex) {
            getDebug().error(
                    "IdRemoteServicesImpl.getServiceAttributes_idrepo: caught "
                            + "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error(
                    "IdRemoteServicesImpl.getServiceAttributes_idrepo: caught "
                            + "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public void unassignService(SSOToken token, IdType type, String name,
            String serviceName, Map attrMap, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, serviceName, attrMap, amOrgName, amsdkDN };
            client.send(client.encodeMessage("unassignService_idrepo", objs),
                    null);

        } catch (RemoteException rex) {
            getDebug().error("IdRemoteServicesImpl.unassignService_idrepo: "
                    + "caught exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error("IdRemoteServicesImpl.unassignService_idrepo: " 
                    + "caught exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    public void modifyService(SSOToken token, IdType type, String name,
            String serviceName, SchemaType stype, Map attrMap,
            String amOrgName, String amsdkDN) throws IdRepoException,
            SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, serviceName, stype.getType(), attrMap, amOrgName,
                    amsdkDN };
            client.send(client.encodeMessage("modifyService_idrepo", objs),
                    null);

        } catch (RemoteException rex) {
            getDebug().error("IdRemoteServicesImpl.modifyService_idrepo: " +
                    "caught exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error("IdRemoteServicesImpl.modifyService_idrepo: " +
                    "caught exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    public Set getMembers(SSOToken token, IdType type, String name,
            String amOrgName, IdType membersType, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, amOrgName, membersType.getName(), amsdkDN };
            Set res = (Set) client.send(client.encodeMessage(
                    "getMembers_idrepo", objs), null);
            Set idres = new HashSet();
            if (res != null) {
                Iterator it = res.iterator();
                while (it.hasNext()) {
                    String univid = (String) it.next();
                    AMIdentity id = IdUtils.getIdentity(token, univid);
                    idres.add(id);
                }
            }
            return idres;
        } catch (RemoteException rex) {
            getDebug().error("IdRemoteServicesImpl.getMembers_idrepo: " 
                    + "caught exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error("IdRemoteServicesImpl.getMembers_idrepo: " 
                    + "caught exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public Set getMemberships(SSOToken token, IdType type, String name,
            IdType membershipType, String amOrgName, String amsdkDN)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, membershipType.getName(), amOrgName, amsdkDN };
            Set res = (Set) client.send(client.encodeMessage(
                    "getMemberships_idrepo", objs), null);
            Set idres = new HashSet();
            if (res != null) {
                Iterator it = res.iterator();
                while (it.hasNext()) {
                    String univid = (String) it.next();
                    AMIdentity id = IdUtils.getIdentity(token, univid);
                    idres.add(id);
                }
            }
            return idres;

        } catch (RemoteException rex) {
            getDebug().error("IdRemoteServicesImpl.getMemberships_idrepo: " 
                    + "caught exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error("IdRemoteServicesImpl.getMemberships_idrepo: " +
                    "caught exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public void modifyMemberShip(SSOToken token, IdType type, String name,
            Set members, IdType membersType, int operation, String amOrgName)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, members, membersType.getName(),
                    new Integer(operation), amOrgName };
            client.send(client.encodeMessage("modifyMemberShip_idrepo", objs),
                    null);

        } catch (RemoteException rex) {
            getDebug().error(
                    "IdRemoteServicesImpl.modifyMemberShip_idrepo: caught "
                            + "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error(
                    "IdRemoteServicesImpl.modifyMemberShip_idrepo: caught "
                            + "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }

    }

    public Set getSupportedOperations(SSOToken token, IdType type,
            String amOrgName) throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    amOrgName };
            Set ops = (Set) client.send(client.encodeMessage(
                    "getSupportedOperations_idrepo", objs), null);
            Set resOps = new HashSet();
            if (ops != null) {
                Iterator it = ops.iterator();
                while (it.hasNext()) {
                    String op = (String) it.next();
                    IdOperation idop = new IdOperation(op);
                    resOps.add(idop);
                }
            }
            return resOps;
        } catch (RemoteException rex) {
            getDebug().error("IdRemoteServicesImpl." 
                    + "getSupportedOperations_idrepo: caught "
                    + "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error(
                    "IdRemoteServicesImpl.getSupportedOperations_idrepo: " +
                    "caught exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public Set getSupportedTypes(SSOToken token, String amOrgName)
            throws IdRepoException, SSOException {
        try {
            Object[] objs = { token.getTokenID().toString(), amOrgName };
            Set types = (Set) client.send(client.encodeMessage(
                    "getSupportedTypes_idrepo", objs), null);
            Set resTypes = new HashSet();
            if (types != null) {
                Iterator it = types.iterator();
                while (it.hasNext()) {
                    String currType = (String) it.next();
                    IdType thisType = IdUtils.getType(currType);
                    resTypes.add(thisType);
                }
            }
            return resTypes;
        } catch (RemoteException rex) {
            getDebug().error(
                    "IdRemoteServicesImpl.getSupportedTypes_idrepo: caught "
                            + "exception=", rex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        } catch (IdRepoException ide) {
            throw (ide);
        } catch (Exception ex) {
            getDebug().error(
                    "IdRemoteServicesImpl.getSupportedTypes_idrepo: caught "
                            + "exception=", ex);
            throw new IdRepoException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public boolean isExists(SSOToken token, IdType type, String name,
            String amOrgName) throws SSOException, IdRepoException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isActive(SSOToken token, IdType type, String name,
            String amOrgName, String amsdkDN) throws SSOException,
            IdRepoException {
        try {
            Object[] objs = { token.getTokenID().toString(), type.getName(),
                    name, amOrgName, amsdkDN };
            Boolean res = ((Boolean) client.send(client.encodeMessage(
                    "isActive_idrepo", objs), null));
            return res.booleanValue();
        } catch (RemoteException rex) {
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public void clearIdRepoPlugins() {
        // Nothing to do!
    }

    public void reloadIdRepoServiceSchema() {
        // Do Nothing !!
    }

    public void reinitialize() {
        // Do Nothing !!
    }

    private IdSearchResults mapToIdSearchResults(SSOToken token, IdType type,
            String orgName, Map m) throws IdRepoException {
        IdSearchResults results = new IdSearchResults(type, orgName);
        Set idSet = (Set) m.get(AMSR_RESULTS);
        Map attrMaps = (Map) m.get(AMSR_ATTRS);
        Integer err = (Integer) m.get(AMSR_CODE);

        if (idSet != null) {
            Iterator it = idSet.iterator();
            while (it.hasNext()) {
                String idStr = (String) it.next();
                AMIdentity id = IdUtils.getIdentity(token, idStr);
                CaseInsensitiveHashMap attrMap =
                    new CaseInsensitiveHashMap((Map) attrMaps.get(idStr));
                results.addResult(id, attrMap);
            }
        }
        if (err != null) {
            results.setErrorCode(err.intValue());
        }
        return results;
    }
}
