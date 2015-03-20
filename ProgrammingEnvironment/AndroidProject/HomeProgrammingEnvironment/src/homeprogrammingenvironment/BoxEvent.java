/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.homeprogrammingenvironment;

import javafx.scene.Node;

/**
 *
 * @author ryo
 */
public class BoxEvent {

    private Box box;
    private String eventName; // タッチ、リリースとか
    private String name;
    private double x;
    private double y;
    private String imagePath;
    private String boxMode;
    private int touchID;
    private String guiName;
    private String[] item;
    private String option1, option2;
    private String[] places;
//    private Node node;

    BoxEvent(String name, double x, double y, String boxMode, String imgPath,
            String guiName, String[] item, String option1, String option2, String[] places,
            String eventName, int touchID, Box box) {
        this.name = name;
        this.x = x;
        this.y = y;
//        this.node = node;
        this.boxMode = boxMode;
        this.imagePath = imgPath;
        this.eventName = eventName;
        this.box = box;
        this.touchID = touchID;
        this.guiName = guiName;
        this.item = item;
        this.option1 = option1;
        this.option2 = option2;
        this.places = places;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public String getEvent() {
        return eventName;
    }

    public String getBoxMode() {
        return boxMode;
    }
//    public Node getImage(){
//        return node;
//    }

    public String getImagePath() {
        return imagePath;
    }

    public Box getBox() {
        return box;
    }

    public int getTouchID() {
        return touchID;
    }

    public String getGUIName() {
        return guiName;
    }

    public String[] getItem() {
        return item;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String[] getPlaces() {
        return places;
    }
}
