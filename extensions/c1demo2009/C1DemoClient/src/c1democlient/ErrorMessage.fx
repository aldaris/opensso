/*
 * HomeScene.fx
 *
 * Created on Mar 26, 2009, 3:37:42 PM
 */

package c1democlient;

import c1democlient.Constants;
import c1democlient.ui.TextButton;
import javafx.scene.CustomNode;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.text.Text;

/**
 * @author pat
 */

public function doError(message: String, back: function(): Void ): Void {
    Main.goto(ErrorMessage {
        message: message
        back: back
    });
}

public class ErrorMessage extends CustomNode {
    public var message: String;
    public var back: function(): Void;

    public override function create():Node {
        id = "Error";
        VBox {
            content: [
                Text {
                    font: Constants.arialRegular12
                    content: message
                    wrappingWidth: 210
                }
                TextButton {
                    font: Constants.arialBold16
                    content: "Back"
                    action: back
                }
            ]
            spacing: 12
        }
    }
}
