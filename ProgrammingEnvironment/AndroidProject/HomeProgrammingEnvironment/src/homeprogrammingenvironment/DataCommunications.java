/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.homeprogrammingenvironment;

import de.roderick.weberknecht.WebSocketEventHandler;
import de.roderick.weberknecht.WebSocketMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryo
 */
public class DataCommunications {

//    private static final String SERVER_URI = "ws://127.0.0.1:8081/";
    private static String SERVER_URI;
    boolean connectFlag = false;
    boolean waitingFlag = false;
    String filePath = "/sdcard/HomeProgrammingEnvironment/";
    String saveFileName = "";
    de.roderick.weberknecht.WebSocket websocket;

    final private WorkSpace ws;

    DataCommunications(WorkSpace ws) {
        // ipアドレスをファイルから読み込んで SERVER_URI に代入
        String ip = "127.0.0.1";
        File ipFile = new File("/sdcard/HomeProgrammingEnvironment/ip.txt");
        try {
            FileReader fReader = new FileReader(ipFile);
            BufferedReader br = new BufferedReader(fReader);
            ip = br.readLine();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataCommunications.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataCommunications.class.getName()).log(Level.SEVERE, null, ex);
        }
        SERVER_URI = "ws://"+ ip +":8081/";
        System.out.println("host uri => " + SERVER_URI);
        // 初期化
        this.ws = ws;
        this.init();
    }

    public void init() {
        try {
            websocket = new de.roderick.weberknecht.WebSocket(new URI(SERVER_URI));
            connectFlag = true;
            websocket.setEventHandler(new WebSocketEventHandler() {
                public void onOpen() {
                    System.out.println("--open");
                    recieveDeviceFile();
                    recieveProgramFile();

                }

                public void onClose() {
                    System.out.println("--close");
                }

                public void onPing() {
                }

                public void onPong() {
                }

                @Override
                public void onError(IOException ioe) {
                    waitingFlag = false;
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void onMessage(WebSocketMessage wsm) {
                    String data = wsm.getText();
//						 System.out.println("OnMessage : " + data);
                    if (waitingFlag) {
                        if (data.equals("fileSend")) {
                            System.out.println(" ------ " + data);
                        } else {
                            System.out.println(" ----- " + data);
                            saveFile(data);
                        }
                    } else {
                        if (data.equals("success")) {
                            // こっちが送信して返ってくるメッセージ
                        } else if (data.equals("fileSend")) {
                            System.out.println("update programs on other devices.");
                        } else { // 箱の枠を赤くしたりするよん
                            ws.actionBoxEvent(data);
                        }
                    }
                }
            }
            );
        } catch (URISyntaxException ex) {
            System.out.println("URI ERROR : " + ex.getMessage());
        }
    }

    public void connect() { // exeption 書いた方がいいかな
        if (connectFlag) {
            try {
                websocket.connect();
            } catch (Exception e) {
                connectFlag = false;
            }
        }
    }

    public void disconnect() {
        if (connectFlag) {
            websocket.close();
        }
    }

    public void sendMessage(String msg) {
        if (connectFlag) {
            websocket.send(msg);
        }
    }

    public void sendFile(String filePath) {
        // 通信した日にちを記録して、もし通信できていなければ、ファイルに変更内容を追加していく。
        if (connectFlag) {
            FileReader fr = null;
            BufferedReader br = null;

            websocket.send("fileSend");
            websocket.send("start");
            // connection.sendMessage("fileName,program.xml");
            try {
                fr = new FileReader(filePath);
                br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
                    websocket.send(line);
                }
                // 更新時間の保存
                // new FileAdmin().saveTime();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                    fr.close();
                    websocket.send("end");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        System.out.println("ファイル送信失敗");
    }

    private void saveFile(String text) {
        // System.out.println(getDate() + "," + getTime());
        String saveText = text;
        try {
            File file = new File(filePath + saveFileName);

            if (text.equals("start")) {
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
            } else if (text.equals("end")) {
                this.waitingFlag = false;
            } else if (text.equals("fileSend")) {

            } else {
                FileWriter filewriter = new FileWriter(file, true);
                filewriter.write(saveText);
                filewriter.write("\n");
                filewriter.close();
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public boolean getConnectFlag() {
        return connectFlag;
    }

    // プログラムファイル受信は、起動時，プログラムの保存、読み込みの時に呼ばれる。
    public void recieveProgramFile() {
        // ファイル受信処理
        // サーバに programFilePlease って送って，受信を待つ。
        waitingFlag = true;
        saveFileName = "program_Test.xml";

        this.sendMessage("programFilePlease");

        while (waitingFlag) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(DataCommunications.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // デバイスファイル受信は，起動時に呼ばれる。（後に機器追加機能を実装したらその時にも）
    public void recieveDeviceFile() {
        // ファイル受信処理
        // サーバに deviceFilePlease って送って，受信を待つ。
        waitingFlag = true;
        saveFileName = "ApplianceAndServiceList_Demo.xml";

        this.sendMessage("deviceFilePlease");

        while (waitingFlag) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(DataCommunications.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // ファイル保存については onMessage メソッドに書いている
    }
}
