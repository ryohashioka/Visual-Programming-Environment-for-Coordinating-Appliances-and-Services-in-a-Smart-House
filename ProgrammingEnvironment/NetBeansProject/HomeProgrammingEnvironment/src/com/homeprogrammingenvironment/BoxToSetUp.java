/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.homeprogrammingenvironment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * 複数設定の時は木構造になるから、後で直すよ(今は多くても２種類の設定しかできない)
 *
 * @author ryo
 */
public class BoxToSetUp {

    Box box;
    HBox setBox;
    VBox conBox;

    // サイズはセットボックスが表示される時に，どれくらい拡大するかの値。
    // なので，boxクラスでは boxSize + setBoxSize としなければならないお。
    double setBoxSizeX = 0;
    double setBoxSizeY = 0;
    String item;
    String place;
    String childItem = "";

    String guiName;
    String[] items;
    String[] places;
    String option1, option2;
    String[] items2;

    boolean childFlag = false;
    ArrayList<String> parentList = new ArrayList<String>();
    ArrayList<String> childList = new ArrayList<String>();

    // str -> セットボックスに使うGUIの種類と文字列が書かれたもの
    // placeList -> 場所のリストをString型で
    // items = "guiName,item-item-item,option1,option2"
    // item はリストみたいに並べるなら文字列とか数値、スライダーとかなら最大値，最小値，現在の値などが入る。
    // option1 はitemの前につける記号
    // option2 はitemの後につける記号
    BoxToSetUp(String guiName, String[] item, String[] places, String itemValue, String placeValue,
            String option1, String option2, Box box) {
        this.box = box;
        this.setBox = new HBox(15);
        this.conBox = new VBox(8);
        setBox.setAlignment(Pos.CENTER);
        Label itemlabel = new Label("condition");
        itemlabel.setAlignment(Pos.BASELINE_CENTER);
        conBox.getChildren().add(itemlabel);

        this.guiName = guiName;
        this.items = new String[item.length];
        for (int i = 0; i < this.items.length; i++) {
            this.items[i] = item[i];
        }
        this.places = places;
        this.option1 = option1;
        this.option2 = option2;
        if (itemValue.indexOf(";") != -1) {
            this.item = itemValue.split(";")[0];
            this.childItem = itemValue.split(";")[1];
        } else {
            this.item = itemValue;
        }
        this.place = placeValue;
        this.items2 = new String[item.length];
        for (int i = 0; i < this.items2.length; i++) {
            this.items2[i] = item[i];
        }
        // 子がいたら
        for (int i = 0; i < items2.length; i++) {
            if (items2[i].indexOf(";") != -1) {
                parentList.add(items2[i].split(";")[0]);
                childList.add(items2[i].split(";")[1]);
                items2[i] = items2[i].split(";")[0];
            }
//            System.out.println("items:" + this.items[i]);

        }

        setGUI(guiName, items2, "parent");

        box.setItem(this.item);
        this.setItem(this.item);

        setBox.getChildren().add(conBox);

        changeBoxSize();

        if (places.length <= 1) {
        } else {
            setBoxSizeX = setBoxSizeX + 30 + stringMaxLength(places) * 16;
            double hoge = places.length * 23 + 23;
            if (hoge > setBoxSizeY) {
                setBoxSizeY = hoge;
            }
            this.setPlaceBox(places);
        }
        if (setBoxSizeX < 0) {
            setBoxSizeX = 0;
        }
    }

    // アイテムを追加でセットする時に使おう。
    public void setChildItems(String gui, String[] item) {
        // 貰った親のアイテムを全てのアイテムの中から探して，同じものが選択されたとき，下にguiを追加
        // guiを設定する時は conBox に上に線を引いた上で追加する。

        if (childFlag) {
            removeChildGUI();
        }
        childFlag = true;
        Line vborderline = new Line(0, 0, box.initBoxSizeX, 0);
        vborderline.setStroke(Color.web("#B3B3B3"));
        conBox.getChildren().add(vborderline);

        System.out.println("----------------------------- in setChildItems");
        setGUI(gui, item, "child");

//        this.setChildItem(item[0]);
//        changeBoxSize();
    }

    // 別のが選択されたら一旦消す。
    private void removeChildGUI() {
        int i = conBox.getChildren().size() - 1;
        conBox.getChildren().remove(i);
        conBox.getChildren().remove(i - 1);
    }

    // とりあえず，大きめに取って統一でいいや。
    // itemListとguiで判断
    private void changeBoxSize() {
        // まずはguiで判断
        if (guiName.equals("slider")) {
            setBoxSizeX = setBoxSizeX + 30 + 50;
            setBoxSizeY = setBoxSizeY + 50;
        } else if (guiName.equals("textField")) {
            setBoxSizeX = setBoxSizeX + (items.length - 1) * 20;
            setBoxSizeY = setBoxSizeY + 60;
        } else if (guiName.equals("colorPicker")) {
            setBoxSizeX = setBoxSizeX + 130;
            setBoxSizeY = setBoxSizeY + 150;
        } else if (guiName.equals("choice")) {
            setBoxSizeX = setBoxSizeX - box.initBoxSizeX + 30 + stringMaxLength(items2) * 16; // 多分12ポイント？
            setBoxSizeY = setBoxSizeY + items2.length * 23 + 23;
        } else if (guiName.equals("radio")) {
            setBoxSizeX = setBoxSizeX - box.initBoxSizeX + 30 + stringMaxLength(items2) * 16; // 多分12ポイント？
            setBoxSizeY = setBoxSizeY + items2.length * 23 + 23;
        } else {
        }

        double hogeSizeX = setBoxSizeX;
        // 子はいるかな？
        for (int i = 0; i < childList.size(); i++) {
            String[] str = childList.get(i).split("/");
            String childGUI = str[0];
            String[] ite = new String[str.length - 1];
            for (int j = 0; j < ite.length; j++) {
                ite[j] = str[j + 1];
            }
            if (childGUI.equals("slider")) {
                if (hogeSizeX < 80) {
                    setBoxSizeX = 30 + 50;
                }
                setBoxSizeY = setBoxSizeY + 30;
            } else if (childGUI.equals("textField")) {
                if (hogeSizeX < ((ite.length) * 20 * Integer.parseInt(ite[0])) - box.initBoxSizeX) {
                    setBoxSizeX = ((ite.length) * 20 * Integer.parseInt(ite[0]) - box.initBoxSizeX);
                }
                setBoxSizeY = setBoxSizeY + 40;
            } else if (childGUI.equals("colorPicker")) {
                if (hogeSizeX < 130) {
                    setBoxSizeX = 130;
                }
                setBoxSizeY = setBoxSizeY + 150;
            } else if (childGUI.equals("choice")) {
                if (hogeSizeX < (stringMaxLength(ite) * 16) - box.initBoxSizeX) {
                    setBoxSizeX = (stringMaxLength(ite) * 16) - box.initBoxSizeX; // 多分12ポイント？
                }
                setBoxSizeY = setBoxSizeY + ite.length * 23 + 23;
            } else if (childGUI.equals("radio")) {
                if (hogeSizeX < (stringMaxLength(ite) * 16) - box.initBoxSizeX) {
                    setBoxSizeX = (stringMaxLength(ite) * 16) - box.initBoxSizeX; // 多分12ポイント？
                }
                setBoxSizeY = setBoxSizeY + ite.length * 23 + 23;
            } else {
            }
        }

        box.setBoxSize();
    }

    /**
     * **************************** getter と setter
     * *****************************
     */
    // 主に描画用
    // guiを返します。
    public Node getBox() {
        return setBox;
    }

    // ボックスのサイズ
    public double getsetBoxSizeX() {
        return setBoxSizeX;
    }

    public double getsetBoxSizeY() {
        return setBoxSizeY;
    }

    // セットアイテム関係
    public String getItem() {
        if (childItem.equals("")) {
            return this.item;
        } else {
            return this.item + ";" + this.childItem;
        }
    }
    
    private String getParentItem(){
        return this.item;
    }

    private void setItem(String item) {
        this.item = item;
//        System.out.println("item --- " + this.item);
        // もし，子が存在するものが選択されたら子ノードを追加する
        for (int i = 0; i < parentList.size(); i++) {
            if (item.equals(parentList.get(i))) {
                String[] str = childList.get(i).split("/");
                String gui = str[0];
                String[] ite = new String[str.length - 1];
                for (int j = 0; j < ite.length; j++) {
                    ite[j] = str[j + 1];
                }
                this.setChildItems(gui, ite);
                return;
            }
        }
        if (childFlag) {
            removeChildGUI();
            childFlag = false;
        }

    }

    private void setChildItem(String item) {
        this.childItem = item;
//        System.out.println("childItem --- " + this.childItem);
//        box.setItem(this.item + " - " + this.childItem);
    }

    // 場所関係
    public String getPlace() {
        return this.place;
    }

    private void setPlace(String place) {
        this.place = place;
    }

    // 保存の時に使うよ
    public String getGuiName() {
        return guiName;
    }

    public String[] getItems() {
        return items;
    }

    public String[] getPlaces() {
        return places;
    }

    /**
     * **************************** 以下，各GUIについて *****************************
     */
    // PorC は parent か childのどちらかが入る
    public void setGUI(String gui, String[] item, String PorC) {
        if (gui.equals("slider")) {
            setSlider(item, option1, option2, PorC);
        } else if (gui.equals("textField")) {
            setTextBox(item, option1, option2, PorC);
        } else if (gui.equals("colorPicker")) {
            setColorPicker(item, option1, option2, PorC);
        } else if (gui.equals("choice")) {
//            setChoiceBox(itemlist, option1, option2);
            setRadioButton(item, option1, option2, PorC); // チョイスボックスはAndroid上でToo Many Touch Pointと出て，原因不明
        } else if (gui.equals("radio")) {
            setRadioButton(item, option1, option2, PorC);
        } else {
            System.out.println("other guiName : " + guiName);
        }
    }

    /**
     * ************ slider ****************
     */
    public void setSlider(String[] item, final String option1, final String option2, final String PorC) {
        String itemVal;
        if (PorC.equals("parent")) {
            itemVal = this.item;
        } else {
            itemVal = this.childItem;
        }

        // 一番最初は，読み込んだ値を入れる．
        if (itemVal.equals("")) {
            itemVal = item[3];
        }

        // string から int のキャスト
        int item1 = Integer.parseInt(item[1]);
        int item2 = Integer.parseInt(item[2]);
        int item3 = Integer.parseInt(itemVal);
        // スライダーの準備
        // 最小値，最大値，現在の値
        Slider slider = new Slider(item1, item2, item3);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            String PaorCh = PorC;

            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number t, Number t1) {
                if (PaorCh.equals("parent")) {
                    setItem(String.valueOf(t1.intValue()));
                    box.setItem(option1 + String.valueOf(t1.intValue()) + option2);
                } else {
                    setChildItem(String.valueOf(t1.intValue()));
                    box.setItem(getParentItem() + " - " + option1 + String.valueOf(t1.intValue()) + option2);
                }
            }
        });

        // ボックスになんか起こったら
        EventHandler touchEvent = new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent t) {
                // タッチされたら動かさない。
                box.moveFlag(false);
                Object type = t.getEventType();
                if (type == TouchEvent.TOUCH_RELEASED) { // mouse released
                    box.moveFlag(true);
                }
            }
        };

        if (PorC.equals("parent")) { // 親の場合
            box.setItem(option1 + getParentItem() + option2);
        } else { // 子の場合
            box.setItem(item + " - " + option1 + itemVal + option2);
        }

        slider.setOnTouchPressed(touchEvent);
        slider.setOnTouchReleased(touchEvent);
        slider.setOnTouchMoved(touchEvent);
        slider.setOnTouchStationary(touchEvent);

        conBox.getChildren().add(slider);
    }

    /**
     * ************ textBox ****************
     */
    public void setTextBox(final String[] item, String option1, String option2, final String PorC) {
        String[] itemVals;

        if (PorC.equals("parent")) { // 親の場合
            if (this.item.indexOf("/") != -1) {
                itemVals = this.item.split("/");
            } else {
                itemVals = new String[1];
                itemVals[0] = this.item;
            }
        } else { // 子の場合
            if (this.childItem.indexOf("/") != -1) {
                itemVals = this.childItem.split("/");
            } else {
                itemVals = new String[1];
                itemVals[0] = this.childItem;
            }
        }

        int textFieldSize = Integer.parseInt(item[0]);
        final ArrayList<TextField> textList = new ArrayList<TextField>();
        final ArrayList<Label> labelList = new ArrayList<Label>();
        final HBox hb = new HBox(3);
        int iniCounter = 0;
        String s = "";
        for (int i = 1; i < item.length; i++) {
            if (item[i].equals("text")) {
                // テキストフィールドを追加
                final TextField tf = new TextField(itemVals[iniCounter]);
                s = s + itemVals[iniCounter];
                if (iniCounter < itemVals.length - 1) {
                    iniCounter++;
                } else {
                    itemVals[iniCounter] = "";
                }

                tf.setPrefColumnCount(textFieldSize);
                // テキストイベント。どれかにイベントがあったら全て取得して更新
                tf.setOnAction(new EventHandler<ActionEvent>() {
                    String PaorCh = PorC;

                    @Override
                    public void handle(ActionEvent t) {
                        int c1 = 0;
                        int c2 = 0;
                        String str1 = ""; // 保存用
                        String str2 = ""; // 表示用
                        for (int i = 1; i < item.length; i++) {
                            if (item[i].equals("text")) {
                                if (c1 == 0) {
                                    str1 = str1 + textList.get(c1).getText();
                                } else {
                                    str1 = str1 + "/" + textList.get(c1).getText();
                                }
                                str2 = str2 + textList.get(c1).getText();
                                c1++;
                            } else {
                                str2 = str2 + labelList.get(c2).getText();
                                c2++;
                            }
                        }
                        if (PaorCh.equals("parent")) {
                            setItem(str1);
                            box.setItem(str2);
                        } else {
                            setChildItem(str1);
                            box.setItem(getParentItem() + " - " + str2);
                        }
                    }
                });

                tf.setOnTouchPressed(new EventHandler<TouchEvent>() {
                    @Override
                    public void handle(TouchEvent t) {
                        tf.selectAll();
                    }

                });
                textList.add(tf);
                hb.getChildren().add(tf);
            } else {
                // ラベルを追加
                Label label = new Label(item[i]);
                labelList.add(label);
                hb.getChildren().add(label);
                s = s + item[i];
            }
        }
        if (PorC.equals("parent")) { // 親の場合

        } else { // 子の場合
            box.setItem(getParentItem() + " - " + s);
        }

        conBox.getChildren().add(hb);
    }

    /**
     * ************ radioButton ****************
     */
    public void setRadioButton(String[] item, final String option1, final String option2, final String PorC) {
        String itemVal;
        if (PorC.equals("parent")) {
            itemVal = this.item;
        } else {
            itemVal = this.childItem;
        }

        //
        final ToggleGroup group = new ToggleGroup();
        VBox radibox = new VBox(8);
        radibox.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < item.length; i++) {
            final RadioButton rb = new RadioButton(item[i]);
            rb.setToggleGroup(group);
            if (item[i].equals(itemVal)) {
                rb.setSelected(true);
            }
            rb.setOnAction(new EventHandler<ActionEvent>() {
                String PaorCh = PorC;

                @Override
                public void handle(ActionEvent t) {
                    if (rb.isSelected()) {
//                            System.out.println(rb.getText());
                        if (PaorCh.equals("parent")) {
                            setItem(rb.getText());
                            box.setItem(option1 + rb.getText() + option2);
                        } else {
                            setChildItem(rb.getText());
                            box.setItem(getParentItem() + " - " + option1 + rb.getText() + option2);
                        }
                    }
                }
            });

            radibox.getChildren().add(rb);
        }
        conBox.getChildren().add(radibox);
    }

    /**
     * ************ choiceBox ****************
     */
    // Androidと相性が悪いのかバグかわからないけど，too many touch pointと出て落ちる．
    public void setChoiceBox(String[] item, String option1, String option2, final String PorC) {
        // チョイスボックスなら、単純にアイテムを追加していくだけ。
//            final ChoiceBox<String> choiceBox = new ChoiceBox<String>();
//            choiceBox.getSelectionModel().selectedIndexProperty().addListener(
//                    new ChangeListener<Number>() {
//                        @Override
//                        public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
//                            String str = choiceBox.getItems().get(t1.intValue()).toString();
//                            setItem(str);
//                        }
//                    });
//            for (int i = 0; i < item.length; i++) {
//                choiceBox.getItems().add(item[i]);
//            }
//            choiceBox.setValue(itemVal);
//            vbox.getChildren().add(choiceBox);
    }

    /**
     * ************ colorPicker ****************
     */
    // javafxのカラーピッカーはチョイスボックスを用いてるため、不調
    public void setColorPicker(String[] item, String option1, String option2, final String PorC) {
//        String[] item = items.split("-");
        String itemVal;
        if (PorC.equals("parent")) {
            itemVal = this.item;
        } else {
            itemVal = this.childItem;
        }

        // カラーピッカー
        String color = "";
        if (itemVal.equals("")) {
            color = item[0];
        } else {
            color = itemVal;
        }
        try {
            final VBox cLabel = new VBox();
            cLabel.setAlignment(Pos.CENTER);
//                cLabel.setContentDisplay(ContentDisplay.TOP);
            String textCol = Color.web(color).toString();
            char[] hogeChar = {textCol.charAt(2), textCol.charAt(3),
                textCol.charAt(4), textCol.charAt(5),
                textCol.charAt(6), textCol.charAt(7)};
            String textColor = new String(hogeChar);
            cLabel.setStyle("-fx-text-fill: #" + textColor + ";");
            final Text cText = new Text(textColor);
//                cLabel.setText(textColor);
            final Image img = new Image(new FileInputStream(box.filePath + "color.png"));
            final ImageView cPic = new ImageView(img);
//                cLabel.setGraphic(cPic);

            EventHandler cpeh = new EventHandler<TouchEvent>() {
                String PaorCh = PorC;

                @Override
                public void handle(TouchEvent t) {
                    Object type = t.getEventType();
                    try {
//                        System.out.println("touching ----------------- : " + t.getTarget().toString());
//                        if (t.getTarget().getClass().getSimpleName().equals("Label")) {
                        Color col = img.getPixelReader().getColor((int) t.getTouchPoint().getX(),
                                (int) t.getTouchPoint().getY());
//                            Color col = img.getPixelReader().getColor((int) t.getX(),
//                                    (int) t.getY());
                        String colStr = col.toString();
                        char[] hogeChar = {colStr.charAt(2), colStr.charAt(3),
                            colStr.charAt(4), colStr.charAt(5),
                            colStr.charAt(6), colStr.charAt(7)};
                        String cCode = new String(hogeChar);
//                            cText.setStyle("-fx-text-fill: #" + cCode + ";");
//                            cLabel.setText(cCode);
                        cText.setFill(col);
                        cText.setText(cCode);

                        if (PaorCh.equals("parent")) {
                            setItem("#" + cCode);
                            box.setItem("#" + cCode);
                        } else {
                            setChildItem("#" + cCode);
                            box.setItem(getParentItem() + " - " + "#" + cCode);
                        }

//                            double hue = col.getHue() * 65535;
//                            double sat = col.getSaturation() * 255;
//                            double bri = col.getBrightness() * 255;
//                            setItem(hue + "," + sat + "," + bri);
//                        }
                        box.moveFlag(false);
//                            if (type == TouchEvent.TOUCH_RELEASED) { // mouse released
//                                moveFlag(true);
//                            }
                    } catch (Exception e) {
                    }
                    if (type == TouchEvent.TOUCH_RELEASED) { // mouse released
                        box.moveFlag(true);
                    }
                }
            };

            cPic.setOnTouchPressed(cpeh);
            cPic.setOnTouchMoved(cpeh);
            cPic.setOnTouchReleased(cpeh);

            cLabel.getChildren().addAll(cPic, cText);

            conBox.getChildren().add(cLabel);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Box.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * ************ label ****************
     */
    public void setLabel() {
    }

    /**
     * ************ placeBox ****************
     */
    // 場所を入力する為の箱？これも本当はチョイスボックスでしたいけど、できない。
    public void setPlaceBox(String places[]) {
        String[] item = places;

        final ToggleGroup group = new ToggleGroup();
        VBox radibox = new VBox(8);
        // place という文字を入れる。
        Label itemlabel = new Label("place");
        itemlabel.setAlignment(Pos.BASELINE_CENTER);
        radibox.getChildren().add(itemlabel);
        radibox.setAlignment(Pos.TOP_LEFT);

        for (int i = 0; i < item.length; i++) {
            final RadioButton rb = new RadioButton(item[i]);
            rb.setToggleGroup(group);
            if (item[i].equals(place)) {
                rb.setSelected(true);
            }
            rb.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    if (rb.isSelected()) {
//                            System.out.println(rb.getText());
                        setPlace(rb.getText());
                    }
                }
            });
            radibox.getChildren().add(rb);
        }
        Line hborderline = new Line(0, 0, 0, setBoxSizeY);
        hborderline.setStroke(Color.web("#B3B3B3"));
        setBox.getChildren().add(hborderline);
        setBox.getChildren().add(radibox);
    }

    // 文字列配列の最大文字数を返す
    private int stringMaxLength(String[] str) {
        int max = str[0].length();	// 最初は最大値

        for (int i = 0; i < str.length; i++) {
            if (max < str[i].length()) {	//現在の最大値よりも大きい値が出たら
                max = str[i].length();	//変数maxに値を入れ替える
            }
        }
        return max;
    }
}
