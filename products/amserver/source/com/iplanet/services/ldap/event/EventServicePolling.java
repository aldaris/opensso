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
 * $Id: EventServicePolling.java,v 1.2 2006-01-19 00:30:54 rarcot Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.services.ldap.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import netscape.ldap.LDAPException;
import netscape.ldap.LDAPInterruptedException;
import netscape.ldap.LDAPMessage;

import com.iplanet.am.util.SystemProperties;
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
 * <p>
 * Both these threads (EventServicePolling & TimeOut) synchronize by means of a
 * monitor object (_monitor) shared between the 2 threads.
 * </p>
 */
public class EventServicePolling extends EventService {

    private TimeOut _timeOut = null;

    private Thread _timeOutThread = null;

    private Object _monitor;

    private final int IS_MESSAGE_PROCESSED = 0;

    public synchronized String addListener(SSOToken token,
            IDSEventListener listener, String base, int scope, String filter,
            int operations) throws LDAPException, EventException {
        String requestID = super.addListener(token, listener, base, scope,
                filter, operations);

        startTimeOutThread();
        return requestID;
    }

    /**
     * Main monitor thread loop. Wait for persistent search change notifications
     */
    public void run() {
        if (debugger.messageEnabled()) {
            debugger.message("EventServicePolling.run(): Event Thread is "
                    + "running! Idle timeout = " + _idleTimeOut + " minutes.");
        }

        // Initialize the listeners
        initListeners();
        
        boolean successState = true;
        LDAPMessage message = null;

        // Used to determine if the message is processed or not
        boolean processingResult[] = new boolean[1];

        while (successState) {
            try {
                try {
                    if (debugger.messageEnabled()) {
                        debugger.message("EventServicePolling.run(): Waiting "
                                + "for response");
                    }

                    // Re-intialize the message and processResult
                    message = null;
                    processingResult[IS_MESSAGE_PROCESSED] = false;

                    message = _msgQueue.getResponse();

                    // This method call below needs to lock on monitor
                    successState = processResponse(message, processingResult);
                } catch (LDAPInterruptedException ie) {
                    // This method call below does NOT need to lock on
                    // monitor, as the TimeOut thread will be in a wait state
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
                        debugger.warning("EventServicePolling.run(): Unknown "
                                + "exception caught. Ignoring..", t);
                    }
                    sleepRetryInterval();
                }
            }
        } // end of while loop

        // The thread is being terminated for some reason.
        debugger.error("EventServicePolling.run() - Monitor thread is "
                + "terminating! Persistent Searches will no longer be "
                + "operational.");

        return; // Gracefully exit the thread
    } // end of thread

    protected EventServicePolling() throws EventException {
        super();
    }

    protected static String getName() {
        return "EventServicePolling";
    }

    protected boolean processResponse(LDAPMessage message,
            boolean[] processingResult) {
        // If this thread locks on the monitor object, the TimeOut thread should
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
        // Update the time out value for TimeOut thread and notify
        interruptTimeOutThread(!successState, _idleTimeOutMills);
        return successState;
    }

    private synchronized boolean resetTimedOutConnections() {
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
                removeListener(request);
                addListener(request.getRequester(), request.getListener(),
                        request.getBaseDn(), request.getScope(), request
                                .getFilter(), request.getOperations());
                // Remove request only after a new one was successStately added
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
            successState = processExceptionErrorCodes(le, errorCode, false);
        } catch (LDAPException e) {// Probably psearch could not be established
            if (debugger.messageEnabled()) {
                debugger.message("EventServicePolling.resetAllSearches(): "
                       + "LDAPException occurred, while trying to re-establish "
                                        + "persistent searches.", e);
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
                debugger.warning("EventServicePolling.processLDAPException() "
                        + "- LDAPException " + "received:", le);
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
        // point the TimeOut thread will be in a wait state.
        if (debugger.messageEnabled()) {
            debugger.message("EventServicePolling."
                    + "processInterruptedException() - Message present: "
                    + (message != null) + " Processed earlier: "
                    + processingResult[IS_MESSAGE_PROCESSED]);
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
        // the TimeOut thread will be in a wait() state
        if (debugger.messageEnabled()) {
            debugger.message("EventServicePolling."
                    + "processLDAPInterruptedException(): Received an "
                    + "interrupted Exception - resetting searches. Message "
                    + "present: " + (message != null) + "message proccessed: "
                    + processingResult[IS_MESSAGE_PROCESSED], ie);
        }

        // TimeOut thread has notified for resetting. First process
        // any messages if present in the queue.
        boolean successState = true;
        // If message == null, we can ignore it because this exception happened
        // on a blocked call, so ideally message should be null.
        if (message != null && !processingResult[IS_MESSAGE_PROCESSED]) {
            // Seems like we have a response
            // Now this process response won't get into a deadlock because
            // waiting to lock the monitor object because, a monitor.wait() on
            // the TimeOut thread would release the lock.
            successState = processResponse(message, processingResult);
        }

        if (successState) {
            successState = resetTimedOutConnections();
        }
        // If the success State is false the TimeOut thread is already
        // interrupted by now by the resetAllSearches() calls which are made
        // by processResponse() method of superCalls. The resetAllSearches()
        // in this class is overridden and interrupts the TimeOut thread to exit

        return successState;
    }

    /**
     * Should be called only once!!
     */
    private void startTimeOutThread() {
        if ((_timeOutThread == null) || (!_timeOutThread.isAlive())) {
            // Start the Thread when the first listener is added
            _monitor = new Object();
            _timeOut = new TimeOut(this, _idleTimeOutMills, _monitor);
            _timeOutThread = new Thread(_timeOut, "TimeOut");
            _timeOutThread.setDaemon(true);
            _timeOutThread.start();
        }
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
