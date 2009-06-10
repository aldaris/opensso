/*
 * Constants.fx
 *
 * Created on Mar 26, 2009, 3:44:05 PM
 */

package c1democlient;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;

/**
 * @author pat
 */

public def sunBlue = Color.rgb(83,130,161);

public def arialRegular10: Font = Font.font("Arial", FontWeight.REGULAR, 10);
public def arialRegular12: Font = Font.font("Arial", FontWeight.REGULAR, 12);
public def arialBold12: Font = Font.font("Arial", FontWeight.BOLD, 12);
public def arialBold16: Font = Font.font("Arial", FontWeight.BOLD, 16);
public def arialBold20: Font = Font.font("Arial", FontWeight.BOLD, 20);
public def arialBoldItalic20: Font = Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 20);
public def arialBoldItalic18: Font = Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 18);

public def host = "localhost";
public def port = "8080";
public def contextRoot = "C1DemoServer";
