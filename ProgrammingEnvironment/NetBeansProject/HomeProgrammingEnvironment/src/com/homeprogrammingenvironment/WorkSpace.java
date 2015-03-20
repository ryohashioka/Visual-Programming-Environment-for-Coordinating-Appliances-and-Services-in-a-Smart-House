
package com.homeprogrammingenvironment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author ryo
 * プログラムをするフィールド
 * ボックスの生成、削除、接続などが行なえる。
 * 
 */
public class WorkSpace extends ProgrammingEnvironment {

    // boxの接続を行なうクラス。
    BoxConnecter connecter = new BoxConnecter();

    // プログラムフィールドパネル
    final Pane programField = new Pane();
    final double sizeX, sizeY;

    // メニューサイズ
    final double menuSizeX;
    final double menuSizeY;
    // ゴミ箱のサイズ
    final double trashSizeX;
    final double trashSizeY;
    private boolean onTrashBoxFlag = false;

    // プログラムIDリストとボックスIDリスト。
    // ボックスIDリストはプログラムIDと対応付けるために2次元配列
    ArrayList<String> programIDList = new ArrayList<String>();
    ArrayList<ArrayList<Box>> boxList = new ArrayList<ArrayList<Box>>();
    ArrayList<MyLine> lineList = new ArrayList<MyLine>();

    // プログラムの保存、ロードの時に使う filePath
    final String filePath = "/sdcard/HomeProgrammingEnvironment/";
    final String boxListFile = "program_Test.xml";

    // 通信を行なうクラス
    DataCommunications dataCommunications = new DataCommunications(this);

    // 
    WorkSpace(double sizeX, double sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.menuSizeX = 100;
        this.menuSizeY = 350;
        this.trashSizeX = 80;
        this.trashSizeY = 80;
        // サーバとの接続
        dataCommunications.connect();
    }

    // パネルを生成（最初の１回しか呼ばれない）
    public Pane createPane() {
        // プログラムフィールドのパネル
        // 初期状態ではプログラムも何も表示されない
//        programField.setStyle("-fx-background-color: green;");
        programField.setPrefSize(sizeX, sizeY);

        init();
        return programField;
    }

    // ゴミ箱のイメージ
    ImageView image = new ImageView();
    public void init() {
        // 最低限必要なもの
        // ゴミ箱の描画
        String trushFilePath = "/sdcard/HomeProgrammingEnvironment/data/";
        File file = new File("trashBox.png");
        try {
            if (!file.getName().equals("data")) {
                image = new ImageView(new Image(new FileInputStream(trushFilePath + file)));
                image.setFitWidth(trashSizeX);
                image.setFitHeight(trashSizeY);
            }
        } catch (FileNotFoundException ex) {
        }

        // こっから編集用パネル、ボタンの描画
        final AnchorPane menuPane = new AnchorPane();
        menuPane.setStyle("-fx-border-color: #B3B3B3;"
                + "-fx-border-width: 0.5;");
        VBox menuBox = new VBox(10);
        menuBox.setAlignment(Pos.BOTTOM_CENTER);
        menuBox.setPrefSize(menuSizeX, menuSizeY);
//        menuBox.setStyle("-fx-background-color: #B3B3B3;");

        menuPane.setLayoutX(sizeX - menuSizeX);
        menuPane.setLayoutY(-menuSizeY);
        menuPane.setPrefSize(menuSizeX, menuSizeY);

        // プログラム読み込み用ボタン
        Button loadBtn = new Button("load");
        loadBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadPrograms();
            }
        });
        loadBtn.setLayoutX(50);
        loadBtn.setLayoutY(50);
        loadBtn.setPrefHeight(30);
        menuBox.getChildren().add(loadBtn);

        // プログラム保存用ボタン
        Button saveBtn = new Button("save");
        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                savePrograms();
            }
        });
        saveBtn.setLayoutX(50);
        saveBtn.setLayoutY(100);
        saveBtn.setPrefHeight(30);
        menuBox.getChildren().add(saveBtn);

        // 選択されているiconを出したり消したりするボタン
        Button iconBtn = new Button("iconOn/Off");
        iconBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (int i = 0; i < programIDList.size(); i++) {
                    for (int j = 0; j < boxList.get(i).size(); j++) {
                        if (boxList.get(i).get(j).getSelectFlag()) {
                            boxList.get(i).get(j).iconOnOff();
                        }
                    }
                }
            }
        });
        iconBtn.setLayoutX(50);
        iconBtn.setLayoutY(50);
        iconBtn.setPrefHeight(30);
        menuBox.getChildren().add(iconBtn);

        // 選択されているitemを出したり消したりするボタン
        Button itemBtn = new Button("itemOn/Off");
        itemBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (int i = 0; i < programIDList.size(); i++) {
                    for (int j = 0; j < boxList.get(i).size(); j++) {
                        if (boxList.get(i).get(j).getSelectFlag()) {
                            boxList.get(i).get(j).itemOnOff();
                        }
                    }
                }
            }
        });
        itemBtn.setLayoutX(50);
        itemBtn.setLayoutY(50);
        itemBtn.setPrefHeight(30);
        menuBox.getChildren().add(itemBtn);

        // すべてのボックスを削除するボタン
        Button rmBtn = new Button("removeAll");
        rmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removeAll();
            }
        });
        rmBtn.setLayoutX(50);
        rmBtn.setLayoutY(150);
        rmBtn.setPrefHeight(30);
        menuBox.getChildren().add(rmBtn);

        // 選択されているオブジェクトを削除するボタン
        Button rmsBtn = new Button("remove");
        rmsBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removeSelectedBox();
            }
        });
        rmsBtn.setLayoutX(50);
        rmsBtn.setLayoutY(150);
        rmsBtn.setPrefHeight(30);
        menuBox.getChildren().add(rmsBtn);

        // 
        menuPane.getChildren().add(menuBox);
        menuPane.setBottomAnchor(menuBox, 10.0);

        menuBox.getChildren().add(image);

        // メニューを出し入れするためのパネル
        // タッチすると出し入れできる。
        final Pane subpane = new StackPane();
        subpane.setPrefSize(menuSizeX, 30);

        subpane.setLayoutX(menuPane.getLayoutX());
        subpane.setLayoutY(0);
        final Label menuText = new Label("↓ Menu ↓");
        subpane.setStyle("-fx-background-color: #E5E5E5;"
                + "-fx-border-color: #B3B3B3;"
                + "-fx-border-width: 0.5;");
        subpane.getChildren().add(menuText);
        menuText.setPrefHeight(30);

        subpane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            boolean flag = false;

            @Override
            public void handle(MouseEvent t) {
//                menuPane.setLayoutX(t.getSceneX());
                if (flag) { // 出す！
                    menuPane.setLayoutY(0);
                    subpane.setLayoutY(menuSizeY);
                    menuText.setText("↑ Menu ↑");
                    menuFlag(flag);
                    flag = false;
                } else { // しまう！
                    menuPane.setLayoutY(-menuSizeY);
                    subpane.setLayoutY(0);
                    menuText.setText("↓ Menu ↓");
                    menuFlag(flag);
                    flag = true;
                }
            }
        });

        // メニューをプログラムフィールドに追加
        programField.getChildren().addAll(menuPane, subpane);
    }

    
    private boolean menuflag = false;
    public void menuFlag(boolean flag) {
        menuflag = flag;
    }

    public void actionBoxEvent(String str) {

    }

    // 終了処理
    public void stop() {
        dataCommunications.disconnect();
    }

    // 箱の追加
    public void addBox(Box b) {
        System.out.println("WorkSpace : add box.");
        // 今回は接続も無いオブジェクトの追加なので、プログラムIDを生成し、ボックスIDも生成する
        String programID = this.newProgramID();
        int boxID = this.newBoxID(programID, b);
        b.setProgramID(programID);
        b.setBoxID(boxID);

        // 追加
        programField.getChildren().add(b.getLabel());
    }

    // 箱の追加（プログラムIDとboxIDがすでにある場合）
    public void addBox(Box b, String programID, int boxID) {
        System.out.println("WorkSpace : add box.");
        // idのセット
        setProgramID(programID);
        setBoxID(programID, b, boxID);
        b.setProgramID(programID);
        b.setBoxID(boxID);

        // 追加
        programField.getChildren().add(b.getLabel());
    }

    // 線を接続するメソッドです。実際に接続するのはBoxConnecterクラスです。
    // 接続されるとIDも新しく生成し、前のIDを削除する。
    public void connectBox(Box box1, Box box2) {
//        connecter.connect(box1,box2);
        // すでにオブジェクトが含まれていたら接続しません。（2重接続の防止）
        ArrayList<Box> bs = box1.getNextBoxs();
        for (int i = 0; i < bs.size(); i++) {
            if (bs.get(i) == box2) {
                return;
            }
        }
        // 以下、接続処理
        System.out.println("connect");
        if (box1.getBoxMode().equals("output") && box2.getBoxMode().equals("output")) { // 両方制御されるボックスだったら
            // 接続しません。
            System.out.println("outPut同士では接続できません。");
        } else { // 接続処理
            System.out.println("WorkSpace : connect Box.");
            MyLine line = connecter.connect(box1, box2);
            programField.getChildren().add(line);
            lineList.add(line);
//            box1.connectedOut(line, box2);
//            box2.connectedIn(line, box1);

            //////////// ここからはIDの振り直し処理
            String programID1 = box1.getProgramID();
            String programID2 = box2.getProgramID();
            // 同じプログラム内で線が引かれたらなにもしない。
            if (programID1.equals(programID2)) {

            } else {
                if (programIDList.contains(programID2)) {
                    if (programIDList.contains(programID1)) {
                        ArrayList<Box> boxList1 = boxList.get(programIDList.indexOf(programID1)); // out側
                        ArrayList<Box> boxList2 = boxList.get(programIDList.indexOf(programID2)); // in側
                        // in側をout側の配列に入れる。
                        for (int i = 0; i < boxList2.size(); i++) {
                            boxList1.add(boxList2.get(i));
                        }
                        // IDの振り直し。boxをリストに追加
                        String newProgramID = this.newProgramID();
                        for (int i = 0; i < boxList1.size(); i++) {
                            Box b = boxList1.get(i);
                            b.setProgramID(newProgramID);
                            b.setBoxID(newBoxID(newProgramID, b));
                        }
                        // 前のIDは削除
                        this.removeProgramID(programID1);
                        this.removeProgramID(programID2);

                    } else {
                        System.out.println(programID1 + "is not existence!");
                    }
                } else {
                    System.out.println(programID2 + "is not existence!");
                }
            }
        }
    }

    // すでにIDがある場合の接続。(主に、プログラムの読み込み時に使う)
    // programIDとboxIDからオブジェクトを探して、nextBoxIDのオブジェクトと接続
    public void createLine(String programID, int boxID, int nextBoxID) {
        Box box1;
        Box box2;

        int num = programIDList.indexOf(programID);
        box1 = boxList.get(num).get(boxID);
        box2 = boxList.get(num).get(nextBoxID);
        System.out.println("boxID1 : " + box1.name + ", boxID2 : " + box2.name);

        // 接続処理
        System.out.println("connect");
        if (box1.getBoxMode().equals("output") && box2.getBoxMode().equals("output")) {
            // 接続しません。
            System.out.println("outPut同士では接続できません。");
        } else { // 接続処理
            System.out.println("WorkSpace : connect Box.");
            MyLine line = connecter.connect(box1, box2);
            programField.getChildren().add(line);
            lineList.add(line);
//            box1.connectedOut(line, box2);
//            box2.connectedIn(line, box1);
        }
    }

    // 別の所で書いています。
    public void disconnectBox() {
    }

    // ゴミ箱の上にあるかどうか
    private boolean onTrashBox(double x, double y) {
        if (menuflag) {
            if (sizeX - menuSizeX < x && x < sizeX && 0 < y && y < menuSizeY) {
                return true;
            }
        }
        return false;
    }

    /**
     * ******* ID関連 *********
     */
    // boxIDの生成。プログラムIDが存在しなければ -1 を返す。（生成失敗）
    public int newBoxID(String programID, Box box) {
        if (programIDList.contains(programID)) {
            int index = programIDList.indexOf(programID);
            boxList.get(index).add(box);
            return (boxList.get(index).size() - 1);
        } else {
            // 生成失敗（描画はされている）
            return -1;
        }
    }

    // すでに存在するオブジェクトIDを配列に入れる。
    public void setBoxID(String programID, Box box, int boxID) {
        if (programIDList.contains(programID)) {
            int index = programIDList.indexOf(programID);
            if (boxList.get(index).size() == boxID) { // 追加できるIDなら追加する
                boxList.get(index).add(box);
            } else if (boxList.get(index).size() > boxID) { // 追加できないIDなら置き換える
                boxList.get(index).set(boxID, box);
            } else {
                while (boxList.get(index).size() == boxID) {
                    boxList.get(index).add(null);
                }
                boxList.get(index).add(box);
            }
        }
    }

    // 新しいプログラムIDを生成して、文字列で返します。
    public String newProgramID() {
        String uuidStr;
        UUID programUUID = UUID.randomUUID(); // とりあえず、ランダムなUUID
        String[] uuidStr1 = programUUID.toString().split("-");
        uuidStr = uuidStr1[0];
        for (int j = 1; j < uuidStr1.length; j++) {
            uuidStr = uuidStr + uuidStr1[j];
        }
        programIDList.add(uuidStr);
        boxList.add(new ArrayList<Box>());
        return uuidStr;
    }

    // プログラムIDをセットします。（主にプログラム読み込み時に使います。）
    public void setProgramID(String programID) {
        if (programIDList.contains(programID)) {
            // すでに含まれていたらなにもしません。
            System.out.println("WorkSpace : This programID is already contain.");
        } else {
            programIDList.add(programID);
            boxList.add(new ArrayList<Box>());
        }
    }

    // プログラムの削除
    public void removeProgramID(String programID) {
        boxList.remove(programIDList.indexOf(programID));
        programIDList.remove(programID);
    }

    // オブジェクトの削除
    public void removeBox(String programID, Box box) {
        System.out.println("Remove Box : " + box.getName());
        String beforeProgramID = box.getProgramID();
        int index = programIDList.indexOf(programID);
        // とりあえずリストから削除
        boxList.get(index).remove(box);
        // idが変わるので振り直し。
        for (int i = 0; i < boxList.get(index).size(); i++) {
            boxList.get(index).get(i).setBoxID(i);
        }
        // 描画しているものの削除
        // 箱
        this.programField.getChildren().remove(box.getLabel());
        System.out.println(this.programField.getChildren().size());
        // 箱に繋がっている線
        ArrayList<MyLine> inLineList = box.getInLines();
        ArrayList<MyLine> outLineList = box.getOutLines();
        boolean inFlag = false;
        boolean outFlag = false;
        int size = inLineList.size();
        for (int i = 0; i < size; i++) {
            MyLine line = inLineList.get(i);
//            // 描画を消す
            this.programField.getChildren().remove(line);
            this.lineList.remove(line);

        }
        size = outLineList.size();
        for (int i = 0; i < size; i++) {
            MyLine line = outLineList.get(i);
            this.programField.getChildren().remove(line);
            this.lineList.remove(line);

        }
        inFlag = box.disconnectedAllIn();
        outFlag = box.disconnectedAllOut();
        // 箱が繋がっていない場合、programIDの削除。
        if (!inFlag && !outFlag) {
            removeProgramID(programID);
        } // 前後に繋がっている場合はprogramIDの生成。
        else {
            // ここからはIDの振り直し
            ArrayList<Box> beforeProgram;
            beforeProgram = boxList.get(programIDList.indexOf(beforeProgramID));
            ArrayList<ArrayList<Box>> newPrograms = new ArrayList<ArrayList<Box>>();
            int c = 0;
            for (int j = 0; j < beforeProgram.size(); j++) {
                if (beforeProgram.get(j).inLines.size() == 0) { // 先頭のとき
                    System.out.println("-------------------------" + beforeProgram.get(j).getName());
                    // まずは新しい配列に追加
                    newPrograms.add(new ArrayList<Box>());
                    newPrograms.get(c).add(beforeProgram.get(j));
                    // どんどん下にたどっていく
                    for (int k = 0; k < newPrograms.get(c).size(); k++) {
                        for (int l = 0; l < newPrograms.get(c).get(k).nextBox.size(); l++) {
                            newPrograms.get(c).add(newPrograms.get(c).get(k).nextBox.get(l));

                        }
                    }
                    c++;
                }
            }
            // 箱の重複をチェックして重複したものがあれば、片方の配列に放り込む
            for (int j = 0; j < newPrograms.size(); j++) {
                for (int k = 0; k < newPrograms.get(j).size(); k++) {
                    for (int l = 0; l < newPrograms.size(); l++) {
                        boolean flag = false;
                        if (j >= l) {
                            // j より小さい場合は何もしない（同じプログラム or 確認済み）
                        } else {
                            for (int m = 0; m < newPrograms.get(l).size(); m++) {
                                if (newPrograms.get(j).get(k) == newPrograms.get(l).get(m)) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        // もし、同じものがあれば放り込む。
                        // そして、削除！
                        if (flag) {
                            for (int m = 0; m < newPrograms.get(l)
                                    .size(); m++) {
                                newPrograms.get(j).add(
                                        newPrograms.get(l).get(m));
                            }
                            newPrograms.remove(l);
                            l--;
                        }
                    }
                }
            }
            // IDの振り直し処理
            for (int j = 0; j < newPrograms.size(); j++) {
                String id = newProgramID();
                for (int k = 0; k < newPrograms.get(j).size(); k++) {
                    Box db = newPrograms.get(j).get(k);
                    db.setProgramID(id);
                    db.setBoxID(newBoxID(id, db));
                    System.out.println(db.getName() + "-------------------------" + db.getProgramID() + "," + db.getBoxID());
                }
            }
            // あらたにプログラムを作りなおしたので、前のプログラムは削除！
            this.removeProgramID(programID);
        }
        // 完全な削除
        box.remove();
    }

    // 選択されているボックスの削除
    public void removeSelectedBox() {
        for (int i = boxList.size() - 1; i >= 0; i--) {
            for (int j = boxList.get(i).size() - 1; j >= 0; j--) {
                if (boxList.get(i).get(j).getSelectFlag()) {
                    removeBox(programIDList.get(i), boxList.get(i).get(j));
                }
            }
        }

        // 線の削除。箱の接続関係を無くして(idの振り直しも)、描画をけす。そして開放。
        for (int i = lineList.size() - 1; i >= 0; i--) {
            if (lineList.get(i).getSelectFlag()) {
                String beforeProgramID = "";
                MyLine line = lineList.get(i);
                for (int j = boxList.size() - 1; j >= 0; j--) {
                    for (int k = boxList.get(j).size() - 1; k >= 0; k--) {
                        Box box = boxList.get(j).get(k);
                        if (box.inLines.contains(line)) {
                            box.disconnectedIn(line);
                        }
                        if (box.outLines.contains(line)) {
                            box.disconnectedOut(line);
                        }
                        beforeProgramID = box.getProgramID();
//                        System.out.println("-------------------------" + beforeProgramID);

                    }
                }
                this.programField.getChildren().remove(line);
                lineList.remove(line);
                line.remove();

//                System.out.println("-------------------------" + beforeProgramID);

                // ここからはIDの振り直し
                ArrayList<Box> beforeProgram;
                beforeProgram = boxList.get(programIDList.indexOf(beforeProgramID));
                ArrayList<ArrayList<Box>> newPrograms = new ArrayList<ArrayList<Box>>();
                int c = 0;
                for (int j = 0; j < beforeProgram.size(); j++) {
                    if (beforeProgram.get(j).inLines.size() == 0) { // 先頭のとき
//                        System.out.println("-------------------------" + beforeProgram.get(j).getName());
                        // まずは新しい配列に追加
                        newPrograms.add(new ArrayList<Box>());
                        newPrograms.get(c).add(beforeProgram.get(j));
                        // どんどん下にたどっていく
                        for (int k = 0; k < newPrograms.get(c).size(); k++) {
                            for (int l = 0; l < newPrograms.get(c).get(k).nextBox.size(); l++) {
                                newPrograms.get(c).add(newPrograms.get(c).get(k).nextBox.get(l));

                            }
                        }
                        c++;
                    }
                }
                // 箱の重複をチェックして重複したものがあれば、片方の配列に放り込む
                for (int j = 0; j < newPrograms.size(); j++) {
                    for (int k = 0; k < newPrograms.get(j).size(); k++) {
                        for (int l = 0; l < newPrograms.size(); l++) {
                            boolean flag = false;
                            if (j >= l) {
                                // j より小さい場合は何もしない（同じプログラム or 確認済み）
                            } else {
                                for (int m = 0; m < newPrograms.get(l).size(); m++) {
                                    if (newPrograms.get(j).get(k) == newPrograms.get(l).get(m)) {
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                            // もし、同じものがあれば放り込む。
                            // そして、削除！
                            if (flag) {
                                for (int m = 0; m < newPrograms.get(l)
                                        .size(); m++) {
                                    newPrograms.get(j).add(
                                            newPrograms.get(l).get(m));
                                }
                                newPrograms.remove(l);
                                l--;
                            }
                        }
                    }
                }
                // IDの振り直し処理
                for (int j = 0; j < newPrograms.size(); j++) {
                    String id = newProgramID();
                    for (int k = 0; k < newPrograms.get(j).size(); k++) {
                        Box db = newPrograms.get(j).get(k);
                        db.setProgramID(id);
                        db.setBoxID(newBoxID(id, db));
//                        System.out.println(db.getName() + "-------------------------" + db.getProgramID() + "," + db.getBoxID());
                    }
                }
                // あらたにプログラムを作りなおしたので、前のプログラムは削除！
                this.removeProgramID(beforeProgramID);
            }
        }
    }

    // 全てのボックスを削除
    public void removeAll() {
        programField.getChildren().clear();
        programIDList.clear();
        boxList.clear();
        // 最後に初期化
        init();
    }

    // 
    /**
     * ******* ID関連owari *********
     */
    
    
    // プログラムのロード
    public void loadPrograms() {
        System.out.println("ProgrammingEnvironment : load");
        // xmlファイルの読み込み
        if (this.dataCommunications.connectFlag) {
            this.dataCommunications.recieveProgramFile();
        }
        
        // 現在の箱の削除(初期化)
        removeAll();
        // xmlファイルを上から読み込んでいって、追加するだけ
        try {
            // 読み込む前準備
            File file = new File(filePath + boxListFile);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            // 読み込み開始
            document = builder.parse(file);
            Element root = document.getDocumentElement();
            System.out.println("load XML : root = " + root.getNodeName());

            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
//                System.out.println("+++++++++++++++++++++++++++");
                Node child = children.item(i);
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    // プログラムIDの取得
                    String programIDstr = childElement.getAttribute("id");
                    // ここからオブジェクト毎に読み込んでいく
                    NodeList grandChildren = childElement.getChildNodes();
                    for (int j = 0; j < grandChildren.getLength(); j++) {
                        Node grandChild = grandChildren.item(j);
                        if (grandChild instanceof Element) {
                            Element grandChildElement = (Element) grandChild;
                            addBox(new Box(grandChildElement.getAttribute("name"),
                                    Double.parseDouble(grandChildElement.getAttribute("x")),
                                    Double.parseDouble(grandChildElement.getAttribute("y")),
                                    grandChildElement.getAttribute("boxClass"),
                                    grandChildElement.getAttribute("gui"),
                                    grandChildElement.getAttribute("set").split(","),
                                    grandChildElement.getAttribute("option").split(",")[0],
                                    grandChildElement.getAttribute("option").split(",")[1],
                                    grandChildElement.getAttribute("places").split(","),
                                    grandChildElement.getAttribute("place"),
                                    grandChildElement.getAttribute("operation"),
                                    grandChildElement.getAttribute("icon"),
                                    "",// ここはカテゴリーが入る。けど、WSではカテゴリーは関係ないので空でよい。
                                    this),
                                    programIDstr,
                                    Integer.parseInt(grandChildElement.getAttribute("boxID")));
                            // オブジェクトの生成をして、描画
                        }
                    }

                    // 線を引きます！
                    for (int j = 0; j < grandChildren.getLength(); j++) {
                        Node grandChild = grandChildren.item(j);
                        if (grandChild instanceof Element) {
                            Element grandChildElement = (Element) grandChild;
                            if (grandChildElement.getAttribute("nextBox").equals("")) {
                                // 次の箱が無ければ何もしない
                            } else {
                                String[] nextStr = grandChildElement.getAttribute("nextBox").split(",");
                                for (int k = 0; k < nextStr.length; k++) {
                                    createLine(programIDstr,
                                            Integer.parseInt(grandChildElement.getAttribute("boxID")),
                                            Integer.parseInt(nextStr[k]));
                                }
                            }
                        }
                    }
                }

            }
        } catch (ParserConfigurationException ex) {
            System.out.println(ex);
        } catch (SAXException ex) {
            Logger.getLogger(BoxList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BoxList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // プログラムのセーブ
    public void savePrograms() {
        System.out.println("ProgrammingEnvironment : save programs");

        // xmlの準備
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }
        Document document = documentBuilder.newDocument();

        // XML文書の作成
        Element root = document.createElement("system");

        for (int i = 0; i < programIDList.size(); i++) {
            Element program = document.createElement("program");
            program.setAttribute("id", programIDList.get(i));

            for (int j = 0; j < boxList.get(i).size(); j++) {
                Box box = boxList.get(i).get(j);
                Element object = document.createElement("object");
                object.setAttribute("boxClass", box.getBoxMode());
                object.setAttribute("boxID", String.valueOf(box.getBoxID()));
                object.setAttribute("name", box.getName());
                object.setAttribute("nextBox", box.getNextBoxStr());
                object.setAttribute("operation", box.getItem());
                object.setAttribute("x", String.valueOf(box.getX()));
                object.setAttribute("y", String.valueOf(box.getY()));
                object.setAttribute("icon", box.getImgName());
                object.setAttribute("gui", box.getGUIName());
                object.setAttribute("set", box.getSetItemsStr());
                object.setAttribute("option", box.getOptionStr());
                object.setAttribute("places", box.getPlaceStr());
                object.setAttribute("place", box.getPlace());
                program.appendChild(object);
            }
            root.appendChild(program);
        }
        document.appendChild(root);

        
        // 以前のプログラムと今のプログラムを比較して保存
        // サーバに接続できていない場合は、追加保存する。（できていません。）
        
        // 読み込む前準備
        File file2 = new File(filePath + boxListFile);
        System.out.println(file2.getPath());

        DocumentBuilder logBuilder = null;
        DocumentBuilder beforeBuilder = null;
        try {
            logBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            beforeBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(WorkSpace.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 以下、サーバに送信するファイルの生成
        Document logDoc = documentBuilder.newDocument();
        Element logRoot = logDoc.createElement("programLogs");
        logDoc.appendChild(logRoot);

        Document beforeDoc = beforeBuilder.newDocument();
        try {
            beforeDoc = beforeBuilder.parse(file2);
        } catch (SAXException ex) {
            Logger.getLogger(WorkSpace.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(file2.getName() + "error1");
        } catch (IOException ex) {
            Logger.getLogger(WorkSpace.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(file2.getName() + "error2");
        }

        Element beforeRoot = beforeDoc.getDocumentElement();

        int[] num = new int[beforeRoot.getChildNodes().getLength()];
        System.out.println(num.length);
        for (int i : num) {
            num[i] = 0;
        }

        System.out.println(root.getChildNodes().getLength());
        for (int i = 0; i < root.getChildNodes().getLength(); i++) {
            int counter = 0;
            int cco = 0;
            Node child = root.getChildNodes().item(i);
            if (child instanceof Element) {
                Element childElement = (Element) child;
                String presentID = childElement.getAttribute("id");
                for (int j = 0; j < beforeRoot.getChildNodes().getLength(); j++) {
                    Node beforeChild = beforeRoot.getChildNodes().item(j);
                    if (beforeChild instanceof Element) {
                        Element childElement2 = (Element) beforeChild;
                        if (childElement2.getTagName().equals("program")) { // 保存前後のプログラムIDの比較
                            String pastID = childElement2.getAttribute("id");
                            System.out.println(presentID + ", " + pastID);
                            if (presentID.equals(pastID)) { // もしプログラムIDが一致したならなら
                                counter++;
                                num[j]++;
                                System.out.println("-----------------------------------");
                                // id が一致して operation も一致したら
                                // 親の設定値は
                                for (int k = 0; k < childElement.getChildNodes().getLength(); k++) {
                                    Node presentObjectNode = childElement.getChildNodes().item(k);
                                    if (presentObjectNode instanceof Element) {
                                        Element presentObjectElement = (Element) presentObjectNode;
                                        String presentObjectName = presentObjectElement.getAttribute("name");
                                        String presentObjectOperation = presentObjectElement.getAttribute("operation");
                                        String presentObjectX = presentObjectElement.getAttribute("x");
                                        String presentObjectY = presentObjectElement.getAttribute("y");
                                        String presentObjectPlace = presentObjectElement.getAttribute("place");

                                        System.out.println(presentObjectName + " n " + presentObjectOperation);
                                        for (int l = 0; l < childElement2.getChildNodes().getLength(); l++) {
                                            Node beforeObjectNode = childElement2.getChildNodes().item(l);
                                            if (beforeObjectNode instanceof Element) {
                                                Element beforeObjectElement = (Element) beforeObjectNode;
                                                String beforeObjectName = beforeObjectElement.getAttribute("name");
                                                String beforeObjectOperation = beforeObjectElement.getAttribute("operation");
                                                String beforeObjectX = beforeObjectElement.getAttribute("x");
                                                String beforeObjectY = beforeObjectElement.getAttribute("y");
                                                String beforeObjectPlace = beforeObjectElement.getAttribute("place");

                                                System.out.println("PresentName : " + presentObjectName
                                                        + " , BeforeName : " + beforeObjectName
                                                        + " , PresentOperation : " + presentObjectOperation
                                                        + " , BeforeOperation : " + beforeObjectOperation);
                                                if (presentObjectName.equals(beforeObjectName)
                                                        && !presentObjectOperation.equals(beforeObjectOperation)) {
                                                    cco++;
                                                    System.out.println("ope dayo-=---0-0-0=-=-=nn");
                                                }
                                                if (presentObjectName.equals(beforeObjectName)
                                                        && !presentObjectX.equals(beforeObjectX)) {
                                                    cco++;
                                                    System.out.println("x dayo-=---0-0-0=-=-=nn");
                                                }
                                                if (presentObjectName.equals(beforeObjectName)
                                                        && !presentObjectY.equals(beforeObjectY)) {
                                                    cco++;
                                                    System.out.println("y dayo-=---0-0-0=-=-=nn");
                                                }
                                                if (presentObjectName.equals(beforeObjectName)
                                                        && !presentObjectPlace.equals(beforeObjectPlace)) {
                                                    cco++;
                                                    System.out.println("pla dayo-=---0-0-0=-=-=nn");
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            num[j]++;
                        }
                    }
                }
                if (counter == 0) { // 一致するものが無ければ
                    Element addElement = logDoc.createElement("add");
                    addElement.setAttribute("id", childElement.getAttribute("id"));
                    addElement.setAttribute("switch", "on"); // プログラムを実行するかしないかのON/OFF（現在はすべてON）
                    logRoot.appendChild(addElement);

                    for (int j = 0; j < childElement.getChildNodes().getLength(); j++) {
                        Element grandChildElement = (Element) childElement.getChildNodes().item(j);
                        System.out.println("add" + grandChildElement.getNodeName());
                        if (grandChildElement.getTagName().equals("object")) {
                            Element addChildElement = logDoc.createElement("object");
                            addChildElement.setAttribute("boxClass", grandChildElement.getAttribute("boxClass"));
                            addChildElement.setAttribute("boxID", grandChildElement.getAttribute("boxID"));
                            addChildElement.setAttribute("name", grandChildElement.getAttribute("name"));
                            addChildElement.setAttribute("nextBox", grandChildElement.getAttribute("nextBox"));
                            addChildElement.setAttribute("operation", grandChildElement.getAttribute("operation"));
                            addChildElement.setAttribute("x", grandChildElement.getAttribute("x"));
                            addChildElement.setAttribute("y", grandChildElement.getAttribute("y"));
                            addChildElement.setAttribute("icon", grandChildElement.getAttribute("icon"));
                            addChildElement.setAttribute("gui", grandChildElement.getAttribute("gui"));
                            addChildElement.setAttribute("set", grandChildElement.getAttribute("set"));
                            addChildElement.setAttribute("option", grandChildElement.getAttribute("option"));
                            addChildElement.setAttribute("place", grandChildElement.getAttribute("place"));
                            addChildElement.setAttribute("places", grandChildElement.getAttribute("places"));
                            addElement.appendChild(addChildElement);
                        }
                    }
                }
                if (cco != 0) {
                    Element changeElement = logDoc.createElement("change");
                    changeElement.setAttribute("id", childElement.getAttribute("id"));
                    changeElement.setAttribute("switch", "on"); 
                    logRoot.appendChild(changeElement);

                    for (int j = 0; j < childElement.getChildNodes().getLength(); j++) {
                        Element grandChildElement = (Element) childElement.getChildNodes().item(j);
                        System.out.println("change" + grandChildElement.getNodeName());
                        if (grandChildElement.getTagName().equals("object")) {
                            Element addChildElement = logDoc.createElement("object");
                            addChildElement.setAttribute("boxID", grandChildElement.getAttribute("boxID"));
                            addChildElement.setAttribute("name", grandChildElement.getAttribute("name"));
                            addChildElement.setAttribute("operation", grandChildElement.getAttribute("operation"));
                            addChildElement.setAttribute("x", grandChildElement.getAttribute("x"));
                            addChildElement.setAttribute("y", grandChildElement.getAttribute("y"));
                            addChildElement.setAttribute("place", grandChildElement.getAttribute("place"));
                            changeElement.appendChild(addChildElement);
                        }
                    }

                }
            }
        }
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 0) {
                Node beforeChild = beforeRoot.getChildNodes().item(i);
                if (beforeChild instanceof Element) {
                    Element root2ChildElement = (Element) beforeChild;
                    System.out.println("remove" + i + " : " + root2ChildElement.getAttribute("id"));
                    Element removeChild = logDoc.createElement("remove");
                    removeChild.setAttribute("id", root2ChildElement.getAttribute("id"));
                    logRoot.appendChild(removeChild);
                }
            }
        }

        // 時間の子
        Element otherChild = logDoc.createElement("other");
        otherChild.setAttribute("time", timeToString());
        logRoot.appendChild(otherChild);

        // もし、ネットに接続できていなかったら、Logは追加保存(まだ)
        // XMLファイルの作成
        File file = new File(filePath + boxListFile);
        File fileLog = new File(filePath + "programLog.xml");

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("indent", "yes"); //改行指定
//            transformer.setOutputProperty("encoding", "Shift_JIS"); // エンコーディング
//            transformer.transform(new DOMSource(logDoc), new StreamResult(fileLog));
            transformer.transform(new DOMSource(document), new StreamResult(file));
            transformer.transform(new DOMSource(logDoc), new StreamResult(fileLog));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            return;
        } catch (TransformerException e) {
            e.printStackTrace();
            return;
        }

        // サーバにファイルを送信
        try {
            if (!dataCommunications.getConnectFlag()) {
                dataCommunications.connect();
            }
            dataCommunications.sendFile(filePath + "programLog.xml");
        } catch (Exception ex) {
            System.out.println("ファイル送信失敗");
        }
    }

    // 時間をString型で返す。主に保存のときに使う。
    public String timeToString() {
        String updateTime;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        updateTime = sdf.format(c.getTime());
        // フォーマットパターン変更して表示する
        sdf.applyPattern("HHmmss");
        updateTime = updateTime + sdf.format(c.getTime());
        return updateTime;
    }

    // ページとかあったらええかも！
    // とりあえず、実装は後回し。
    public void createNewPage() {
    }

    public void removePage() {
    }

    // これはとりあえずの処理。
    // touchIDが14を超えるとヌルポが出るよ。
    double[] dx = new double[15];
    double[] dy = new double[15];

    /////// boxEvent 
    public void boxEventListener(BoxEvent e) {
        System.out.println("WorkSpace : getBoxEvent --- " + e.getName());
        String str = e.getEvent();
        if (str.equals("PRESSED")) { // 箱が押されたとき
            // 前にするために、描画配列の一番後ろにいれるよ
            e.getBox().getLabel().toFront();
            // ドラッグ動作の前準備
            for (int i = 0; i < touchPointList.size(); i++) {
                if (touchPointList.get(i).getId() == e.getTouchID()) {
                    dx[e.getTouchID()] = (touchPointList.get(i).getSceneX() - e.getX());
                    dy[e.getTouchID()] = (touchPointList.get(i).getSceneY() - e.getY());
                }
            }
        } else if (str.equals("RELEASED")) { // 箱が離されたとき
            for (int i = 0; i < touchPointList.size(); i++) {
                if (touchPointList.get(i).getId() == e.getTouchID()) {
                    if (onTrashBox(touchPointList.get(i).getSceneX(), touchPointList.get(i).getSceneY())) {
                        this.removeBox(e.getBox().getProgramID(), e.getBox());
                    }
                }
            }
        } else if (str.equals("TAPPED")) { // タップされたとき
            
            e.getBox().selecter(); // ボックスの選択
            
        } else if (str.equals("STATIONARY")) { // 箱がタッチされて留まっているとき
            // 何もしない
        } else if (str.equals("MOVED")) { // 箱がドラッグされてたとき
            // 箱を動かそう。
            for (int i = 0; i < touchPointList.size(); i++) {
                if (touchPointList.get(i).getId() == e.getTouchID()) {
                    System.out.println("-----MOVED : " + touchPointList.get(i).getSceneX() + "," + touchPointList.get(i).getSceneY());
                    e.getBox().move(touchPointList.get(i).getSceneX() - dx[e.getTouchID()], touchPointList.get(i).getSceneY() - dy[e.getTouchID()]);
                }
            }
        }
    }

    // オブジェクトの接続につかう変数
    boolean testConnectFlag = false;
    boolean testConnectFlag2 = true;
    double td1 = 0;
    double td2 = 0;

    // 主に2点タッチ接続のコードを書いている。
    // タッチ点はグローバル変数で別に保持している。
    @Override
    public void setMultiTouchPosition(List<TouchPoint> touchList) {
        // 2本指でタッチされたらボックスの接続を行います。
        // このあと、二本指でタッチして、ある程度近づけたらーって感じにする。
        if (touchList.size() == 2) {
            if (testConnectFlag) {
                int counter = 0;
                Box[] connectBox = new Box[2];
                for (int i = 0; i < touchList.size(); i++) {
                    System.out.println("Programming Environment -> Touched : "
                            + touchList.get(i).getId() + " : "
                            + touchList.get(i).getX() + "," + touchList.get(i).getY());
                    for (int j = 0; j < boxList.size(); j++) {
                        for (int k = 0; k < boxList.get(j).size(); k++) {
                            if (touchList.get(i).getId() == boxList.get(j).get(k).getTouchID()) {
                                connectBox[counter] = boxList.get(j).get(k);
                                counter++;
                            }
                        }
                    }
                }
                if (counter == 2) {
                    System.out.println("Touch!!!!!!!!!! ----- 0: " + connectBox[0].getY() + " 1:" + connectBox[1].getY());
                    // 2点間の距離を計算（タッチされた時点での点と現在の点）
                    double x1 = connectBox[0].getX();
                    double x2 = connectBox[1].getX();
                    double y1 = connectBox[0].getY();
                    double y2 = connectBox[1].getY();
                    if (testConnectFlag2) {
                        // どのくらい近づくか？
//                        td1 = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) * 2 / 3;
                        td1 = connectBox[0].getSizeY() + 10; // とりあえず、これでいいや！（ごめんなさい）
                        testConnectFlag2 = false;
                    }
                    td2 = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
                    System.out.println("-----WorkSpace : td1 = " + td1 + ", td2 = " + td2);
                    if (td2 < td1) {
                        // 接続
                        if (connectBox[0].getY() > connectBox[1].getY()) {
                            connectBox(connectBox[1], connectBox[0]);
                        } else {
                            connectBox(connectBox[0], connectBox[1]);
                        }
                        testConnectFlag = false;
                    }
                }
            }
        } else {
            testConnectFlag = true;
            testConnectFlag2 = true;
        }
        this.touchPointList = touchList;
    }

    boolean testFlag = true;

    // デバッグ用キーイベント
    @Override
    public void setKeyEvent(String key) {
//        System.out.println("ProgrammingEnvironment : KeyTyped - " + key);
        switch (key) {
            case "L": // プログラム読み込み
                loadPrograms();
                break;
            case "S": // プログラム保存
                savePrograms();
                break;
            case "A": // 適当なオブジェクトを生成（boxListのdummyとは無関係）
//                String[] strs = {"11", "2", "3", "44", "5"};
                String[] strs = {"single;textField/2/text/:/text", "range;textField/2/text/:/text/ ~ /text/:/text"};
                addBox(new Box("dummy", 500, 300, "input", "choice", strs, " ", " ", new String[0], "TV.png", "", this));
                break;
            case "R": // オブジェクトの削除
                this.removeBox(programIDList.get(0), boxList.get(0).get(0));
                break;
            case " ": //　設定フィールドを出したり閉じたり
                if (testFlag) {
                    for (int i = 0; i < boxList.size(); i++) {
                        for (int j = 0; j < boxList.get(i).size(); j++) {
                            boxList.get(i).get(j).addSetUpBox();
                        }
                    }
                    testFlag = false;
                } else {
                    for (int i = 0; i < boxList.size(); i++) {
                        for (int j = 0; j < boxList.get(i).size(); j++) {
                            boxList.get(i).get(j).removeSetUpBox();
                        }
                    }
                    testFlag = true;
                }
                break;
            default:
                break;
        }
    }
}
