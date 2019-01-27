package com.example.android.sheinnovates;

import java.util.ArrayList;

public class CheckLabels {
    ArrayList<String> labelnames;
    ArrayList<Boolean> labelbools;

    public CheckLabels() {
        labelnames = new ArrayList<String>();
        labelbools = new ArrayList<Boolean>();
    }

    public void addLabel(String l){
        if (!labelnames.contains(l)){
            labelnames.add(l);
            labelbools.add(false);
        }
    }


    public boolean isChecked(String l){
        return labelbools.get(labelnames.indexOf(l));
    }

    public void toggleCheck(String l){
        int idx = labelnames.indexOf(l);
        labelbools.set(idx, !labelbools.get(idx));
    }

    public void checkindex(int i){
        labelbools.set(i, !labelbools.get(i));
    }

    public String[] getLabelArray() {
        String[] res = new String[labelnames.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = labelnames.get(i);
        }
        return res;

    }

    public boolean[] getCheckedArray() {
        boolean[] res = new boolean[labelbools.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = labelbools.get(i);
        }
        return res;
    }

    public ArrayList<String> getCheckedLabels() {
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i < labelbools.size(); i++){
            if (labelbools.get(i)){
                res.add(labelnames.get(i));
            }
        }
        return res;
    }
}
