/*
 * Settings.fx
 *
 * Created on Mar 26, 2009, 3:37:42 PM
 */

package c1democlient;

import c1democlient.Constants;
import c1democlient.Home;
import c1democlient.Main;
import c1democlient.ui.TextButton;
import javafx.ext.swing.SwingButton;
import javafx.scene.CustomNode;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author pat
 */

def titleFont: Font = Constants.arialBold16;
def subtitleFont: Font = Constants.arialRegular12;

public function doSettings(): Void {
    if ( Main.account == null ) {
        ErrorMessage.doError("Error loading account settings", Home.doHome );
    } else {
        Main.goto(Settings{});
    }
}

public class Settings extends CustomNode {
    public override function create():Node {
        id = "Settings";

        VBox {
            content: [
                Text {
                    font: Constants.arialRegular12
                    content: Main.accountPhone
                }
                VBox {
                    content: [
                        Text {
                            font: titleFont
                            content: "Billing Address"
                        }
                        Text {
                            font: subtitleFont
                            content: "{Main.account.addressLine1}\n"
                            "{Main.account.addressCity}, {Main.account.addressState} {Main.account.addressZip}"
                            wrappingWidth: 210
                        }
                    ]
                }
                SwingButton {
                    text: "Change Billing Address"
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
