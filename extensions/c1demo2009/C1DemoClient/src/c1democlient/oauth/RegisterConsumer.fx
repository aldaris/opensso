/*
 * RegisterConsumer.fx
 *
 * Created on May 27, 2009, 7:25:52 PM
 */

package c1democlient.oauth;
import java.lang.Exception;
import java.lang.StringBuffer;
import javafx.io.http.HttpRequest;

/**
 * @author pat
 */

public function registerConsumer(consumerKey: String, consumerSecret: String, consumerName: String, uri: String, action: function(): Void): Void {
    println("registerConsumer {consumerKey} {consumerSecret} {consumerName}");

    var param: String = "cons_key={consumerKey}&secret={consumerSecret}&name={consumerName}&signature_method=HMAC-SHA1";

    var request: HttpRequest = HttpRequest {
        location: uri
        method: HttpRequest.POST;

        onException: function(exception: Exception) {
            println("Error: {exception}");
        }
        onResponseCode: function(responseCode:Integer) {
            println("{responseCode} from {request.location}");
        }
        onOutput: function(os: java.io.OutputStream) {
            try {
                println("Writing {param} to {request.location}");
                os.write(param.getBytes());
            } finally {
                os.close();
            }
        }
        onInput: function(is: java.io.InputStream) {
            var i: Integer;
            var sb = new StringBuffer();
            while (

            (i = is.read()) != - 1) {
                sb.append(i as Character);
            }
            println("registerConsumer response: {sb}");
            is.close();
            if ( action != null ) {
                action();
            }
        }
    }

    request.setHeader("Content-Type", "application/x-www-form-urlencoded");
    request.setHeader("Content-Length", "{param.length()}");

    request.start();
}

