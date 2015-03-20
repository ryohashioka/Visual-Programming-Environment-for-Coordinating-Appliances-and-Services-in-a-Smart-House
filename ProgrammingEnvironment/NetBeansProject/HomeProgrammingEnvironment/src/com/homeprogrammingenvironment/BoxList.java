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
import javafx.scene.control.ScrollPane;
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
 * @author ryo サーバのデータベースに登録されている機器やサービスのボックスを列挙するパネルを描く。
 *
 *
 */
public class BoxList extends ProgrammingEnvironment {

    // WorkSpaceにオブジェクトを生成するときなどに使う。
    WorkSpace ws;

    final Pane field = new Pane(); // BoxListPanel

    final double sizeX; // ボックスリストの描画サイズX
    final double sizeY; // ボックスリストの描画サイズY

    ArrayList<Box> boxList = new ArrayList<Box>(); // このフィールドに描画されているボックスリスト
    ArrayList<String> categoryList = new ArrayList<String>(); // カテゴリ名のリスト
    ArrayList<Pane> categoryPaneList = new ArrayList<Pane>(); // カテゴリパネルリスト

    // dummyBox これは、ボックスをWorkSpaceにコピーする際にドラッグ&ドロップするボックスです。
    private String[] dummyBoxStr = {"0", "1"}; // 
    Box dummyBox = new Box("dummy", 0, 0, "input", "", dummyBoxStr, "", "", new String[0], "", this);

    // オブジェクトを読み込むファイル
    final String filePath = "/sdcard/HomeProgrammingEnvironment/";
    final String boxListFile = "ApplianceAndServiceList_Demo.xml"; // デモ用
//    final String boxListFile = "ApplianceAndServiceList_Experiment.xml"; // 実験用

    BoxList(double sizeX, double sizeY, WorkSpace workSpace) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.ws = workSpace;
    }

    /* 初期化用関数 特に使っていません。 */
    public void init() {
        // ファイルから読み込んでボックスリストをどんどん追加していく。
//        addBox();
    }

    /* boxlistのパネルを生成 */
    public Pane createPane() {
        int subSizeX = 30; // ボックスリストを閉じたり出したりするボタンの範囲

        // 全体パネル
        field.setPrefSize(sizeX + subSizeX, sizeY);
        field.setStyle("-fx-background-color: #E2EFFF;");

        // ボックスリストフィールドのパネル
        final FlowPane boxListField = new FlowPane();
        boxListField.setPrefWidth(sizeX);
        boxListField.setStyle("-fx-background-color: #E2EFFF;");
        boxListField.setVgap(10);
        boxListField.setHgap(10);

        // ボックス一覧ファイルの読み込みとボックスの追加
        this.load();

        // 読み込んだボックスをカテゴリに追加
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(sizeX, sizeY);
        scrollPane.setStyle("-fx-background-color: #E2EFFF;");
        Accordion accordion = new Accordion();
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

        return field;
    }

    // WorkSpaceにボックスを追加していく（今はboxEventListenerクラスに書いてしまっています。）
//    public void addBoxToWorkSpace() {
//    }
    double dx = 0;
    double dy = 0;

    // ボックスのイベントリスナ
    // ボックスリストのボックスがタッチされると、dummyBoxにそのままコピー。
    // そして、そのdummyBoxがWorkSpace内にあると、生成
    @Override
    public void boxEventListener(BoxEvent e) {
        System.out.println("BoxList : " + mouseX + "," + mouseY + " ----- " + e.getTouchID());
        if (e.getTouchID() == 1) {

            // boxのイベント名
            String str = e.getEvent();

            if (str.equals("PRESSED")) { // 箱が押されたとき
                System.out.println("-----BoxList PRESSED");
                System.out.println("----------" + e.getX() + "," + e.getY());

//            // ドラッグ動作の前準備（dummyBoxに新しい全く同じオブジェクトを代入）
                if (e.getImagePath().equals("")) { // アイコンが無い場合
                    dummyBox = new Box(e.getName(), e.getX(), e.getY(),
                            e.getBoxMode(), e.getGUIName(), e.getItem(),
                            e.getOption1(), e.getOption2(), e.getPlaces(), "", ws
                    );
                } else { // アイコンがある場合
                    dummyBox = new Box(e.getName(), e.getX(), e.getY(), e.getBoxMode(),
                            e.getGUIName(), e.getItem(), e.getOption1(), e.getOption2(),
                            e.getPlaces(), e.getImagePath(), "", ws);
                }

                // 描画
                field.getChildren().add(dummyBox.getLabel());

                dx = e.getBox().getSizeX() / 2;
                dy = e.getBox().getSizeY() / 2;

            } else if (str.equals("RELEASED")) { // 箱が離されたとき

                System.out.println("-----BoxList RELEASED");
                if (mouseX > sizeX) {
                    // ws上で離されたときに追加する。（BoxListより右側はWorkSpade）
                    System.out.println("BoxList : Box is added from boxlist to workspase.");
                    ws.addBox(dummyBox);
                }
                // dummyBoxの描画をやめる
                field.getChildren().remove(dummyBox.getLabel());

            } else if (str.equals("STATIONARY")) { // 箱をその場で維持しているとき
                // 特に何もしません。
                System.out.println("-----BoxList STATIONARY");

            } else if (str.equals("MOVED")) { // 箱がドラッグされたとき

                System.out.println("-----BoxList MOVED");
//            System.out.println("---------- Touch Point : " + dx + "," + dy);

                // 箱を動かす。
                for (int i = 0; i < touchPointList.size(); i++) {
                    if (touchPointList.get(i).getId() == e.getTouchID()) {
                        System.out.println("-----MOVED : " + touchPointList.get(i).getSceneX() + "," + touchPointList.get(i).getSceneY());
                        dummyBox.move(mouseX - dx, mouseY - dy);
                    }
                }
            }
        }
    }

    // xmlファイルからボックスリストを読み込む。
    // そして、ボックスもどんどん追加していく。
    private void load() {
        System.out.println("BoxList : load!");

        try {
            // xmlファイルから読み込む前準備
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
        // そうでなければ、新しくカテゴリを追加
        if (flag) {
            categoryList.add(str);
        } else { // 同じカテゴリ名が存在していたら何もしない。

        }
    }

    // 箱をリストに描画して行く処理（主に最初に呼ばれる。）
    public boolean addBoxesToBoxList() {

        // 別スレッドで１つずつ描画する。
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

        return false;
    }

    @Override
    // マルチタッチを受け付けたくないので、1点だけをとるようにしている。
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
