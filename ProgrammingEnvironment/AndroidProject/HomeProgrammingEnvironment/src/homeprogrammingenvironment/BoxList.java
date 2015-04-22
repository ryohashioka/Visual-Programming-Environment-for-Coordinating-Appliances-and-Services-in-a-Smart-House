/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package com.homeprogrammingenvironment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author ryo メモ： カテゴリーの分だけパネルを作る 各パネルに対応したボックスを入れる。 各パネルはFlowPaneで実装
 * 右のパネルをしまうパネルにはカテゴリー一覧を入れる。 右のパネルはタッチしたらそのカテゴリが選択されて、ムーブすると動かす事が出来る。
 *
 *
 */
public class BoxList extends ProgrammingEnvironment {

    WorkSpace ws;

    final Pane field = new Pane();

    final double sizeX;
    final double sizeY;

//    ArrayList<Box> inputBoxList = new ArrayList<Box>();
//    ArrayList<Box> outputBoxList = new ArrayList<Box>(); // リストは結局使わない。
    ArrayList<Box> boxList = new ArrayList<Box>();
    ArrayList<String> categoryList = new ArrayList<String>();
    ArrayList<Pane> categoryPaneList = new ArrayList<Pane>();
    private String[] dummyBoxStr = {"0", "1"};
    Box dummyBox = new Box("dummy", 0, 0, "input", "", dummyBoxStr, "", "", new String[0], "", this); // コピー用に使ってます。

    final String filePath = "/sdcard/HomeProgrammingEnvironment/";
    final String boxListFile = "ApplianceAndServiceList_Demo.xml";
//    final String boxListFile = "ApplianceAndServiceList_Experiment.xml";

    BoxList(double sizeX, double sizeY, WorkSpace workSpace) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.ws = workSpace;
    }

    public void init() {
        // ファイルから読み込んでボックスリストをどんどん追加していく。
//        addBox();
    }

    // boxlistのパネルを生成して
    public Pane createPane() {
        int subSizeX = 30;
        // 全体パネル
        field.setPrefSize(sizeX + subSizeX, sizeY);
        field.setStyle("-fx-background-color: #E2EFFF;");

        // ボックスリストフィールドのパネル
        // addCatepane(All)って感じでする。
        final FlowPane boxListField = new FlowPane();
//        boxListField.setPrefSize(sizeX, sizeY);
        boxListField.setPrefWidth(sizeX);
        boxListField.setStyle("-fx-background-color: #E2EFFF;");
        boxListField.setVgap(10);
        boxListField.setHgap(10);

        // ファイルの読み込みとボックスの追加だよい
        this.load();

        // カテゴリに追加とかしていく
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(sizeX, sizeY);
        scrollPane.setStyle("-fx-background-color: #E2EFFF;");
        Accordion accordion = new Accordion();
//        accordion.setPrefHeight(20);
        scrollPane.setContent(accordion);

        field.getChildren().add(scrollPane);

        for (int i = 0; i < categoryList.size(); i++) {

            FlowPane catepane = new FlowPane();
            catepane.setPrefWidth(sizeX);
//            catepane.setMinHeight(sizeY);
            catepane.setStyle("-fx-background-color: #E2EFFF;");
            catepane.setVgap(10);
            catepane.setHgap(10);
            TitledPane t1 = new TitledPane(categoryList.get(i), catepane);
            t1.setFont(new Font(24));
            accordion.getPanes().add(t1);
            categoryPaneList.add(catepane);
        }

        // boxListFieldを閉まったり出したりするためのパネル
        // クリックすると出たり閉じたり。
        // 後でアニメーションつける！
        // 矢印もいい感じに！
        final BorderPane subBoxListField = new BorderPane();
        subBoxListField.setPrefSize(subSizeX, sizeY);
        subBoxListField.setTranslateX(sizeX);
        subBoxListField.setStyle("-fx-background-color: #E5E5E5;");

        final Text sblfText = new Text();
        sblfText.setText("<");

        subBoxListField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            Boolean flag = false;

            @Override
            public void handle(MouseEvent t) {
//                System.out.println("clicked");
                if (flag) { // 出す！
                    field.setTranslateX(0);
                    sblfText.setText("<");
                    flag = false;
                } else { // しまう！
                    field.setTranslateX(-sizeX);
                    sblfText.setText(">");
                    flag = true;
                }
            }

        });

        subBoxListField.setCenter(sblfText);

        field.getChildren().add(subBoxListField);
//        field.getChildren().add(boxListField);

        return field;
    }

    // Wsにボックスを追加していきましょう
    public void addBoxToWorkSpace() {
    }

    double dx = 0;
    double dy = 0;

    // ボックスのイベントリスナ
    @Override
    public void boxEventListener(BoxEvent e) {
        System.out.println("BoxList : " + mouseX + "," + mouseY + " ----- " + e.getTouchID());
        if (e.getTouchID() == 1) {
            String str = e.getEvent();
            if (str.equals("PRESSED")) { // 箱が押されたとき
                System.out.println("-----BoxList PRESSED");
                System.out.println("----------" + e.getX() + "," + e.getY());
//            // ドラッグ動作の前準備
                if (e.getImagePath().equals("")) {
                    dummyBox = new Box(e.getName(), e.getX(), e.getY(),
                            e.getBoxMode(), e.getGUIName(), e.getItem(),
                            e.getOption1(), e.getOption2(), e.getPlaces(), "", ws
                    );
                    // 子がいたら
                    for (int i = 0; i < e.getItem().length; i++) {
                        System.out.println("items:" + e.getItem()[i]);
                    }
                } else {
                    dummyBox = new Box(e.getName(), e.getX(), e.getY(), e.getBoxMode(),
                            e.getGUIName(), e.getItem(), e.getOption1(), e.getOption2(),
                            e.getPlaces(), e.getImagePath(), "", ws);
                    // 子がいたら
                    for (int i = 0; i < e.getItem().length; i++) {
                        System.out.println("items:" + e.getItem()[i]);
                    }
                }
                field.getChildren().add(dummyBox.getLabel());
//                dx = (mouseX - e.getX());
//                dy = (mouseY - e.getY());
                dx = e.getBox().getSizeX() / 2;
                dy = e.getBox().getSizeY() / 2;
            } else if (str.equals("RELEASED")) { // 箱が離されたとき
                System.out.println("-----BoxList RELEASED");
                if (mouseX > sizeX) {
                    // ws上で離されたときに追加する。
                    System.out.println("BoxList : Box is added from boxlist to workspase.");
                    ws.addBox(dummyBox);
                }
                field.getChildren().remove(dummyBox.getLabel());
            } else if (str.equals("STATIONARY")) { // 箱がクリックされたとき
                System.out.println("-----BoxList STATIONARY");
            } else if (str.equals("MOVED")) { // 箱がドラッグされてたとき
                System.out.println("-----BoxList MOVED");
//            System.out.println("---------- Touch Point : " + dx + "," + dy);
                // 箱を動かそう。
                // ほんまは箱をコピーして動かす。
                for (int i = 0; i < touchPointList.size(); i++) {
                    if (touchPointList.get(i).getId() == e.getTouchID()) {
                        System.out.println("-----MOVED : " + touchPointList.get(i).getSceneX() + "," + touchPointList.get(i).getSceneY());
                        dummyBox.move(mouseX - dx, mouseY - dy);
                    }
                }
//            dummyBox.move(mouseX - dx, mouseY - dy);
            }
        }
    }

    // xmlファイルからボックスリストを読み込む。
    // そして、ボックスもどんどん追加していくよ。
    /*  ここを何回かに分けてやって，読み込みー表示、読み込みー表示って感じでやってみたら？
     * 準備 -> 子ノード10個読み込み -> 描画 -> 読み込み終了 => 準備へ戻る
     * 子ノードの読み込み数だけを保持しといて，lengthまで続ける
     * 1個ずつ描画して行ったら？
     */
    private void load() {
        System.out.println("BoxList : load!");

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
            String[] str = new String[children.getLength()];

            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    str[i] = childElement.getTagName();
                    // 箱の追加とカテゴリの追加
                    this.addBox(childElement.getAttribute("name"), 0, 0, childElement.getTagName(),
                            childElement.getAttribute("icon"), childElement.getAttribute("category"),
                            childElement.getAttribute("gui"), childElement.getAttribute("set"),
                            childElement.getAttribute("option"), childElement.getAttribute("place"));
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

    // BoxListFieldにボックスを追加します。
    // imgが無い場合は imgPath = null にしてください。
    public void addBox(String name, int x, int y, String boxMode, String imgPath,
            String category, String guiName, String itemStr, String optionStr, String placeStr) {

        String[] item = itemStr.split(",");
        String[] option = optionStr.split(",");
        String[] place = placeStr.split(",");

        boxList.add(new Box(name, x, y, boxMode, guiName, item, option[0], option[1], place, imgPath, category, this));
//        addCategory("All");
        addCategory(category);
//        }
    }

    // カテゴリを追加
    private void addCategory(String str) {
        // すでにカテゴリがある場合は、そのパネルに追加する
        int num = 0;
        boolean flag = true;
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).equals(str)) {
                flag = false;
                num = i;
                break;
            } else {
            }
        }
        if (flag) {
            categoryList.add(str);
//            // ボックスリストフィールドのパネル
//            // スクロールしたいけど、うまくいかないので、後回しにしてる
////            ScrollPane scrollPane = new ScrollPane();
////            scrollPane.setPrefSize(sizeX, sizeY);
//            FlowPane catepane = new FlowPane();
////            catepane.setPrefSize(sizeX, sizeY);
//            catepane.setPrefWidth(sizeX);
////            catepane.setMinHeight(sizeY);
//            catepane.setStyle("-fx-background-color: #E2EFFF;");
//            catepane.setVgap(10);
//            catepane.setHgap(10);
//            catepane.getChildren().add(box.getLabel());
//            System.out.println("FPane : " + catepane.getPrefWrapLength());
////            scrollPane.setContent(catepane);
////            categoryPaneList.add(catepane);
////            TitledPane t1 = new TitledPane(str, catepane);
////            accordion.getPanes().add(t1);
////            testfpane.getChildren().add(box.getLabel());
////            System.out.println("BoxList : create - " + categoryList.get(categoryList.size()-1) + " - " + box.name);
////            System.out.println(" カテゴリ内の箱の数 ---: "+categoryPaneList.get(num).getChildren().size());
        } else {
//            categoryPaneList.get(num).getChildren().add(box.getLabel());
//            testfpane.getChildren().add(box.getLabel());
//            System.out.println("BoxList : add - " + categoryList.get(num) + " - " + box.name);
//            System.out.println(" カテゴリ内の箱の数 ---: "+categoryPaneList.get(num).getChildren().size());
        }
    }

//    int counter = 0;
    // 箱をリストに描画して行く
    public boolean addBoxesToBoxList() {

        Task<Void> task = new Task<Void>() {
            int counter = 0;

            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < boxList.size(); i++) {
                    if (isCancelled()) {
                        break;
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            int num = categoryList.indexOf(boxList.get(counter).category);
                            System.out.println(categoryList.get(num));
                            categoryPaneList.get(num).getChildren().add(boxList.get(counter).getLabel());
                            counter++;
                        }
                    });
                    Thread.sleep(30);
                }
                System.out.println("finish");
                return null;
            }
        };

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(task);

//        if (counter < boxList.size()) {
//            int num = categoryList.indexOf(boxList.get(counter).category);
//            categoryPaneList.get(num).getChildren().add(boxList.get(counter).getLabel());
//            counter++;
//            return true;
//        } else {
//            return false;
//        }
        return false;
    }

    @Override
    public void setMultiTouchPosition(List<TouchPoint> touchList) {
        this.touchPointList = touchList;
        System.out.println();
        for (int i = 0; i < touchList.size(); i++) {
            if (touchList.get(i).getId() == 1) {
                mouseX = touchList.get(i).getSceneX();
                mouseY = touchList.get(i).getSceneY();
            }
        }
    }
}
