package com.solidpeakdevelopment.tuneyourlada;

import java.text.DecimalFormat;

// Object holding all tuning canvas properties
public class CanvasState {
    String bodyColor = "red";
    int wheels = 0;
    int windowTint = 0;
    int spoiler = 0;
    int bodykit = 0;
    double suspensionHeight = 0.03;
    double heightText  = 0;

    public String getBodyColor() {
        return bodyColor;
    }

    public void setBodyColor(String bodyColor) {
        this.bodyColor = bodyColor;
    }

    public int getWheels() {
        return wheels;
    }

    public void setWheels(int wheels) {
        this.wheels = wheels;
    }

    public int getWindowTint() {
        return windowTint;
    }

    public void setWindowTint(int windowTint) {
        this.windowTint = windowTint;
    }

    public int getSpoiler() {
        return spoiler;
    }

    public void setSpoiler(int spoiler) {
        this.spoiler = spoiler;
    }

    public int getBodykit() {
        return bodykit;
    }

    public void setBodykit(int bodykit) {
        this.bodykit = bodykit;
    }

    public double getSuspensionHeight() {
        return suspensionHeight;
    }

    public void plusSuspension() {
        if (this.suspensionHeight < 0.049) {
            this.suspensionHeight = suspensionHeight + 0.005;
        }
    }

    public void minusSuspension() {
        if (this.suspensionHeight > 0.006) {
            this.suspensionHeight = suspensionHeight - 0.005;
        }
    }


    public void setLowSuspension(){
        this.suspensionHeight = 0.005;
    }

    public void setMediumSuspension(){
        this.suspensionHeight = 0.03;
    }

    public void setHighSuspension(){
        this.suspensionHeight = 0.05;
    }


    public double getHeightText() {
        this.heightText = suspensionHeight * 1000;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Float.valueOf(decimalFormat.format(heightText));
    }

}