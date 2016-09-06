package com.jniu.game2048;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextView tvScore;
    private GameView gameView;

    public static MainActivity mainActivity = null;

    public MainActivity(){
        mainActivity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvScore = (TextView) findViewById(R.id.tvScore);
        gameView = (GameView) findViewById(R.id.gameView);
//        gameView.getLayoutParams().height = gameView.getWidth();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        gameView.getLayoutParams().height = (int) (metrics.widthPixels-getResources().getDimension(R.dimen.activity_horizontal_margin)*2);
    }


    public static MainActivity getMainActivity() {
        return mainActivity;
    }


    public void showScore(){
        tvScore.setText(gameView.getScore().get(GameView.SCORE_NUM)+"");
    }

}
