/*
 * HomeScene.fx
 *
 * Created on Mar 26, 2009, 3:37:42 PM
 */

package c1democlient;

import c1democlient.Constants;
import c1democlient.Home;
import c1democlient.Login;
import c1democlient.Main;
import c1democlient.Settings;
import c1democlient.ui.TextButton;
import javafx.scene.CustomNode;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author pat
 */

public function doHome(): Void {
    Main.goto(Home{});
}

def titleFont: Font = Constants.arialBold16;
def subtitleFont: Font = Constants.arialRegular12;

public class Home extends CustomNode {
    public override function create():Node {
        id = "MyAccount Home";

        var node: Node;

        if ( Main.account != null ) {
        //if ( true ) {
            node = VBox {
                content: [
                    Text {
                        font: Constants.arialRegular12
                        content: Main.accountPhone
                    }
                    VBox {
                        content: [
                            TextButton {
                                font: titleFont
                                content: "Statement"
                                action: Statement.doStatement
                            }
                            Text {
                                font: subtitleFont
                                content: "See your bills, call logs, etc"
                                wrappingWidth: 210
                            }
                        ]
                    }
                    VBox {
                        content: [
                            TextButton {
                                font: titleFont
                                content: "Account Settings"
                                action: Settings.doSettings
                            }
                            Text {
                                font: subtitleFont
                                content: "Control your account"
                                wrappingWidth: 210
                            }
                        ]
                    }
                    VBox {
                        content: [
                            TextButton {
                                font: titleFont
                                content: "Family Settings"
                                action: FamilySettings.doFamilySettings
                            }
                            Text {
                                font: subtitleFont
                                content: "Control all of the numbers for this account"
                                wrappingWidth: 210
                            }
                        ]
                    }
                    VBox {
                        content: [
                            TextButton {
                                font: titleFont
                                content: "Notifications"
                                action: function(): Void {
                                    Notifications.doNotifications(Main.phone, doHome)
                                };
                            }
                            Text {
                                font: subtitleFont
                                content: "Keep yourself up to date"
                                wrappingWidth: 210
                            }
                        ]
                    }
                    TextButton {
                        font: titleFont
                        content: "Logout"
                        action: Login.doLogin
                    }
                ]
                spacing: 12
            }
        } else {
            node = VBox {
                content: [
                    Text {
                        font: Constants.arialRegular12
                        content: Main.accountPhone
                    }
                    VBox {
                        content: [
                            TextButton {
                                font: titleFont
                                content: "Phone Statement"
                                action: function(): Void {
                                    PhoneStatement.doPhoneStatement(Main.phone, doHome);
                                }
                            }
                            Text {
                                font: subtitleFont
                                content: "See your bills, call logs, etc"
                                wrappingWidth: 210
                            }
                        ]
                    }
                    VBox {
                        content: [
                            TextButton {
                                font: titleFont
                                content: "Phone Settings"
                                action: function(): Void {
                                    PhoneSettings.doPhoneSettings(Main.phone, doHome);
                                }
                            }
                            Text {
                                font: subtitleFont
                                content: "Control your phone"
                                wrappingWidth: 210
                            }
                        ]
                    }
                    VBox {
                        content: [
                            TextButton {
                                font: titleFont
                                content: "Notifications"
                                action: function(): Void {
                                    Notifications.doNotifications(Main.phone, doHome)
                                };
                            }
                            Text {
                                font: subtitleFont
                                content: "Keep yourself up to date"
                                wrappingWidth: 210
                            }
                        ]
                    }
                    TextButton {
                        font: titleFont
                        content: "Logout"
                        action: Login.doLogin
                    }
                ]
                spacing: 12
            }
        }

        return node;
    }
}

