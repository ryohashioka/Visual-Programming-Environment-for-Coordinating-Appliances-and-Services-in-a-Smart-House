/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.homeprogrammingenvironment;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

/**
 *
 * @author ryo
 */
public class MyLine extends Line {

    final int strokeWidth = 3;
    private boolean selectFlag = false;
    final String colorCode = "#7A7A7A";
    final String colorCode_Selected = "#2933FA";

    MyLine(double x1, double y1, double x2, double y2) {
//        line = new Line(x1, y1, x2, y2);
        setStartX(x1);
        setStartY(y1);
        setEndX(x2);
        setEndY(y2);
//        line.setStyle("-fx-fill: " + colorCode + ";"
//                + "-fx-stroke-width: 3;");
//        setStroke(Color.BLUE);
//        setFill(Color.web(colorCode_Selected));
        setStroke(Color.web(colorCode));
        setStrokeWidth(strokeWidth);
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                System.out.println("line ------------------ mouse clicked! ");
                selecter();
            }

        });
    }

    public void selecter() {
        if (selectFlag) {
            System.out.println("------------------ true ");
            selectFlag = false;
//            line.setStyle("-fx-fill: " + colorCode_Selected + ";"
//            + "-fx-stroke-width: 3;");
            setStroke(Color.web(colorCode));
            setStrokeWidth(strokeWidth);
        } else {
            System.out.println("------------------ false ");
            selectFlag = true;
//            line.setStyle("-fx-fill: " + colorCode + ";"
//            + "-fx-stroke-width: 3;");
            setStroke(Color.web(colorCode_Selected));
            setStrokeWidth(strokeWidth);
        }
    }

    public boolean getSelectFlag() {
        return selectFlag;
    }

    public void remove() {
        try {
            this.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(MyLine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
