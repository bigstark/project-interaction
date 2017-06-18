package com.bigstark.interaction.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bigstark.interaction.CircleLoadingCompleteView;
import com.bigstark.interaction.CircleLoadingView;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        CircleLoadingView clv = (CircleLoadingView) findViewById(R.id.circle_loading_view);
        clv.setRotationDuration(500);
        clv.setInterval(200);
        clv.start();


        CircleLoadingCompleteView clcv = (CircleLoadingCompleteView) findViewById(R.id.circle_loading_completion_view);
        clcv.start();

        clcv.postDelayed(new Runnable() {
            @Override
            public void run() {
                CircleLoadingCompleteView clcv = (CircleLoadingCompleteView) findViewById(R.id.circle_loading_completion_view);
                clcv.setLoadingComplete(true);
            }
        }, 5000);
    }
}
