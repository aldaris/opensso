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
 * $Id: LDAPv3EventServicePolling.java,v 1.1 2005-11-01 00:31:13 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.idm.plugins.ldapv3;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import netscape.ldap.LDAPException;
import netscape.ldap.LDAPInterruptedException;
import netscape.ldap.LDAPMessage;

import com.iplanet.am.sdk.IdRepoListener;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.IdRepoException;

/**
 * This class extends the LDAPv3EventService class and provides the
 * functionality to operate in a mode where it can be interrupted by the
 * LDAPv3TimeOut thread, typically when time outs are required. The time outs
 * are needed when the SDK is running behind a Load Balancer/Firewall as these
 * tend to drop idle connections on a periodic basis.
 * 
 * <p>
 * Whether or not the Event Service should operate in timeout mode is determined
 * by <code>com.sun.am.event.connection.idle.timeout</code>. An instance of
 * this class is instantiated when operating in the LB/Firewall time out mode by
 * making a <code>LDAPv3EventService.getInstance()</code> call.
 * </p>
 * 
 * <p>
 * The run method functionality has been designed to recover itself successfully
 * on the occurance of an Interrupt at any point in its run() cycle.
 * </p>
 * 
 * <p>
 * This thread will interrupt the LDAPv3TimeOut thread in 2 cases:
 * <ol>
 * <li>If interrupted by the LDAPv3TimeOut thread, this thread will reset the
 * connections that have timed out, then set new time out value for the
 * LDAPv3TimeOut thread and then notify the LDAPv3TimeOut thread by means of an
 * interrupt.</li>
 * <li>If a fatal exception (such as Server stop/down) is detected by this
 * thread. This thread will try to reset all its persistent searches and when
 * successful, it notifies the LDAPv3TimeOut thread by means of an interrupt.
 * </li>
 * </ol>
 * 
 * <p>
 * Both these threads (LDAPv3EventServicePolling & LDAPv3TimeOut) synchronize by
 * means of a monitor object (_monitor) shared between the 2 threads.
 * </p>
 */
public class LDAPv3EventServicePolling extends LDAPv3EventService {

    private LDAPv3TimeOut _timeOut = null;

    private Thread _timeOutThread = null;

    private Object _monitor;

    private final int IS_MESSAGE_PROCESSED = 0;

    public synchronized void removeListener(LDAPv3Repo target) {
        // need to stop this process and the timeout monitor if no more request.
        if (debugger.messageEnabled()) {
            debugger.message("LDAPv3EventServicePolling.removeListener called");
        }
        super.removeListener(target);
        _timeOutThread.interrupt();
    }

    public synchronized String addListener(SSOToken token,
            IdRepoListener listener, String base, int scope, String filter,
            int operations, Map pluginConfig, LDAPv3Repo pluginInstance,
            String serverNames) throws LDAPException, IdRepoException {
        String requestID = super.addListener(token, listener, base, scope,
                filter, operations, pluginConfig, pluginInstance, serverNames);

        startTimeOutThread();
        return requestID;
    }

    /**
     * Main monitor thread loop. Wait for persistent search change notifications
     */
    public void run() {
        if (debugger.messageEnabled()) {
            debugger
                    .message("LDAPv3EventServicePolling.run(): Event Thread is "
                            + "running! Idle timeout = "
                            + _idleTimeOut
                            + " minutes." + " randomID=" + randomID);
        }

        boolean successState = true;
        LDAPMessage message = null;

        // Used to determine if the message is processed or not
        boolean processingResult[] = new boolean[1];

        while (successState) {
            try {
                try {
                    if (debugger.messageEnabled()) {
                        debugger.message("LDAPv3EventServicePolling.run(): " +
                                "Waiting for response. randomID=" + randomID);
                    }

                    // Re-intialize the message and processResult
                    message = null;
                    processingResult[IS_MESSAGE_PROCESSED] = false;

                    message = _msgQueue.getResponse();

                    // This method call below needs to lock on monitor
                    successState = processResponse(message, processingResult);
                } catch (LDAPInterruptedException ie) {
                    // This method call below does NOT need to lock on
                    // monitor, as the LDAPv3TimeOut thread will be in a wait
                    // state
                    successState = processLDAPInterruptedException(ie, message,
                            processingResult);
                } catch (LDAPException le) {
                    // This method call below needs to lock on monitor
                    successState = processLDAPException(le);
                }
            } catch (Throwable t) {
                // Catching Throwable to prevent the thread from exiting.
                if (t instanceof InterruptedException) {
                    successState = processInterruptedException(message,
                            processingResult, successState);
                } else {
                    if (debugger.warningEnabled()) {
                        debugger.warning(
                                "LDAPv3EventServicePolling.run(): Unknown "
                                        + "exception caught. Ignoring.."
                                        + " randomID=" + randomID, t);
                    }
                    if (_requestList.size() > 0) {
                        sleepRetryInterval();
                    }
                }
            }
            if (debugger.warningEnabled()) {
                debugger.warning("LDAPv3EventServicePolling.run(). " +
                        "_requestList.size()=" + _requestList.size());
            }
            if (_requestList.size() == 0) {
                successState = false;
            }
        } // end of while loop

        _timeOut.setExitStatus(true);

        if (debugger.warningEnabled()) {
            debugger.error("LDAPv3EventServicePolling.run() - Monitor " +
                            "thread is terminating! Persistent Searches will " +
                            "no longer be operational.  randomID=" + randomID);
        }

        return; // Gracefully exit the thread
    } // end of thread

    protected LDAPv3EventServicePolling(Map pluginConfig, String serverNames)
            throws LDAPException {
        super(pluginConfig, serverNames);
        if (debugger.messageEnabled()) {
            debugger.message("LDAPv3EventServicePolling.constructor()"
                    + " exit. randomID=" + randomID);
        }
    }

    protected String getName() {
        return "LDAPv3EventServicePolling";
    }

    protected boolean processResponse(LDAPMessage message,
            boolean[] processingResult) {
        // If this thread locks on the monitor object, the LDAPv3TimeOut thread
        // should
        // wait until the responses are processed before issuing an interrupt.
        synchronized (_monitor) {
            boolean successState = true;
            successState = super.processResponse(message);
            processingResult[IS_MESSAGE_PROCESSED] = true;
            message = null; // De-reference the processed message
            return successState;
        }
    }

    protected boolean resetAllSearches(boolean clearCaches) {
        boolean successState = super.resetAllSearches(clearCaches);
        // Update the time out value for LDAPv3TimeOut thread and notify
        interruptTimeOutThread(!successState, _idleTimeOutMills);
        return successState;
    }

    private synchronized boolean resetTimedOutConnections() {
        // Determine the connections that have timed out
        Set resetList = new HashSet();
        long nextTimeOut = _idleTimeOutMills;
        long currentTime = System.currentTimeMillis();

        if (debugger.messageEnabled()) {
            debugger.message(
                    "LDAPv3EventServicePolling.resetTimedOutConnections():"
                            + " determining timed out connections. randomID="
                            + randomID);
        }

        Collection requestObjs = _requestList.values();
        Iterator iter = requestObjs.iterator();
        while (iter.hasNext()) {
            Request request = (Request) iter.next();
            long lastUpdatedTime = request.getLastUpdatedTime();
            if (checkIfTimedOut(currentTime, lastUpdatedTime)) {
                if (debugger.messageEnabled()) {
                    debugger.message("LDAPv3EventServicePolling."
                            + "resetTimedOutConnections(): the following "
                            + "request: " + request.getListener()
                            + " has timed" + " out. Current Time: "
                            + currentTime + " Last " + "updated time: "
                            + request.getLastUpdatedTime() + " randomID="
                            + randomID);
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
     * Should be called only in Polling mode. This method will interrupt the
     * time out thread.
     * 
     * @param resetList
     * @param nextTimeOut
     * @return
     */
    private synchronized boolean resetTimedOutSearches(Set resetList,
            long nextTimeOut) {
        // Now Reset the searches that have timed out
        if (debugger.messageEnabled()) {
            debugger.message("LDAPv3EventServicePolling.resetAllSearches(): "
                    + resetList.size() + " connections (searches) timed out!"
                    + " randomID=" + randomID);
        }

        boolean successState = true;

        // By default, if all the searches need to be reset timeOut =
        // total idle time out.
        long timeOut = _idleTimeOutMills;
        try {
            Iterator reqIter = resetList.iterator();
            while (reqIter.hasNext()) {
                Request request = (Request) reqIter.next();
                removeListener(request);
                addListener(request.getRequester(), request.getListener(),
                        request.getBaseDn(), request.getScope(), request
                                .getFilter(), request.getOperations(), request
                                .getPluginConfig(), request.getOwner(), request
                                .getServerNames());
                // Remove request only after a new one was successStately added
                _requestList.remove(request.getRequestID());
            }
            // Listeners were successStately established. So, timeOut will be
            // the value of next connection time out.
            timeOut = nextTimeOut;
        } catch (IdRepoException le) {
            // Something wrong with establishing connection. All searches need
            // to be restared. Also reset timeout value back to original value
            if (debugger.messageEnabled()) {
                debugger.message(
                        "LDAPv3EventServicePolling.resetAllSearches(): " +
                        "IdRepException occurred while re-establishing " +
                        "listeners.  randomID=" + randomID, le);
            }
            // this error is return by addListener only if the filter is not
            // valid.
            int errorCode = 87;
            successState = processExceptionErrorCodes(le, errorCode, false);
        } catch (LDAPException e) {// Probably psearch could not be established
            if (debugger.messageEnabled()) {
                debugger.message(
                        "LDAPv3EventServicePolling.resetAllSearches(): " +
                        "LDAPException occurred, while trying to re-establish "+
                        "persistent searches. randomID=" + randomID, e);
            }
            int errorCode = e.getLDAPResultCode();
            successState = processExceptionErrorCodes(e, errorCode, false);
        } finally {
            interruptTimeOutThread(!successState, timeOut);
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

    private boolean processExceptionErrorCodes(Exception ex, int errorCode,
            boolean interrupt) {
        boolean successState = true;
        if (_retryErrorCodes.contains(Integer.toString(errorCode))) {
            // Call Only the parent method, because at this point we
            // want to interrupt only if required.
            successState = super.resetAllSearches(true);
            if (interrupt) {
                interruptTimeOutThread(!successState, _idleTimeOutMills);
            }
        } else { // Some other error
            processNetworkError(ex);
        }
        return successState;
    }

    private boolean processLDAPException(LDAPException le) {
        // We do not want to get interrupted while we are processing the
        // exception error response. So, we lock on the monitor object
        synchronized (_monitor) {
            if (debugger.warningEnabled()) {
                debugger.warning(
                        "LDAPv3EventServicePolling.processLDAPException() "
                                + "- LDAPException " + "received:  randomID="
                                + randomID, le);
            }
            boolean successState = true;
            int code = le.getLDAPResultCode();
            successState = processExceptionErrorCodes(le, code, true);
            return successState;
        }
    }

    private boolean processInterruptedException(LDAPMessage message,
            boolean[] processingResult, boolean successState) {
        // The flow can get to this part, in 2 cases
        // 1 - Got a message (response) and before it could be processed, it got
        // interrupted, OR
        // Got interrupted after the message was processed.
        // 2 - An LDAPException occurred, before the exception could
        // be processed it got interrupted.
        // 
        // Now the following body of the method handles case - 1 correctly. But,
        // if 2 happens (edge case), we end up re-establishing all searches
        // since message would be null. That is okay and the system will
        // recover.
        //
        // Also: No need to synchronize on the monitor object because at this
        // point the LDAPv3TimeOut thread will be in a wait state.
        if (debugger.messageEnabled()) {
            debugger.message("LDAPv3EventServicePolling."
                    + "processInterruptedException() - Message present: "
                    + (message != null) + " Processed earlier: "
                    + processingResult[IS_MESSAGE_PROCESSED] + " randomID="
                    + randomID);
        }
        boolean resultState = successState; // honor the previous success state
        // Check the processing result too see if the message was already
        // processed
        if (!processingResult[IS_MESSAGE_PROCESSED]) {
            // Make sure that the message that was not processed is processed.
            resultState = processResponse(message, processingResult);
        }

        if (resultState) {
            resultState = resetTimedOutConnections();
        }

        return resultState;
    }

    private boolean processLDAPInterruptedException(
            LDAPInterruptedException ie, LDAPMessage message,
            boolean[] processingResult) {
        // No need to synchronize on the monitor object in this method, because
        // the LDAPv3TimeOut thread will be in a wait() state
        if (debugger.messageEnabled()) {
            debugger.message("LDAPv3EventServicePolling."
                    + "processLDAPInterruptedException(): Received an "
                    + "interrupted Exception - resetting searches. Message "
                    + "present: " + (message != null) + "message proccessed: "
                    + processingResult[IS_MESSAGE_PROCESSED] + " randomID="
                    + randomID, ie);
        }

        // LDAPv3TimeOut thread has notified for resetting. First process
        // any messages if present in the queue.
        boolean successState = true;
        // If message == null, we can ignore it because this exception happened
        // on a blocked call, so ideally message should be null.
        if (message != null && !processingResult[IS_MESSAGE_PROCESSED]) {
            // Seems like we have a response
            // Now this process response won't get into a deadlock because
            // waiting to lock the monitor object because, a monitor.wait() on
            // the LDAPv3TimeOut thread would release the lock.
            successState = processResponse(message, processingResult);
        }

        if (successState) {
            successState = resetTimedOutConnections();
        }
        // If the success State is false the LDAPv3TimeOut thread is already
        // interrupted by now by the resetAllSearches() calls which are made
        // by processResponse() method of superCalls. The resetAllSearches()
        // in this class is overridden and interrupts the LDAPv3TimeOut thread
        // to exit

        return successState;
    }

    /**
     * Should be called only once!!
     */
    private void startTimeOutThread() {
        if ((_timeOutThread == null) || (!_timeOutThread.isAlive())) {
            // Start the Thread when the first listener is added
            _monitor = new Object();
            _timeOut = new LDAPv3TimeOut(this, _idleTimeOutMills, _monitor);
            _timeOutThread = new Thread(_timeOut, "LDAPv3TimeOut");
            _timeOutThread.setDaemon(true);
            _timeOutThread.start();
        }
    }

    private void interruptTimeOutThread(boolean flag, long timeOut) {
        if (debugger.messageEnabled()) {
            debugger.message(
                    "LDAPv3EventServicePolling.interruptTimeOutThread(): " +
                    "Interrupting TimeOut thread with exit status: " + flag +
                    " time out: " + timeOut + " randomID="+ randomID);
        }
        _timeOut.setTimeOutValue(timeOut);
        _timeOut.setExitStatus(flag);
        _timeOutThread.interrupt();
    }

    protected Thread getServiceThread() {
        return _monitorThread;
    }
}
