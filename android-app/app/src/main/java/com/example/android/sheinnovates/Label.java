package com.example.android.sheinnovates;

public class Label {
    String name;
    Float score;
    public Label(String n, Float s){
        this.name = n;
        this.score = s;
    }

    public String getName() {
        return this.name;
    }

    public Float getScore() {
        return this.score;
    }
}
