/*
 * StatementScene.fx
 *
 * Created on May 18, 2009, 8:06:11 PM
 */

package c1democlient;

import c1democlient.Constants;
import c1democlient.Main;
import c1democlient.model.CallLog;
import c1democlient.model.Phone;
import c1democlient.oauth.OauthRequest;
import c1democlient.parser.CallLogsPullParser;
import c1democlient.ui.TableNode;
import c1democlient.ui.TextButton;
import java.lang.Exception;
import javafx.data.Pair;
import javafx.io.http.HttpRequest;
import javafx.scene.CustomNode;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextOrigin;

/**
 * @author pat
 */

public function doPhoneStatement(phone:Phone, back: function(): Void): Void {
    println("Call CallLog Restful Web Service via OAuth...");

    var params: Pair[];
    insert Pair {
        name: "expandLevel";
        value: "2";
    } into params;

    var oauthRequest: OauthRequest = OauthRequest {
        consumerKey: Main.consumerKey;
        consumerSecret: Main.consumerSecret;
        token: Main.token;
        tokenSecret: Main.tokenSecret;
        tokenExpires: Main.tokenExpires;
        signatureMethod: OauthRequest.HMACSHA1;
        urlParameters: params;

        request: HttpRequest {
            location: "http://{Constants.host}:{Constants.port}/{Constants.contextRoot}/resources/phones/{phone.phoneNumber}/callLogCollection/"
            method: HttpRequest.GET

            onException: function(exception: Exception) {
                println("Error: {exception}");
            }
            onResponseCode: function(responseCode:Integer) {
                println("{responseCode} from {oauthRequest.request.location}");
                if (responseCode != 200) {
                    ErrorMessage.doError("Error loading phone statement", back );
                }
            }
            onInput: function(input: java.io.InputStream) {
                var tableContent: Node[];
                var calls: CallLog[];

                try {
                    var parser = CallLogsPullParser {
                        onDone: function( data:CallLog[] ) {
                            calls = data;
                        }
                    };
                    parser.parse(input);
                    for ( call in calls ) {
                        // Cook the call time a little - replace 'T' with a space
                        var callTimeStr = call.callTime.replaceAll("T", " ");

                        // Now trim off the seconds and timezone
                        var lastColon = callTimeStr.lastIndexOf(":");
                        callTimeStr = callTimeStr.substring(0, lastColon);
                        lastColon = callTimeStr.lastIndexOf(":");
                        callTimeStr = callTimeStr.substring(0, lastColon);

                        var textBox = TextButton {
                            font: Constants.arialBold12
                            content: call.phoneNumberTo
                            action: function(): Void {
                                StatementDetail.doStatementDetail( call.phoneNumberTo,
                                    call.callDurationSecs, callTimeStr, function(): Void {
                                        doPhoneStatement(phone, back);
                                    } 
                                );
                            }
                        }
                        var callTime = Text {
                            content: callTimeStr;
                            textOrigin: TextOrigin.TOP;
                        }

                        insert textBox into tableContent;
                        insert callTime into tableContent;
                    }

                    Main.goto(PhoneStatement {
                        phone: phone
                        back: back
                        tableContent: tableContent
                    });
                } finally {
                    input.close();
                }
            }
        }
    }

    oauthRequest.start();
}

public class PhoneStatement extends CustomNode {
    public var phone: Phone;
    public var tableContent: Node[];
    public var back: function(): Void;

    public override function create():Node {
        id = "Calls for {phone.phoneNumber}";
        VBox {
            content: [
                Text {
                    font: Constants.arialRegular12
                    content: Main.accountPhone
                }
                TableNode {
                    height: 200
                    rowHeight: 25
                    rowSpacing: 0
                    columnWidths: [85, 130]
                    tableFill: Color.WHITE
                    evenRowFill: Color.WHITE
                    oddRowFill: Color.LIGHTBLUE
                    selectedRowFill: Color.WHITE
                    selectedIndex: -1
                    content: tableContent
                }
                TextButton {
                    font: Constants.arialBold16
                    content: "Back"
                    action: back
                }
            ]
            spacing: 10
        }
    }
}
