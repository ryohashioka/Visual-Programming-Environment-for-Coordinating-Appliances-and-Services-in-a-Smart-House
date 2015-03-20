
package com.homeprogrammingenvironment;

import java.util.List;
import javafx.scene.input.TouchPoint;

/**
 *
 * @author ryo
 * このクラスはプログラムフィールドとボックスリストから継承されている。
 * 必要なタッチ位置の取得や実装すべきボックスイベントリスナを用意している。
 * 
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

    // デバッグ用マウス位置を受け取るメソッド（使っていません。
    public void setMousePosition(double x, double y) {
//        System.out.println("Mouse Moved : " + x + "," + y);
        mouseX = x;
        mouseY = y;
    }

    //
    public void setMultiTouchPosition(List<TouchPoint> touchList) {
        this.touchPointList = touchList;
    }
    
    // キーイベント。主にWorkSpaceのデバッグ用に使っている。
    public void setKeyEvent(String key){
//        System.out.println("ProgrammingEnvironment : KeyTyped - " + key);
    }
}
