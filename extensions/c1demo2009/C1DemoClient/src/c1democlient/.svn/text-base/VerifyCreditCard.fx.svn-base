/*
 * CreditCardScene.fx
 *
 * Created on May 15, 2009, 2:18:32 PM
 */

package c1democlient;

import c1democlient.Constants;
import c1democlient.Main;
import c1democlient.parser.OtpPullParser;
import c1democlient.ui.SwingPasswordField;
import java.lang.Exception;
import javafx.ext.swing.SwingButton;
import javafx.io.http.HttpRequest;
import javafx.scene.CustomNode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextOrigin;

/**
 * @author pat
 */

public function doCreditCard(): Void {
    Main.goto(VerifyCreditCard{});
}

public class VerifyCreditCard extends CustomNode {
    var ccPromptText: Text;
    var ccTextBox: SwingPasswordField;
    var cvv2PromptText: Text;
    var cvv2TextBox: SwingPasswordField;

    public override function create():Node {
        id = "Authentication";
        VBox {
            content: [
                HBox {
                    content: [
                        Text {
                            font: Constants.arialRegular12
                            fill: Color.BLACK
                            content: "Your phone number:"
                        }
                        Text {
                            font: Constants.arialBold12
                            fill: Color.BLACK
                            content: bind Main.phoneNumber;
                        }
                    ]
                },
                Text {
                    font: Constants.arialRegular12
                    fill: Color.BLACK
                    wrappingWidth: 210
                    content: "You have not yet set a password. To verify your "
                    "identity, please enter the last 4 digits and the CVV2 number* "
                    "of the credit card with which you bought your phone:"
                }
                HBox {
                    content: [
                        ccPromptText = Text {
                            font: Constants.arialRegular12
                            fill: Color.BLACK
                            content: "Last 4 of Credit Card:"
                            textOrigin: TextOrigin.TOP
                            translateY: bind (ccTextBox.boundsInLocal.height - ccPromptText.boundsInLocal.height) / 2;
                        },
                        ccTextBox = SwingPasswordField {
                            columns: 5
                        }
                    ]
                    spacing: 5
                }
                HBox {
                    content: [
                        cvv2PromptText = Text {
                            font: Constants.arialRegular12
                            fill: Color.BLACK
                            content: "CVV2 Number:"
                            textOrigin: TextOrigin.TOP
                            translateY: bind (cvv2TextBox.boundsInLocal.height - cvv2PromptText.boundsInLocal.height) / 2;
                        },
                        cvv2TextBox = SwingPasswordField {
                            columns: 5
                            translateX: 33 // NASTY - but it will do for now
                            action: submitCreditCard;
                        }
                    ]
                    spacing: 5
                }
                SwingButton {
                    text: "Proceed"
                    action: submitCreditCard;
                }
                Text {
                    font: Constants.arialRegular10
                    fill: Color.BLACK
                    wrappingWidth: 210
                    content: "*The CVV2 number is 3 digits found on "
                        "the back of most credit cards. For "
                        "American Express cards, it is the 4 digits "
                        "printed above the card number on the "
                        "front of the card."
                }
            ]
            spacing: 5
        }
    }

    function submitCreditCard(): Void {
        println("submitCreditCard {ccTextBox.text} {cvv2TextBox.text}");

        var param: String = "action=auth2&content=<auth2><answer>{ccTextBox.text}</answer><answer>{cvv2TextBox.text}</answer></auth2>";

        var request: HttpRequest = HttpRequest {
            location: "http://{Constants.host}:{Constants.port}/{Constants.contextRoot}/resources/challenges/{Main.phoneNumber}/"
            method: HttpRequest.POST;

            onException: function(exception: Exception) {
                println("Error: {exception}");
            }
            onResponseCode: function(responseCode:Integer) {
                println("{responseCode} from {request.location}");
                if (responseCode != 200) {
                    ErrorMessage.doError("Error verifying card details", doCreditCard );
                }
            }
            onOutput: function(output: java.io.OutputStream) {
                try {
                    println("Writing {param} to {request.location}");
                    output.write(param.getBytes());
                } finally {
                    output.close();
                }
            }
            onInput: function(input: java.io.InputStream) {
                try {
                    var parser = OtpPullParser {
                        onDone: function( data:String ) {
                            println("otp = {data}");
                            SetQuestion.doSetQuestion(data);
                        }
                    };
                    parser.parse(input);
                } finally {
                    input.close();
                }
            }
        }

        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setHeader("Content-Length", "{param.length()}");

        request.start();
    }
}
