package com.example.android.sheinnovates;

import android.net.Uri;

import java.util.ArrayList;

public class ImageData {
    Uri uri;
    boolean processed;
    Float score;
    Float filteredscore;
    ArrayList<Label> labels;
    public ImageData(Uri uri){
        this.uri = uri;
        this.processed = false;
        this.score = (float)0;
        this.filteredscore = (float)0;
        this.labels = new ArrayList<Label>();
    }

    public void addLabel(Label l){
        this.labels.add(l);
    }

    public String getLabelsText() {
        String res = "";
        for (Label l : labels){
            res = res + l.name + ", ";
        }
        if (res.length() < 2) return res;
        res = res.substring(0,res.length() -2);
        return res;
    }

    public String getInfoText() {
        String res = "";
        res = getLabelsText() + '\n';
        res = res + "Score: "+ score.toString();
        return res;
    }


}
