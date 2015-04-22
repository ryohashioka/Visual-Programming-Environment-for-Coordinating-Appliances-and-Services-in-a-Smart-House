/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package com.homeprogrammingenvironment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 *
 * @author ryo 
 * プログラムフィールドやボックスリストで使うボックス。
 * ドラッグ&ドロップ、ピンチイン／ピンチアウトの動作などの処理を書いている。
 * それぞれのオブジェクトを描画するための情報を保持している。
 *
 */
public class Box {

    // どこのオブジェクトかも保持している（boxlistかworkspaceか）
    ProgrammingEnvironment pge;

    // BoxのモトとなるJavaFXのBox
    final VBox vbox = new VBox(10);

    // boxの設定を行うためのbox
    BoxToSetUp setBox;

    // box の情報
    double x = 50;
    double y = 200;
    String name = null;
    String boxMode = "input"; // INPUT OUTPUT CONDITION を区別するための変数
    String eventName = "TOUCH";
    String imagePath = ""; // iconの保存場所
    String category = ""; // カテゴリ名
    ImageView image = new ImageView(); // icon
    String colorCode = "#515151"; // ボックスの色
    String itemStr = ""; // setboxにて設定されている値
    int touchID = 0;
    String programID = ""; 
    int boxID = 0;
    boolean moveFlag = true;

    // icon が置かれている場所
    final String filePath = "/sdcard/HomeProgrammingEnvironment/data/";

    double boxSizeX = 120;
    double boxSizeY = 120; // 最初はアイコンと名前が表示される。（最終的には保存のときにXMLファイルでオブジェクト毎に管理）
    final double initBoxSizeX = 120;
    final double initBoxSizeY = 40; // 一番小さい名前だけの時に使う
    final double imgSizeX = 85;
    final double imgSizeY = 85;

    // 接続されている、接続している線の管理
    ArrayList<MyLine> inLines = new ArrayList<MyLine>();
    ArrayList<MyLine> outLines = new ArrayList<MyLine>();
    // 次の箱、前の箱も持っている
    ArrayList<Box> nextBox = new ArrayList<Box>();
    ArrayList<Box> frontBox = new ArrayList<Box>();

    // ボックスの生成を行なう
    Box(String name, double x, double y, String boxMode,
            String guiName, String[] item, String option1, String option2, String[] places,
            String category, ProgrammingEnvironment pge) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.pge = pge;
        this.boxMode = boxMode;
        this.imagePath = "";
        this.category = category;
//        this.guiName = guiName;
//        this.item = item;
        this.init();
        setBox = new BoxToSetUp(guiName, item, places, "", "", option1, option2, this);
    }

    Box(String name, double x, double y, String boxMode,
            String guiName, String[] item, String option1, String option2, String[] places,
            String imgPath, String category, ProgrammingEnvironment pge) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.imagePath = imgPath;
        this.category = category;
        File file = new File(imgPath);
//        System.out.println(" Box : file : " + file.getName());
        try {
            if (!file.getName().equals("data")) {
                this.image = new ImageView(new Image(new FileInputStream(filePath + file)));
                this.image.setFitWidth(imgSizeX);
                this.image.setFitHeight(imgSizeY);
            }
        } catch (FileNotFoundException ex) {
        }
        this.pge = pge;
        this.boxMode = boxMode;
//        this.guiName = guiName;
//        this.item = item;
//        System.out.println("----------------------------- in box age create box");
        setBox = new BoxToSetUp(guiName, item, places, "", "", option1, option2, this);
        this.init();
    }

    // プログラムロードの時に使ってる。
    Box(String name, double x, double y, String boxMode,
            String guiName, String[] item, String option1, String option2, String[] places, String place,
            String itemVal, String imgPath, String category, ProgrammingEnvironment pge) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.imagePath = imgPath;
        this.category = category;
        File file = new File(imgPath);
//        System.out.println(" Box : file : " + file.getName());
        try {
            if (!file.getName().equals("data")) {
                this.image = new ImageView(new Image(new FileInputStream(filePath + file)));
                this.image.setFitWidth(imgSizeX);
                this.image.setFitHeight(imgSizeY);
            }
        } catch (FileNotFoundException ex) {
        }
        this.pge = pge;
        this.boxMode = boxMode;
//        this.guiName = guiName;
//        this.item = item;
//        System.out.println("----------------------------- in box age create box");
        setBox = new BoxToSetUp(guiName, item, places, itemVal, place, option1, option2, this);
        this.setItem(itemVal);
        this.init();
    }

    public void init() {
//        System.out.println(boxMode);
        if (boxMode.equals("input")) {
            colorCode = "#FCDA85"; // オレンジ
        } else if (boxMode.equals("output")) {
            colorCode = "#C0DFFF"; // 青
        } else if (boxMode.equals("condition")) { // （使っていない）
            colorCode = "#C5F9CD"; // 緑
        }
        
        // 箱の初期値をセット
        setBoxSize(boxSizeX, boxSizeY);
        // 箱の生成
        create();
    }

    // とりあえず、ボックスをセットします。
    public void create() {
        vbox.setAlignment(Pos.TOP_CENTER);
        Label label = new Label();
        label.setAlignment(Pos.BASELINE_CENTER);
        label.setText(name);
        label.setGraphic(image);
        label.setContentDisplay(ContentDisplay.TOP);
        vbox.setStyle("-fx-background-color: " + colorCode + ";"
                + "-fx-text-fill: #515151;"
                + "-fx-border-radius: 20;"
                + "-fx-background-radius: 20;"
                + "-fx-border-color: #B3B3B3;"
                + "-fx-border-width: 2;"
                + "-fx-padding: 5;");

        vbox.setLayoutX(x);
        vbox.setLayoutY(y);

        // ボックスになにか起こったら
        EventHandler touchEvent = new EventHandler<TouchEvent>() {
            // タップ処理のためのタイマー処理
            Timer timer = new Timer();
            boolean timerFlag = false;

            class Ti extends TimerTask {

                @Override
                public void run() {
                    timerFlag = true;
                }
            }

            @Override
            public void handle(TouchEvent t) {
                
                // タップ判定も作る
                // タッチされて、一定時間以内に、動きも少なかったら、boxEventにタップと送る。
                System.out.println("Box Touched : ------ " + t.getTouchPoint().getId());
                setTouchID(t.getTouchPoint().getId());
                Object type = t.getEventType();
                if (type == TouchEvent.TOUCH_PRESSED) { // touch pressed
                    // タイマーのセット
                    timerFlag = false;
                    timer.schedule(new Ti(), 100);
                    setEventName("PRESSED");
                    boxEvent(vbox.getLayoutX(), vbox.getLayoutY(), t.getTouchPoint().getId());
                } else if (type == TouchEvent.TOUCH_RELEASED) { // mouse released
                    // 0.5秒以内に離されたらタップ判定
                    if (!timerFlag) {
                        setEventName("TAPPED");
                        boxEvent(vbox.getLayoutX(), vbox.getLayoutY(), t.getTouchPoint().getId());
                    }
                    setEventName("RELEASED");
                    setTouchID(0);
                    boxEvent(vbox.getLayoutX(), vbox.getLayoutY(), t.getTouchPoint().getId());
                } else if (type == TouchEvent.TOUCH_STATIONARY) { // mouse clicked
                    setEventName("STATIONARY");
                    boxEvent(vbox.getLayoutX(), vbox.getLayoutY(), t.getTouchPoint().getId());
                } else if (type == TouchEvent.TOUCH_MOVED) { // mouse dragged
                    setEventName("MOVED");
                    boxEvent(vbox.getLayoutX(), vbox.getLayoutY(), t.getTouchPoint().getId());
                }
            }

        };

        vbox.setOnTouchPressed(touchEvent);
        vbox.setOnTouchReleased(touchEvent);
        vbox.setOnTouchMoved(touchEvent);
        vbox.setOnTouchStationary(touchEvent);

        // ピンチイン／ピンチアウトの処理
        // ピンチアウトすると設定項目が追加され、ピンチインすると削除される。
        EventHandler zoomEvent = new EventHandler<ZoomEvent>() {
            double boxWidth = vbox.getPrefWidth();
            double boxHeight = vbox.getPrefHeight();

            @Override
            public void handle(ZoomEvent t) {
                Object type = t.getEventType();
                if (type == ZoomEvent.ZOOM_STARTED) {

                } else if (type == ZoomEvent.ZOOM) {
//                    System.out.println(" box is zoom!!!!!! : " + t.getEventType() + " , " + t.getZoomFactor());
//                    System.out.println(" box is size : " + boxWidth + " , " + boxHeight);
                    if (t.getZoomFactor() > 1) {
                        addSetUpBox();
                    } else {
                        removeSetUpBox();
                        moveFlag(true);
                    }
                }
            }
        };

        vbox.setOnZoom(zoomEvent);

        //
        vbox.getChildren().add(label);
    }

    
    private boolean iconFlag = true;

    // アイコンのON/OFF機能
    public void iconOnOff() {
        if (iconFlag) { // On の状態のとき、OFFにする
            Label label = new Label();
            label.setAlignment(Pos.BASELINE_CENTER);
            label.setText(name);
            label.setContentDisplay(ContentDisplay.TOP);
            
            vbox.getChildren().remove(0);
            vbox.getChildren().add(0, label);
            boxSizeY = boxSizeY - initBoxSizeY * 2;
            setBoxSize();
            iconFlag = false;
        } else { // Off の状態のとき、ONにする
            Label label = new Label();
            label.setAlignment(Pos.BASELINE_CENTER);
            label.setText(name);
            label.setGraphic(image);
            label.setContentDisplay(ContentDisplay.TOP);

            vbox.getChildren().remove(0);
            vbox.getChildren().add(0, label);
            boxSizeY = boxSizeY + initBoxSizeY * 2;
            setBoxSize();
            iconFlag = true;
        }
    }

    
    private boolean itemFlag = false;
    // 設定値を表示するかのOn/Off
    public void itemOnOff() {
        if (itemFlag) {
            vbox.getChildren().remove(1);
            boxSizeY = boxSizeY - initBoxSizeY;
            setBoxSize();
            itemFlag = false;
        } else {
            Label itemlabel = new Label();
            itemlabel.setAlignment(Pos.BASELINE_CENTER);
            itemlabel.setText(itemStr);
            // 条件文とオブジェクト名の間に入れる線
            Line borderline = new Line(0, 0, initBoxSizeX, 0);
            borderline.setStroke(Color.web("#B3B3B3"));

            itemlabel.setGraphic(borderline);
            itemlabel.setContentDisplay(ContentDisplay.TOP);
            vbox.getChildren().add(1, itemlabel);
            boxSizeY = boxSizeY + initBoxSizeY;
            setBoxSize();
            itemFlag = true;
            reSetItem();
        }
    }

    // 設定値が変更された場合に、描画しなおす
    private void reSetItem() {
        if (itemFlag) {
            Label itemlabel = new Label();
            itemlabel.setAlignment(Pos.BASELINE_CENTER);

            if (itemStr.indexOf("#") != -1) {
                System.out.println("------------colset:" + itemStr);
                int itemNum = itemStr.indexOf("#");
                char[] iChar = {
                    itemStr.charAt(itemNum),
                    itemStr.charAt(itemNum + 1),
                    itemStr.charAt(itemNum + 2),
                    itemStr.charAt(itemNum + 3),
                    itemStr.charAt(itemNum + 4),
                    itemStr.charAt(itemNum + 5),
                    itemStr.charAt(itemNum + 6)
                };
                String iStr = new String(iChar);
                itemlabel.setStyle("-fx-text-fill: " + iStr + ";");
            }

            itemlabel.setText(itemStr);

            // 条件文とオブジェクト名の間に入れる線
            Line borderline = new Line(0, 0, initBoxSizeX, 0);
            borderline.setStroke(Color.web("#B3B3B3"));

            itemlabel.setGraphic(borderline);
            itemlabel.setContentDisplay(ContentDisplay.TOP);
            vbox.getChildren().remove(1);
            vbox.getChildren().add(1, itemlabel);
        }
    }

    // 値が決まっていない箱の大きさをセット
    // 文字の長さなどで大きさを推定
    public void setBoxSize() {
        if(itemFlag){
            if(initBoxSizeX < itemStr.length() * 12){
                boxSizeX = itemStr.length() * 12;
            } else {
                boxSizeX = initBoxSizeX;
            }
        }


        if (setUpFlag) {
            vbox.setPrefSize(boxSizeX, boxSizeY);
        } else {
            vbox.setPrefSize(boxSizeX + setBox.getsetBoxSizeX(), boxSizeY + setBox.getsetBoxSizeY());
        }
        reSetLines();
    }

    // もらった値から大きさを変更
    public void setBoxSize(double sizeX, double sizeY) {
        vbox.setPrefSize(sizeX, sizeY);
        reSetLines();
    }

    // 箱の移動時など、線も描画しなおす
    public void reSetLines() {
        for (int i = 0; i < inLines.size(); i++) {
            inLines.get(i).setEndX(getX() + vbox.getPrefWidth() / 2);
            inLines.get(i).setEndY(getY());
        }
        for (int i = 0; i < outLines.size(); i++) {
            outLines.get(i).setStartX(getX() + vbox.getPrefWidth() / 2);
            outLines.get(i).setStartY(getY() + vbox.getPrefHeight());
        }
    }

    // この箱を動かす時に呼ぶ(線が繋がってたら線も動かす)
    public void move(double x, double y) {
        if (moveFlag) {
            System.out.println("Box : moving... " + x + "," + y);

            vbox.setLayoutX(x);
            vbox.setLayoutY(y);

            // 線も動かす
            reSetLines();
        }
    }

    // 
    private void setEventName(String str) {
        eventName = str;
    }

    // サーバから機器の情報が来た時に呼ばれる
    public void updataState() {
    }

    // 選択、非選択のフラグ
    boolean selectFlag = false;

    // 箱の選択、非選択 
    public void selecter() {
        if (selectFlag) { // 枠を灰色に
            selectFlag = false;
            vbox.setStyle("-fx-background-color: " + colorCode + ";"
                    + "-fx-text-fill: #515151;"
                    + "-fx-border-radius: 20;"
                    + "-fx-background-radius: 20;"
                    + "-fx-border-color: #B3B3B3;"
                    + "-fx-border-width: 2;"
                    + "-fx-padding: 5;");
        } else { // 枠を青色に
            selectFlag = true;
            vbox.setStyle("-fx-background-color: " + colorCode + ";"
                    + "-fx-text-fill: #515151;"
                    + "-fx-border-radius: 20;"
                    + "-fx-background-radius: 20;"
                    + "-fx-border-color: #2933FA;"
                    + "-fx-border-width: 2;"
                    + "-fx-padding: 5;");
        }
    }

    public boolean getSelectFlag() {
        return selectFlag;
    }

    // 設定している値とマッチしているか確かめる
    public void conditionMatch(String condition) {
        if (condition.equals(this.setBox.getItem())) {
            Label label = new Label();
            label.setAlignment(Pos.BASELINE_CENTER);
            label.setText(name);
            label.setGraphic(image);
            label.setContentDisplay(ContentDisplay.TOP);
            label.setStyle("-fx-text-fill: #D31F1F;"); // 赤色
            vbox.getChildren().remove(0);
            vbox.getChildren().add(0, label);
        } else {
            Label label = new Label();
            label.setAlignment(Pos.BASELINE_CENTER);
            label.setText(name);
            label.setGraphic(image);
            label.setContentDisplay(ContentDisplay.TOP);
            label.setStyle("-fx-text-fill: #000000;"); // 黒色
            vbox.getChildren().remove(0);
            vbox.getChildren().add(0, label);
        }
    }

    // 接続された時に呼ばれる。
    // box と line を保持。
    public void connectedIn(MyLine line, Box box) {
        inLines.add(line);
        frontBox.add(box);
    }

    public void connectedOut(MyLine line, Box box) {
        outLines.add(line);
        nextBox.add(box);
    }

    // 箱の切断処理
    // メモ：ラインと箱はセットで追加されるから、同じだお
    public Box disconnectedIn(MyLine line) {
        int index = inLines.indexOf(line);
        System.out.println("Box " + getName() + " : frontBox is " + frontBox.size());
        Box b = frontBox.get(index);
        inLines.remove(index);
        frontBox.remove(index);
        System.out.println("Box " + getName() + " : frontBox is " + frontBox.size());
        return b;
    }

    public boolean disconnectedAllIn() {
        boolean flag = false;
        for (int i = 0; i < inLines.size(); i++) {
            Box b = frontBox.get(i);
            b.disconnectedOut(inLines.get(i));
            flag = true;
        }
        inLines.clear();
        frontBox.clear();
        return flag;
    }

    public Box disconnectedOut(MyLine line) {
        int index = outLines.indexOf(line);
        System.out.println("Box " + getName() + " : nextBox is " + nextBox.size());
        Box b = nextBox.get(index);
        outLines.remove(index);
        nextBox.remove(index);
        System.out.println("Box " + getName() + " : nextBox is " + nextBox.size());
        return b;
    }

    public boolean disconnectedAllOut() {
        boolean flag = false;
        for (int i = 0; i < outLines.size(); i++) {
            Box b = nextBox.get(i);
            b.disconnectedIn(outLines.get(i));
            flag = true;
        }
        outLines.clear();
        nextBox.clear();
        return flag;
    }

    // この箱を消す時の処理(このクラスはもう必要ないっちゃ)
    public void remove() {
        try {
            this.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(Box.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     ****************** get とか set とか。
     */
    // 箱をlabel型で貰えるよ！
    public VBox getLabel() {
        return vbox;
    }

    public String getName() {
        return name;
    }

    // 座標を返してくれます。
    public double getX() {
//        x = vbox.getLayoutX();
        return vbox.getLayoutX();
    }

    public double getY() {
//        y = vbox.getLayoutY();
        return vbox.getLayoutY();
    }

    public double getSizeX() {
        return vbox.getPrefWidth();
    }

    public double getSizeY() {
        return vbox.getPrefHeight();
    }

    public String getBoxMode() {
        return boxMode;
    }

    public String getImgName() {
        String[] strs = imagePath.split("/");
        return strs[strs.length - 1];
    }

    public void setTouchID(int id) {
        this.touchID = id;
    }

    public int getTouchID() {
        return touchID;
    }

    public String getNextBoxStr() {
        String str = "";
        for (int i = 0; i < nextBox.size(); i++) {
            if (i == 0) {
                str = String.valueOf(nextBox.get(i).getBoxID());
            } else {
                str = str + "," + nextBox.get(i).getBoxID();
            }
        }
        return str;
    }

    public ArrayList<Box> getNextBoxs() {
        return nextBox;
    }

    // IDのセットとゲット
    public void setProgramID(String programID) {
        this.programID = programID;
    }

    public void setBoxID(int boxID) {
        this.boxID = boxID;
    }

    public String getProgramID() {
        return programID;
    }

    public int getBoxID() {
        return boxID;
    }

    public String getGUIName() {
        return setBox.getGuiName();
    }

    public void setItem(String itemStr) {
//        System.out.println("Box : setupBox : item : " + itemStr);
        this.itemStr = itemStr;
        reSetItem();
    }

    // 表示されている文字を取る訳ではありません
    // 保存とか内部処理をする為の文字列を返す予定
    public String getItem() {
        return setBox.getItem();
    }

    public String getOptionStr() {
        return setBox.option1 + "," + setBox.option2;
    }

    public String getPlaceStr() {
        String str = "";
        for (int i = 0; i < setBox.getPlaces().length; i++) {
            if (i == 0) {
                str = String.valueOf(setBox.getPlaces()[i]);
            } else {
                str = str + "," + setBox.getPlaces()[i];
            }
        }
        return str;
    }

    public String getPlace() {
        return setBox.getPlace();
    }

    boolean setUpFlag = true;

    public ArrayList<MyLine> getInLines() {
        return this.inLines;
    }

    public ArrayList<MyLine> getOutLines() {
        return this.outLines;
    }

    public void moveFlag(boolean flag) {
        moveFlag = flag;
    }

    public void addSetUpBox() {
        if (setUpFlag) {
            setSetUpBox();
            this.reSetLines();
            setUpFlag = false;
        }
    }

    public void removeSetUpBox() {
        if (!setUpFlag) {
            if (itemFlag) {
                vbox.getChildren().remove(2);
            } else {
                vbox.getChildren().remove(1);
            }
            setUpFlag = true;
            setBoxSize();
        }
    }

    // 箱の設定を行うためのスライダーとかチョイスボックスとかのセット
    public void setSetUpBox() {
        // setBoxからノードをゲットして追加する
        // 次にボックスのサイズを調整する
        setBoxSize(boxSizeX + setBox.getsetBoxSizeX(),
                boxSizeY + setBox.getsetBoxSizeY());
        vbox.getChildren().add(setBox.getBox());
    }

    // 現在設定されているアイテムリストを得る
    public String[] getSetItems() {
        return setBox.items;
    }

    public String getSetItemsStr() {
        String str = setBox.items[0];
        for (int i = 1; i < setBox.items.length; i++) {
            str = str + "," + setBox.items[i];
        }
        return str;
    }

    /**
     * ******** Event Listener に渡すよ *********
     */
    private void boxEvent(double x, double y) {
        pge.boxEventListener(
                new BoxEvent(name, x, y, boxMode, imagePath,
                        setBox.guiName, setBox.getItems(), setBox.option1, setBox.option2, setBox.places,
                        eventName, 0, this));
        // 子がいたら
        for (int i = 0; i < setBox.getItems().length; i++) {
            System.out.println("items:" + setBox.getItems()[i]);
        }
    }

    private void boxEvent(double x, double y, int touchID) {
        pge.boxEventListener(
                new BoxEvent(name, x, y, boxMode, imagePath,
                        setBox.guiName, setBox.getItems(), setBox.option1, setBox.option2, setBox.places,
                        eventName, touchID, this));

        // 子がいたら
        for (int i = 0; i < setBox.getItems().length; i++) {
            System.out.println("items:" + setBox.getItems()[i]);
        }
    }

}
