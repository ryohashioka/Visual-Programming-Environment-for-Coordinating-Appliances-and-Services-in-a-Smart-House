/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package com.homeprogrammingenvironment;

import de.roderick.weberknecht.WebSocketEventHandler;
import de.roderick.weberknecht.WebSocketMessage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author ryo
 * websocket関連
 * 接続、切断、テキストメッセージの送信、ファイルの送信などのメソッドがあるよ。
 */
public class WebSocketManager {

    de.roderick.weberknecht.WebSocket websocket;
    // サーバ機とかipとか特に決めてないし、ネットワーク接続が変わったらここを変えてね。
    final String wsuri = "ws://localhost:8081/"; //"ws://192.168.11.6:8081/"; 

    WebSocketManager() {
    }

    // 初期設定を行っています。
    // あと、websocketのイベントハンドラも作ってるよ。
    public void setup() {
        try {
            websocket = new de.roderick.weberknecht.WebSocket(new URI(wsuri));
            websocket.setEventHandler(new WebSocketEventHandler() {
                public void onOpen() {
                    System.out.println("--open");
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
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void onMessage(WebSocketMessage wsm) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
        } catch (URISyntaxException ex) {
//            Logger.getLogger(WebSocketTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // サーバと接続します。
    public void connect() {
        websocket.connect();
    }

    // サーバと切断します。
    public void disconnect() {
        websocket.close();
    }

    // もらった変数をサーバに送ります
    public void sendMessage(String message) {
        websocket.send(message);
    }

    // ファイルを一行ずつサーバに送ります。
    public void sendFile(File file) {
    }

}
