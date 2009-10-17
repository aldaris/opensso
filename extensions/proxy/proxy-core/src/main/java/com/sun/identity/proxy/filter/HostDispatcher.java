
package com.sun.identity.proxy.filter;

import com.sun.identity.proxy.handler.Handler;
import com.sun.identity.proxy.handler.HandlerException;
import com.sun.identity.proxy.http.Exchange;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

/**
 * Dispatches to handlers based on the content of request header. Regular
 * expression patterns are mapped to handlers to pass matching requests to.
 * <p>
 * This class can be useful when more than one remote server uses the same
 * cookie, and that cookie needs to be managed by a filter rather than being
 * relayed to the remote client. In this case, two filter chains would be
 * configured to terminate to the same cookie filter and client handler.
 * <p>
 * Regular expressions are matched against the <tt>Host</tt> header in the
 * incoming request. Per RFC 2616, this header contains the host name and
 * optional port number. Prior to matching, this class converts the host header
 * value to lower case. If no host header exists in the request, then an empty
 * string value is used for matching.
 * <p>
 * Regular expression patterns are evaluated in the order they are added to the
 * <tt>handlers</tt> map. If no matching handler is found, a
 * {@link HandlerException} will be thrown. Therefore, it is advisable to add a
 * catch-all handler to dispatch to a default handler.
 * <p>
 * Example:
 * <pre>
 * HostDispatcher dispatcher = new HostDispatcher();
 * dispatcher.handlers.put(Pattern.compile("(www\\.)?example1\\.com(:80)?"), chain1);
 * dispatcher.handlers.put(Pattern.compile("example2\\.com(:80)?"), chain2);
 * dispatcher.handlers.put(Pattern.compile(".*"), errorHandler);
 * </pre>
 * In this example, the first expression allows a www. prefix; the first and
 * second expressions both allow an optional port number. If the host doesn't
 * match the first two expressions, the exchange is dispatched to the error
 * chain.
 * <p>
 * Note: It's generally better to use the container's virtual host function
 * rather than using this class to dipatch to different handlers. If one filter
 * in one chain were to misbehave, it could affect all handlers exposed through
 * a single proxy servlet, whereas there is a better chance that the container
 * can encapsulate the problem of the misbehaving servlet, leaving all other
 * servlets operational.
 *
 * @author Paul C. Bryan
 */
public class HostDispatcher implements Handler
{
    /** Maps regular expression patterns to handlers to dispatch to. */
    public final LinkedHashMap<Pattern, Handler> handlers = new LinkedHashMap<Pattern, Handler>();

    /**
     * Creates a new host dispatcher.
     */
    public HostDispatcher() {
    }

    /**
     * Handles an exchange by dispatching to the handler mapped to the
     * regular expression that first matches the request's host header.
     */
    @Override
    public void handle(Exchange exchange) throws HandlerException, IOException {
        String host = exchange.request.headers.first("Host");
        if (host == null) {
            host = "";
        }
        host = host.toLowerCase();
        for (Pattern p : handlers.keySet()) {
            if (p.matcher(host).matches()) {
                handlers.get(p).handle(exchange);
                return;
            }
        }
        throw new HandlerException("no matching handler found for host");
    }
}

