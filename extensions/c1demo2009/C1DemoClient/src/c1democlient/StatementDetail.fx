/*
 * StatementDetail.fx
 *
 * Created on Mar 26, 2009, 3:37:42 PM
 */

package c1democlient;

import c1democlient.Constants;
import c1democlient.Home;
import c1democlient.Main;
import c1democlient.ui.TextButton;
import javafx.scene.CustomNode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author pat
 */

def titleFont: Font = Constants.arialBold16;
def subtitleFont: Font = Constants.arialRegular12;

public function doStatementDetail( phoneNumberTo: String,
    callDurationSecs: Number, callTime: String, back: function(): Void ): Void {
    Main.goto(StatementDetail{
        phoneNumberTo: phoneNumberTo
        callDurationSecs: callDurationSecs
        callTime: callTime
        back: back
        });
}

public class StatementDetail extends CustomNode {
    public var phoneNumberTo: String;
    public var callDurationSecs: Number;
    public var callTime: String;
    public var back: function(): Void;
    public override function create():Node {
        id = "Call";

        VBox {
            content: [
                Text {
                    font: Constants.arialRegular12
                    content: "Phone number: {Main.phoneNumber}";
                }
                HBox {
                    content: [
                        Text {
                            font: Constants.arialBold12
                            content: "Date/Time:"
                        }
                        Text {
                            font: Constants.arialRegular12
                            content: callTime
                        }
                    ]
                }
                HBox {
                    content: [
                        Text {
                            font: Constants.arialBold12
                            content: "Duration:"
                        }
                        Text {
                            font: Constants.arialRegular12
                            content: "{callDurationSecs} seconds"
                        }
                    ]
                }
                HBox {
                    content: [
                        Text {
                            font: Constants.arialBold12
                            content: "To:"
                        }
                        Text {
                            font: Constants.arialRegular12
                            content: phoneNumberTo
                        }
                    ]
                }
                TextButton {
                    font: titleFont
                    content: "Back"
                    action: back
                }
            ]
            spacing: 12
        }
    }
}

