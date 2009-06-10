/*
 * TextButton.fx
 *
 * Created on Mar 26, 2009, 3:55:03 PM
 */

package c1democlient.ui;

import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextOrigin;

import c1democlient.Constants;

/**
 * @author pat
 */

public class TextButton extends CustomNode {
    public var color: Color = Color.BLACK;
    public var highlightColor: Color = Constants.sunBlue;
    public var font: Font;
    public var content: String;

    public var action: function(): Void;

    var rect: Rectangle;
    var text: Text;

    public override var onMouseReleased = function(e: MouseEvent) {
        if ( action != null ) {
            action();
        }
    }

    public override var onMouseEntered = function(e: MouseEvent) {
        text.fill = highlightColor;
        text.underline = true;
    }

    public override var onMouseExited = function(e: MouseEvent) {
        text.fill = color;
        text.underline = false;
    }

    public override function create():Node {
        Group {
            content: [
                rect = Rectangle
                {
                    height: bind text.layoutBounds.height
                    width: bind text.layoutBounds.width
                    opacity: 0.0
                },
                text = Text
                {
                    fill: color;
                    font: font;
                    content: content;
                    underline: false;
                    textOrigin: TextOrigin.TOP;
                }
            ]
        };
    }
}
