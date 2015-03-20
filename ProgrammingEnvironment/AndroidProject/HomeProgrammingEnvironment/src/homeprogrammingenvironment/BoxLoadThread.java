/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.homeprogrammingenvironment;

/**
 *
 * @author ryo
 */
public class BoxLoadThread extends Thread {
    boolean flag = true;
    
    BoxList boxList;
    
    BoxLoadThread(BoxList boxList){
        this.boxList = boxList;
    }

    public void run() {
        while (flag) {
            flag = boxList.addBoxesToBoxList();
            try {
                this.sleep(300);
            } catch (InterruptedException ex) {
                System.out.println("sleep error");
            }
        }
    }
}
