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
 * $Id: EventServicePolling.java,v 1.3 2007-04-09 23:26:01 goodearth Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.services.ldap.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import netscape.ldap.LDAPException;
import com.iplanet.services.ldap.LDAPServiceException;
import com.iplanet.sso.SSOToken;

/**
 * This class extends the EventService class and provides the functionality to
 * operate in a mode where it can be interrupted by the TimeOut thread,
 * typically when time outs are required. The time outs are needed when the SDK
 * is running behind a Load Balancer/Firewall as these tend to drop idle
 * connections on a periodic basis.
 * 
 * <p>
 * Whether or not the Event Service should operate in timeout mode is determined
 * by <code>com.sun.am.event.connection.idle.timeout</code>. An instance of
 * this class is instantiated when operating in the LB/Firewall time out mode by
 * making a <code>EventService.getInstance()</code> call.
 * </p>
 * 
 * <p>
 * The run method functionality has been designed to recover itself successfully
 * on the occurance of an Interrupt at any point in its run() cycle.
 * </p>
 * 
 * <p>
 * This thread will interrupt the TimeOut thread in 2 cases:
 * <ol>
 * <li>If interrupted by the TimeOut thread, this thread will reset the
 * connections that have timed out, then set new time out value for the TimeOut
 * thread and then notify the TimeOut thread by means of an interrupt.</li>
 * <li>If a fatal exception (such as Server stop/down) is detected by this
 * thread. This thread will try to reset all its persistent searches and when
 * successful, it notifies the TimeOut thread by means of an interrupt.</li>
 * </ol>
 * 
 */
public class EventServicePolling extends EventService {

    private TimeOut _timeOut = null;

    private Thread _timeOutThread = null;

    public synchronized String addListener(SSOToken token,
            IDSEventListener listener, String base, int scope, String filter,
            int operations) throws LDAPException, EventException {
        String requestID = super.addListener(token, listener, base, scope,
                filter, operations);

        startTimeOutThread();
        return requestID;
    }

    protected EventServicePolling() throws EventException {
        super();
    }

    protected static String getName() {
        return "EventServicePolling";
    }

    public synchronized boolean resetAllSearches(boolean clearCaches) {
        boolean successState = super.resetAllSearches(clearCaches);
        // Update the time out value for TimeOut thread and notify
        interruptTimeOutThread(!successState, _idleTimeOutMills);
        return successState;
    }

    // This method will be called by TimeOut thread. At the end of this call
    // parameters for TimeOut thread are updated appropriately.
    protected synchronized boolean resetTimedOutConnections() {
        // Determine the connections that have timed out
        Set resetList = new HashSet();
        long nextTimeOut = _idleTimeOutMills;
        long currentTime = System.currentTimeMillis();

        if (debugger.messageEnabled()) {
            debugger.message("EventServicePolling.resetTimedOutConnections():"
                    + " determining timed out connections.");
        }

        Collection requestObjs = _requestList.values();
        Iterator iter = requestObjs.iterator();
        while (iter.hasNext()) {
            Request request = (Request) iter.next();
            long lastUpdatedTime = request.getLastUpdatedTime();
            if (checkIfTimedOut(currentTime, lastUpdatedTime)) {
                if (debugger.messageEnabled()) {
                    debugger.message("EventServicePolling."
                            + "resetTimedOutConnections(): the following "
                            + "request: " + request.getListener()
                            + " has timed" + " out. Current Time: "
                            + currentTime + " Last " + "updated time: "
                            + request.getLastUpdatedTime());
                }
                resetList.add(request);
            } else { // Determine which connection will time out first
                long timeOut = lastUpdatedTime + _idleTimeOutMills
                        - currentTime;
                nextTimeOut = (timeOut < nextTimeOut) ? timeOut : nextTimeOut;
            }
        }

        boolean successState = true;
        if (nextTimeOut == _idleTimeOutMills) {
            // All the searches need to be re-established
            successState = resetAllSearches(false);
        } else {
            // Only a few of them need to be re-established
            successState = resetTimedOutSearches(resetList, nextTimeOut);
        }

        return successState;
    }

    /**
     * Should be called only in Polling mode. This method will update the
     * parameters of TimeOut thread
     *
     * @param resetList Set of Request objects whoses connections need to be
     * reset.
     * @param nextTimeOut the amount of time the TimeOut thread should sleep,
     * in other words the time for next time out.
     * @return true if the connections were successfully established. false
     * if the connections could not be successfully established even after the
     * retry.
     */
    private synchronized boolean resetTimedOutSearches(Set resetList,
            long nextTimeOut) {
        // Now Reset the searches that have timed out
        if (debugger.messageEnabled()) {
            debugger.message("EventServicePolling.resetAllSearches(): "
                    + resetList.size() + " connections (searches) timed out!");
        }

        boolean successState = true;

        // By default, if all the searches need to be reset timeOut =
        // total idle time out.
        long timeOut = _idleTimeOutMills;
        try {
            Iterator reqIter = resetList.iterator();
            while (reqIter.hasNext()) {
                Request request = (Request) reqIter.next();
                addListener(request.getRequester(), request.getListener(),
                        request.getBaseDn(), request.getScope(), request
                                .getFilter(), request.getOperations());
                // Remove request only after a new one was successfully added

                removeListener(request);

                // Remove the old request object from the list.
                _requestList.remove(request.getRequestID());
            }
            // Listeners were successStately established. So, timeOut will be
            // the value of next connection time out.
            timeOut = nextTimeOut;
        } catch (LDAPServiceException le) {
            // Something wrong with establishing connection. All searches need
            // to be restared. Also reset timeout value back to original value
            if (debugger.messageEnabled()) {
                debugger.message("EventServicePolling.resetAllSearches(): "
                        + " LDAPServiceException occurred while re-establishing"
                                        + "listeners. ", le);
            }
            int errorCode = le.getLDAPExceptionErrorCode();
            successState = processExceptionErrorCodes(le, errorCode);
        } catch (LDAPException e) {// Probably psearch could not be established
            if (debugger.messageEnabled()) {
                debugger.message("EventServicePolling.resetAllSearches(): "
                       + "LDAPException occurred, while trying to re-establish "
                                        + "persistent searches.", e);
            }
            int errorCode = e.getLDAPResultCode();
            successState = processExceptionErrorCodes(e, errorCode);
        } finally {
            updateTimeOutThreadParams(!successState, timeOut);
        }

        return successState;
    }

    private boolean checkIfTimedOut(long currentTime, long lastUpdatedTime) {
        boolean timedOut = false;
        long timeDiffMills = currentTime - lastUpdatedTime;
        long timeDiffMinutes = (timeDiffMills / 60000);

        long elapsedTime = _idleTimeOut - timeDiffMinutes;
        if (elapsedTime <= 1) {
            // If the range is less that or equal to 1 minutes => Reset
            timedOut = true;
        }
        return timedOut;
    }

    private boolean processExceptionErrorCodes(Exception ex, int errorCode) {
            
        boolean successState = true;
        if (_retryErrorCodes.contains(Integer.toString(errorCode))) {
            // Call Only the parent method, because at this point we
            // want to interrupt only if required.
            successState = super.resetAllSearches(true);
        } else { // Some other error
            processNetworkError(ex);
        }
        return successState;
    }

    /**
     * Should be called only once!!
     */
    private void startTimeOutThread() {
        if ((_timeOutThread == null) || (!_timeOutThread.isAlive())) {
            // Start the Thread when the first listener is added
            _timeOut = new TimeOut(this, _idleTimeOutMills);
            _timeOutThread = new Thread(_timeOut, "TimeOut");
            _timeOutThread.setDaemon(true);
            _timeOutThread.start();
        }
    }

    private void updateTimeOutThreadParams(boolean flag, long timeOut) {
        if (debugger.messageEnabled()) {
            debugger.message("EventServicePolling.updateTimeOutThreadParams(): "

                    + " updating TimeOut thread params with exit status: "
                    + flag + " time out: " + timeOut);
        }
        _timeOut.setTimeOutValue(timeOut);
        _timeOut.setExitStatus(flag);
    }

    private void interruptTimeOutThread(boolean flag, long timeOut) {
        if (debugger.messageEnabled()) {
            debugger.message("EventServicePolling.interruptTimeOutThread(): "
                    + " Interrupting TimeOut thread with exit status: " + flag
                    + " time out: " + timeOut);
        }
        _timeOut.setTimeOutValue(timeOut);
        _timeOut.setExitStatus(flag);
        _timeOutThread.interrupt();
    }

    protected Thread getServiceThread() {
        return _monitorThread;
    }
}
