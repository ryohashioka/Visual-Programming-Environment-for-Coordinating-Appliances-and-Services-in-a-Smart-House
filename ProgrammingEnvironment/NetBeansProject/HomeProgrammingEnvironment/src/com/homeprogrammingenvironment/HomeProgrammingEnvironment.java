/*
 * Copyright (c) 2015 Ryo Hashioka
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
 * はじめに呼ばれるクラス
 * プログラムフィールドやボックスリスト、といった初期画面のパネルの描画処理を行なう。
 * なお、このプログラムではマウスでの操作はできません。
 * タッチ可能なデバイスでお使い下さい。
 * 
 */
public class HomeProgrammingEnvironment extends Application {

    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

    final double screenSizeX = primaryScreenBounds.getWidth();
    final double screenSizeY = primaryScreenBounds.getHeight();

    WorkSpace workSpace = new WorkSpace(screenSizeX, screenSizeY);
    BoxList boxList = new BoxList(screenSizeX / 4, screenSizeY, workSpace);

    Pane root;
    Scene scene;

    @Override
    public void start(Stage primaryStage) {

        // 全てのモトとなるパネル
        root = new Pane();
        root.getChildren().add(workSpace.createPane()); // プログラムフィールドの追加
        root.getChildren().add(boxList.createPane()); // オブジェクトリストの追加

        // タッチイベント
        EventHandler eh = new EventHandler<TouchEvent>() {

            boolean flag = true;

            // 複数タッチは getTouchPoints() の配列で返ってくる。
            @Override
            public void handle(TouchEvent t) {
                setMultiTouchPositon(t.getTouchPoints());
                Object type = t.getEventType();
            }
        };

        root.setOnTouchPressed(eh);
        root.setOnTouchMoved(eh);
        root.setOnTouchReleased(eh);
        root.setOnTouchStationary(eh);

        // デバッグ用マウスイベント（使えません）
        EventHandler ehm = new EventHandler<MouseEvent>() {
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

        // キーイベント（デバッグ用）
        EventHandler keh = new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                setKeyEvent(t.getCharacter());
            }
        };

        root.setOnKeyTyped(keh);

        // 
        scene = new Scene(root, screenSizeX, screenSizeY);

        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.setTitle(
                "HomeProgrammingEnvironment");
        primaryStage.setScene(scene);

        primaryStage.show();

        // すべてを描画後、ボックスリストにオブジェクトを配置していく
        boxList.addBoxesToBoxList();

    }

    // デバッグ用のマウスポジションをそれぞれのフィールドに渡すメソッド（使っていません）
    public void setMousePosition(double x, double y) {
        workSpace.setMousePosition(x, y);
        boxList.setMousePosition(x, y);
    }

    // タッチポイントをそれぞれのフィールドに渡すメソッド
    // ボックスリストではマルチタッチを受け付けていません。
    public void setMultiTouchPositon(List<TouchPoint> list) {
        workSpace.setMultiTouchPosition(list);
        boxList.setMultiTouchPosition(list);
    }

    // デバッグ用のキーイベント（ほとんど使っていません。）
    public void setKeyEvent(String key) {
        workSpace.setKeyEvent(key);
        boxList.setKeyEvent(key);
    }

    // 終了処理
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
