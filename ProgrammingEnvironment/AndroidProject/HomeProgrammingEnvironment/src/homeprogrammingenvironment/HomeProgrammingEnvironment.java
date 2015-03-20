/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.homeprogrammingenvironment;

import static java.lang.Thread.sleep;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author ryo
 */
public class HomeProgrammingEnvironment extends Application {

    WebSocketManager websocketmana;

    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

    final double screenSizeX = primaryScreenBounds.getWidth();
    final double screenSizeY = primaryScreenBounds.getHeight();

    WorkSpace workSpace = new WorkSpace(screenSizeX, screenSizeY);
    BoxList boxList = new BoxList(screenSizeX / 4, screenSizeY, workSpace);

    Pane root;
    Scene scene;

    @Override
    public void start(Stage primaryStage) {

        // 一番したのパネル
        root = new Pane();
        root.getChildren().add(workSpace.createPane());
        root.getChildren().add(boxList.createPane());

        EventHandler eh = new EventHandler<TouchEvent>() {

            boolean flag = true;

            // 複数タッチは getTouchPoints() の配列で返ってくる。
            @Override
            public void handle(TouchEvent t) {
//                setMousePosition(t.getTouchPoint().getSceneX(), t.getTouchPoint().getSceneY());
                setMultiTouchPositon(t.getTouchPoints());
//                System.out.println("testtttttttttt----------------" + t.getTouchPoints().size());
                Object type = t.getEventType();
                if (type == TouchEvent.TOUCH_RELEASED && flag) {
//                    flag = boxList.addBoxesToBoxList();
                }
            }
        };

        root.setOnTouchPressed(eh);
        root.setOnTouchMoved(eh);
        root.setOnTouchReleased(eh);
        root.setOnTouchStationary(eh);

        // マウスイベントだけど、今は特につかってません。
        EventHandler ehm = new EventHandler<MouseEvent>() {

            boolean flag = true;

            @Override
            public void handle(MouseEvent t) {
                setMousePosition(t.getSceneX(), t.getSceneY());
                Object type = t.getEventType();
            }
        };

        root.setOnMousePressed(ehm);
        root.setOnMouseMoved(ehm);
        root.setOnMouseReleased(ehm);
        root.setOnMouseDragged(ehm);

        // キーイベント
        EventHandler keh = new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                setKeyEvent(t.getCharacter());
            }
        };

        root.setOnKeyTyped(keh);

        scene = new Scene(root, screenSizeX, screenSizeY);

        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.setTitle(
                "HomeProgrammingEnvironment");
        primaryStage.setScene(scene);

        primaryStage.show();

        boxList.addBoxesToBoxList();

    }

    public void setMousePosition(double x, double y) {
        workSpace.setMousePosition(x, y);
        boxList.setMousePosition(x, y);
    }

    // ボックスリストではマルチタッチを受け付けません。
    public void setMultiTouchPositon(List<TouchPoint> list) {
//                System.out.println("testtttttttttt----------------"+list.size());        
        workSpace.setMultiTouchPosition(list);
        boxList.setMultiTouchPosition(list);
    }

    // きーいべんつ！
    public void setKeyEvent(String key) {
        workSpace.setKeyEvent(key);
        boxList.setKeyEvent(key);
    }

    public void actionBoxEvent(String str) {

    }

    @Override
    public void stop() {
        workSpace.stop();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
