package com.pixelsOfPromise.uniSim;

public class Timer {
    float time;

    public Timer() {
        this.time = 0;
    }

    public void add(float deltaTime) {
        time += deltaTime;
    }

    public float getTime(){
        return (float) Math.round(time * 10) / 10;
    }

    public int getMinutes() {
        return (int) Math.floor(getTime() / 60);
    }

    public float getSeconds() {
        return (float) Math.round((getTime() % 60) * 10)/10;
    }

    public String getTimeUI(){
        if (getMinutes() < 1) {
            return getSeconds() + "s";
        }
        else {
            return getMinutes() + "m" + getSeconds() + "s";
        }
    }
}
