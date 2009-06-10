/*
 * OauthTest.fx
 *
 * Created on May 27, 2009, 7:34:36 PM
 */

package c1democlient.oauth;

import c1democlient.Main;
import java.io.InputStream;
import java.lang.StringBuffer;
import javafx.data.Pair;
import javafx.io.http.HttpRequest;

/**
 * @author pat
 */

public function test(): Void {
    registerConsumer(testRequest);
}

/*
 * Do this only once (ever!) - don't do it every time the app starts!
 */
public function registerConsumer(action: function(): Void ): Void {
    RegisterConsumer.registerConsumer(Main.consumerKey,
    Main.consumerSecret,
    Main.consumerName,
    Main.oauthRegistrationUrl,
    action);
}

public function testRequest(): Void {
    var request1: OauthRequest = OauthRequest {
        consumerKey: Main.consumerKey;
        consumerSecret: Main.consumerSecret;
        requestUrl: Main.oauthRequestUrl;
        authUrl: Main.oauthAuthUrl;
        accessUrl: Main.oauthAccessUrl;
        username: "1234567890";
        password: "qqq";
        signatureMethod: OauthRequest.HMACSHA1;

        request: HttpRequest {
            location: "http://localhost:8080/C1DemoServer/resources/phones/1112223333/";
            onInput: function(is: InputStream) {
                var i: Integer;
                var sb = new StringBuffer();
                while (

                (i = is.read()) != - 1) {
                    sb.append(i as Character);
                }
                println("MYRESPONSE: {sb}");
                is.close();
            }

            onResponseCode: function(code: Integer) {
                println("CODE:{code}");
            }

            onResponseMessage: function(msg: String) {
                println("MESG:{msg}");
            }

            onDone: function() {
                println("DONE");
            }
        }
    }
    request1.start();
}

