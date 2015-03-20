
package com.homeprogrammingenvironment;

/**
 *
 * @author ryo
 * 箱の接続処理を行なう。
 * 
 */
public class BoxConnecter {

    int strokeWidth = 3;

    BoxConnecter() {
    }

    public MyLine connect(Box box1, Box box2) {
//        System.out.println("connect : " + x1 + y1 + x2 + y2);

        String box1mode = box1.getBoxMode();
        String box2mode = box2.getBoxMode();        
        // もし、box1が inputで box2がoutputなら、box2が下に
        if (box1mode.equals("input") && box2mode.equals("output")) {
            MyLine line = createLine(box1.getX(), box1.getY(), box1.getSizeX(), box1.getSizeY()
                    , box2.getX(), box2.getY(), box2.getSizeX(), box2.getSizeY());
            box1.connectedOut(line, box2);
            box2.connectedIn(line, box1);
            return line;
        } else if (box1mode.equals("output") && box2mode.equals("input")){
            // もし、box1がoutputで box2が inputなら、box1が下に
            MyLine line = createLine(box2.getX(), box2.getY(), box2.getSizeX(), box2.getSizeY()
                    , box1.getX(), box1.getY(), box1.getSizeX(), box1.getSizeY());
            box2.connectedOut(line, box1);
            box1.connectedIn(line, box2);
            return line;
        } else if (box1mode.equals("output") && box2mode.equals("output")){
            // 両方outputなら、接続しない。
            MyLine line = null;
            return line;
        } else {
            MyLine line = createLine(box1.getX(), box1.getY(), box1.getSizeX(), box1.getSizeY()
                    , box2.getX(), box2.getY(), box2.getSizeX(), box2.getSizeY());
            box1.connectedOut(line, box2);
            box2.connectedIn(line, box1);
            return line;
        }
    }

    public void disconnect() {
    }

    private MyLine createLine(double box1x, double box1y, double box1Width, double box1Height,
        double box2x, double box2y, double box2Width, double box2Height) {
        double x1 = box1x + box1Width / 2;
        double y1 = box1y + box1Height;
        double x2 = box2x + box2Width / 2;
        double y2 = box2y;
        MyLine l = new MyLine(x1,y1,x2,y2);
//        l.setStrokeWidth(strokeWidth);
        
        return l;
    }

    // わたされたボックスの相手を返す
    public void getAnotherBox(Box box) {
        return;
    }

    // 指定したラインを得る
    public void getLine(int index) {
        return;
    }

}
