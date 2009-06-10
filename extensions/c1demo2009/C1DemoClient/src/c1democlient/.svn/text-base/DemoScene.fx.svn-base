/*
 * DemoScene.fx
 *
 * Created on May 15, 2009, 2:28:53 PM
 */

package c1democlient;

import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import c1democlient.Constants;
import c1democlient.Main;

/**
 * @author pat
 */

def borderWidth = 7;

public class DemoScene extends Scene {
    public var demoPane: DemoPane;
    init {
        content = [
            Rectangle {
                x: borderWidth
                y: borderWidth
                width: bind (Main.stage.scene.width - (borderWidth * 2.0))
                height: bind (Main.stage.scene.height - (borderWidth * 2.0))
                arcHeight: 20
                arcWidth: 20
                fill: Color.TRANSPARENT
                stroke: Constants.sunBlue
                strokeWidth: 2.0
            },
            demoPane
        ];
        fill = Color.WHITE;
    }
}
