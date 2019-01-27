package com.example.android.sheinnovates;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar mTopToolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    GridView gridview;
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        gridview = (GridView) findViewById(R.id.gridview);
        adapter= new ImageAdapter(this);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            //handling swipe refresh
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        sortImageData();
                        gridview.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        gridview.smoothScrollToPosition(0);
                    }
                }, 2000);
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                Log.e("hereee","okok1");
                calcFilterScore();
                sortImageData();
                Log.e("hereee","okok");
                gridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                gridview.smoothScrollToPosition(0);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            String[] options = new String[]{
                    "OPTION 1",
                    "OPTION 2",
                    "OPTION 3",
                    "OPTION 4",
                    "OPTION 5"
            };

            final boolean[] checkedOptions = new boolean[]{
                    false, // 1
                    false, // 2
                    false, // 3
                    false, // 4
                    false // 5

            };

            final List<String> optionsList = Arrays.asList(options);

            // Set multiple choice items for alert dialog
                /*
                    AlertDialog.Builder setMultiChoiceItems(CharSequence[] items, boolean[]
                    checkedItems, DialogInterface.OnMultiChoiceClickListener listener)
                        Set a list of items to be displayed in the dialog as the content,
                        you will be notified of the selected item via the supplied listener.
                 */
                /*
                    DialogInterface.OnMultiChoiceClickListener
                    public abstract void onClick (DialogInterface dialog, int which, boolean isChecked)

                        This method will be invoked when an item in the dialog is clicked.

                        Parameters
                        dialog The dialog where the selection was made.
                        which The position of the item in the list that was clicked.
                        isChecked True if the click checked the item, else false.
                 */
            builder.setMultiChoiceItems(LaunchActivity.thelabels.getLabelArray(), LaunchActivity.thelabels.getCheckedArray(), new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    LaunchActivity.thelabels.checkindex(which);
                            //[which] = isChecked;

                }
            });

            builder.setCancelable(false);

            builder.setTitle("What are you interested in?");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Notify the current action
                    Toast.makeText(getApplicationContext(),
                            "Okay!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(true);
                    Log.e("hereee","okok2");
                    calcFilterScore();
                    sortImageData();
                    Log.e("hereee","okok");
                    gridview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    gridview.smoothScrollToPosition(0);
                    swipeRefreshLayout.setRefreshing(false);
                    //if (!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
                }
            });

            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do something when click the neutral button
                }
            });

            AlertDialog dialog = builder.create();
            // Display alert dialog
            dialog.show();

            return true;
        }

        if (id == R.id.action_back) {
            Intent intent = new Intent(MainActivity.this, LaunchActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    public void sortImageData() {
        Collections.sort(LaunchActivity.thedata, new Comparator<ImageData>(){
            @Override
            public int compare(ImageData id1, ImageData id2){
                Float id1score = id1.score + id1.filteredscore;
                Float id2score = id2.score + id2.filteredscore;
                return id2score.compareTo(id1score);
            }
        });
        Log.e("donehere","done");
        swipeRefreshLayout.setRefreshing(false);
    }

    public void calcFilterScore(){
        Log.e("calcfilterscore","here");
        ArrayList<String> checkeditems = LaunchActivity.thelabels.getCheckedLabels();
        Log.e("checkedItems", checkeditems.toString());
        for (int i = 0; i < LaunchActivity.thedata.size(); i++){
            float fscore = 0;
            for (Label l : LaunchActivity.thedata.get(i).labels){
                if (checkeditems.contains(l.name)){
                    Log.e("lscore",Float.toString(l.score));
                    fscore = fscore + l.score;
                }
            }
            Log.e("newscore",Float.toString(fscore));
            LaunchActivity.thedata.get(i).filteredscore = fscore;
        }
/*
        for (ImageData imgdata : LaunchActivity.thedata) {
            float fscore = 0;
            for (Label l : imgdata.labels){
                if (checkeditems.contains(l)){
                    fscore = fscore + l.score;
                }
            }
            imgdata.score = imgdata.score + fscore;
        }*/
    }

}
