/*
 * NotificationDetail.fx
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

public function doNotificationDetail(notificationTime: String, messageText: String): Void {
    Main.goto(NotificationDetail{
        notificationTime: notificationTime
        messageText: messageText
        });
}

public class NotificationDetail extends CustomNode {
    public var notificationTime: String;
    public var messageText: String;
    public override function create():Node {
        id = "Notification";

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
                            content: notificationTime
                            wrappingWidth: 210
                        }
                    ]
                }
                VBox {
                    content: [
                        Text {
                            font: Constants.arialBold12
                            content: "Content:"
                        }
                        Text {
                            font: Constants.arialRegular12
                            content: messageText
                            wrappingWidth: 210
                        }
                    ]
                }
                TextButton {
                    font: titleFont
                    content: "Back"
                    action: Home.doHome
                }
            ]
            spacing: 12
        }
    }
}

