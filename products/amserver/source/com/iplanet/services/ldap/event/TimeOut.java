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
 * $Id: TimeOut.java,v 1.1 2005-11-01 00:30:21 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.services.ldap.event;

import com.iplanet.am.util.Debug;

/**
 * Class which incorporates the functionality to interrupt the Event Service
 * when the time out occurs. An instance of this class runs as separate (single)
 * thread which is created by the EventServicePolling class when configured to
 * run against a Load Balancer/Firewall (configured by property:
 * <code>com.sun.am.event.connection.idle.timeout</code>
 * 
 * <p>
 * The thread instance of this class sleeps for a duration specified by the time
 * out value. The amount of time this thread needs sleep (in other words the
 * time at which it has to notify the EventServicePolling thread) is dictated by
 * the EventServicePolling thread, which determines that value based on the
 * connection that is expected to time out first.
 * 
 * <p>
 * After sleeping for the required amount this thread wakes up and locks the
 * monitor object and then issues an interrupt to the EventServicPolling thread,
 * notifying it that a time out occurred. The EventServicePolling at that point
 * verifies its listeners list and resets the persistent search connections that
 * are timing out.
 * 
 * <p>
 * This thread interrupts the EventServicePolling thread only when the sleep
 * period is completed and it can successful obtain a lock for the monitor
 * object. After issuing an interrupt, this thread relinquishes the lock and
 * wait's an indefinite about of time for the EventServicePolling thread to
 * reset its searches and notify. Therefore, it is mandatory for the
 * EventServicePolling thread to issue and interrupt after it has successfully
 * re-established its searches.
 * 
 * <p>
 * Both these threads (EventServicePolling & TimeOut) synchronize by means of a
 * monitor object (_monitor) shared between the 2 threads.
 * </p>
 */
class TimeOut implements Runnable {

    private Thread _serviceThread;

    private long _timeOut;

    private Object _monitor;

    private boolean _exitStatus = false;

    private Debug debug = EventService.debugger;

    private EventServicePolling _serviceInstance;

    TimeOut(EventServicePolling serviceInstance, long timeOut, Object monitor) {
        setServiceInstance(serviceInstance);
        setServiceThread(getServiceInstance().getServiceThread());
        setTimeOutValue(timeOut);
        setMonitor(monitor);
    }

    public void run() {
        debug.message("TimeOut.run() - thread started.");
        while (!shouldExit()) {
            try {

                long updatedTimeOut = getTimeOutValue();
                if (debug.messageEnabled()) {
                    debug.message("TimeOut.run() - Sleeping for "
                            + updatedTimeOut + " milliseconds.");
                }
                // Sleep for the time out period
                Thread.sleep(updatedTimeOut);

                // Need a monitor to avoid a race condition to interrupt the
                // EventServicePolling thread while it is in the middle of
                // processing a request. Once this thread gets a handle to the
                // lock the monitor object, the ES should wait before starting
                // to process until this thread interrupts.
                synchronized (_monitor) {
                    // Notify the service thread that it needs to reset
                    // listeners that might have timed out.
                    if (debug.messageEnabled()) {
                        debug.message("TimeOut.run() - Notifying "
                                + "EventServicePolling thread about timeout");
                    }

                    // Interrupt the EventServicePolling thread to notify
                    // timeout
                    getServiceThread().interrupt();

                    // Wait until the service thread, updated the time out
                    // value and notifies this thread to start.
                    if (debug.messageEnabled()) {
                        debug.message("TimeOut.run() - Waiting for "
                                + "EventServicePolling thread to notify");
                    }
                    _monitor.wait(); // lock on _monitor is now released
                }
            } catch (InterruptedException ie) {
                // Mostly the Service thread has interrupted this thread.
                // Ignore the exception as the state of timeout values and
                // exit status should be been updated the service thread.
                if (debug.messageEnabled()) {
                    debug.message("TimeOut.run() - Received interrupt ", ie);
                }
            }
        } // End while
        debug.error("TimeOut.run() - thread stopped as notified by "
                + "EventServicePolling thread.");
    }

    private void setServiceInstance(EventServicePolling serviceInstance) {
        _serviceInstance = serviceInstance;
    }

    private EventServicePolling getServiceInstance() {
        return _serviceInstance;
    }

    protected synchronized void setTimeOutValue(long timeOut) {
        _timeOut = timeOut;
    }

    private synchronized long getTimeOutValue() {
        return _timeOut;
    }

    private void setServiceThread(Thread serviceThread) {
        _serviceThread = serviceThread;
    }

    private Thread getServiceThread() {
        return _serviceThread;
    }

    protected synchronized void setExitStatus(boolean status) {
        _exitStatus = status;
    }

    private synchronized boolean shouldExit() {
        // Exit when exitStatus is true or if the EventServicePolling thread
        // is dead.
        return (_exitStatus || !getServiceThread().isAlive());
    }

    private void setMonitor(Object monitor) {
        _monitor = monitor;
    }
}
