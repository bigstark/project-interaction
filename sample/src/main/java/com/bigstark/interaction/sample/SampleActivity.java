package com.bigstark.interaction.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bigstark.interaction.CircleLoadingView;
import com.bigstark.interaction.RandomTileLoadingView;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        CircleLoadingView clv = (CircleLoadingView) findViewById(R.id.circle_loading_view);
        clv.setRotationDuration(500);
        clv.setInterval(200);
        clv.start();


        RandomTileLoadingView rti = (RandomTileLoadingView) findViewById(R.id.random_tile_loading_view);
        rti.start();
    }
}
