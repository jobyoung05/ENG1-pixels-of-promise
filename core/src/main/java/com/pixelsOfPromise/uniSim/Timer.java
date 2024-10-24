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
        return time;
    }

}
