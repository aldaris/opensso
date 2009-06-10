/*
 * DemoPane.fx
 *
 * Created on May 15, 2009, 3:44:23 PM
 */

package c1democlient;

import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextOrigin;
import c1democlient.Constants;

/**
 * @author pat
 */

public class DemoPane extends CustomNode {
    public var currentPane: Node;
    public override function create():Node {
        Group {
            content: [
                VBox {
                    translateX: 15
                    translateY: 20
                    spacing: 10
                    content: bind [
                        Text {
                            font: Constants.arialBoldItalic18
                            fill: Constants.sunBlue
                            content: currentPane.id;
                            textOrigin: TextOrigin.TOP
                        },
                        currentPane
                    ]
                }
            ]
        };
    }
}
