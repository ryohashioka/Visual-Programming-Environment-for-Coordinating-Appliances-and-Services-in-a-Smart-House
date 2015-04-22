/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package com.homeprogrammingenvironment;

import java.util.List;
import javafx.scene.input.TouchPoint;

/**
 *
 * @author ryo
 */
public class ProgrammingEnvironment {

    List<TouchPoint> touchPointList;

    public double mouseX = 0;
    public double mouseY = 0;

    ProgrammingEnvironment() {
    }

    // ボックスのイベントリスナ
    public void boxEventListener(BoxEvent e) {
        System.out.println("BoxEvent : " + e.getName() + ", " + e.getEvent() + e.getX() + e.getY());
    }

    // 使ってないよ
    public void setMousePosition(double x, double y) {
//        System.out.println("Mouse Moved : " + x + "," + y);
        mouseX = x;
        mouseY = y;
    }

    public void setMultiTouchPosition(List<TouchPoint> touchList) {
        this.touchPointList = touchList;
    }
    
    // なんちゃってコールバック
    // キーイベントだけど、これはとりあえず。最終的には保存とかはコマンドCとかでやるからね！
    public void setKeyEvent(String key){
//        System.out.println("ProgrammingEnvironment : KeyTyped - " + key);
    }
}
