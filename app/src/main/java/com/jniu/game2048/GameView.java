package com.jniu.game2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackie on 2016/9/2.
 */
public class GameView extends GridLayout {

    //设定单行卡片数量
    private static final int CARD_COUNT = 4;

    //方向
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    private SparseIntArray score = new SparseIntArray();
    public static final int SCORE_NUM = 0;

    public SparseIntArray getScore() {
        return score;
    }

    public void clearScore(){
        score.clear();
        score.append(SCORE_NUM,0);
        MainActivity.getMainActivity().showScore();

    }

    public void addScore(int score){
        this.score.append(SCORE_NUM,this.score.get(SCORE_NUM)+score);
        MainActivity.getMainActivity().showScore();
    }

    //存储卡片数组
    private Card[][] cardMap = new Card[CARD_COUNT][CARD_COUNT];

    private List<Point> emptyPoints = new ArrayList<Point>();

    public GameView(Context context) {
        super(context);

        initGameView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initGameView();
    }


    //初始化
    private void initGameView(){

        setColumnCount(CARD_COUNT);

        setBackgroundColor(0xffffffff);

        setOnTouchListener(new OnTouchListener() {

            private float startX,startY,offsetX,offsetY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                //判断操作方向
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = motionEvent.getX();
                        startY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = motionEvent.getX()-startX;
                        offsetY = motionEvent.getY()-startY;

                        if(Math.abs(offsetX)>Math.abs(offsetY)){
                            if(offsetX<-5){
                                swipeLeft();
                                System.out.println("left");
                            }else if(offsetX>5){
                                swipeRight();
                                System.out.println("right");
                            }
                        }else{
                            if(offsetY<-5){
                                swipeUp();
                                System.out.println("up");
                            }else if(offsetY>5){
                                swipeDown();
                                System.out.println("down");
                            }
                        }
                        break;
                }

                return true;
            }
        });
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int cardWidth = (Math.min(w,h))/CARD_COUNT;

        addCards(cardWidth,cardWidth);

        startGame();
    }

    private void startGame(){

        clearScore();

        for(int y = 0 ;y<CARD_COUNT;y++){
            for(int x = 0;x<CARD_COUNT;x++){
                cardMap[x][y].setNumber(0);
            }
        }

        addRandomNum();
        addRandomNum();
    }

    private void gameOver(){

        Boolean isOver = true;

        ALL:
        for(int y = 0;y<CARD_COUNT;y++){
            for(int x= 0 ;x<CARD_COUNT;x++){
                if(cardMap[x][y].getNumber()==0||
                        (x>0&&cardMap[x][y].equals(cardMap[x-1][y]))||
                        (x<CARD_COUNT-1&&cardMap[x][y].equals(cardMap[x+1][y]))||
                        (y>0&&cardMap[x][y].equals(cardMap[x][y-1]))||
                        (y<CARD_COUNT-1&&cardMap[x][y].equals(cardMap[x][y+1]))){
                    isOver = false;
                    break ALL;
                }
            }
        }

        if(isOver){
            new AlertDialog.Builder(getContext()).setTitle("游戏结束")
                    .setMessage("您的得分是："+score.get(0))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
            startGame();
        }
    }

    private void addRandomNum(){

        emptyPoints.clear();

        for(int y = 0;y<CARD_COUNT;y++){
            for(int x = 0;x<CARD_COUNT;x++){
                if(cardMap[x][y].getNumber()<=0){
                    emptyPoints.add(new Point(x,y));
                }
            }
        }

        Point p = emptyPoints.remove((int)(Math.random()*emptyPoints.size()));
        cardMap[p.x][p.y].setNumber(Math.random()>0.1?2:4);
    }

    private void addCards(int cardWidth,int cardHeight){
        Card c ;

        for(int y = 0;y<CARD_COUNT;y++){
            for(int x = 0;x<CARD_COUNT;x++){
                c = new Card(getContext());
                c.setNumber(0);

                addView(c,cardWidth,cardHeight);
                cardMap[x][y] = c;
            }
        }
    }

    /*
        * 滑动后方向操作
        * */
    private void swipeLeft(){

        Boolean merga = false;

        for(int y = 0;y<CARD_COUNT;y++){
            for(int x = 0;x<CARD_COUNT;x++){

                for(int x1 = x+1;x1<CARD_COUNT;x1++){
                    if(cardMap[x1][y].getNumber()>0){
                        if(cardMap[x][y].getNumber()<=0){
                            cardMap[x][y].setNumber(cardMap[x1][y].getNumber());
                            cardMap[x1][y].setNumber(0);
                            cardMap[x][y].startAnimation(cardMoveAnimation(LEFT));
                            x--;
                            merga = true;
                        }else if(cardMap[x][y].equals(cardMap[x1][y])){

                            Boolean canChange = true;
                            for(int i = 1;i<x1-x;i++){
                                if(cardMap[x+i][y].getNumber()>0){
                                    canChange = false;
                                }
                            }

                            if(canChange){
                                cardMap[x][y].setNumber(cardMap[x][y].getNumber()*2);
                                cardMap[x1][y].setNumber(0);
                                cardMap[x][y].startAnimation(cardMoveAnimation(LEFT));
                                addScore(cardMap[x][y].getNumber());
                                merga = true;
                            }
                        }
                        break;
                    }
                }
            }
        }

        if(merga){
            addRandomNum();
            gameOver();
        }
    }

    private void swipeRight(){

        Boolean merga = false;
        for(int y = 0;y<CARD_COUNT;y++){
            for(int x = CARD_COUNT-1;x>=0;x--){

                for(int x1 = x-1;x1>=0;x1--){
                    if(cardMap[x1][y].getNumber()>0){
                        if(cardMap[x][y].getNumber()<=0){
                            cardMap[x][y].setNumber(cardMap[x1][y].getNumber());
                            cardMap[x1][y].setNumber(0);
                            cardMap[x][y].startAnimation(cardMoveAnimation(RIGHT));
                            x++;
                            merga = true;
                        }else if(cardMap[x][y].equals(cardMap[x1][y])){
                            Boolean canChange = true;
                            for(int i = 1;i<x-x1;i++){
                                if(cardMap[x-i][y].getNumber()>0){
                                    canChange = false;
                                }
                            }

                            if(canChange){
                                cardMap[x][y].setNumber(cardMap[x][y].getNumber()*2);
                                cardMap[x1][y].setNumber(0);
                                cardMap[x][y].startAnimation(cardMoveAnimation(RIGHT));
                                addScore(cardMap[x][y].getNumber());
                                merga = true;
                            }
                        }
                        break;
                    }
                }
            }
        }
        if(merga){
            addRandomNum();
            gameOver();
        }
    }

    private void swipeUp(){

        Boolean merga = false;
        for(int x = 0;x<CARD_COUNT;x++){
            for(int y = 0;y<CARD_COUNT;y++){

                for(int y1 = y+1;y1<CARD_COUNT;y1++){
                    if(cardMap[x][y1].getNumber()>0){
                        if(cardMap[x][y].getNumber()<=0){
//                            cardMap[x][y1].startAnimation(cardMoveAnimation(MOVE_TO_Y,y-y1));
                            cardMap[x][y].setNumber(cardMap[x][y1].getNumber());
                            cardMap[x][y1].setNumber(0);
                            cardMap[x][y].startAnimation(cardMoveAnimation(UP));
                            y--;
                            merga = true;
                        }else if(cardMap[x][y].equals(cardMap[x][y1])){

                            Boolean canChange = true;
                            for(int i = 1;i<y1-y;i++){
                                if(cardMap[x][y+i].getNumber()>0){
                                    canChange = false;
                                }
                            }

                            if(canChange){
//                                cardMap[x][y1].startAnimation(cardMoveAnimation(MOVE_TO_Y,y-y1));
                                cardMap[x][y].setNumber(cardMap[x][y].getNumber()*2);
                                cardMap[x][y1].setNumber(0);
                                cardMap[x][y].startAnimation(cardMoveAnimation(UP));
                                addScore(cardMap[x][y].getNumber());
                                merga = true;
                            }
                        }
                        break;
                    }
                }
            }
        }

        if(merga){
            addRandomNum();
            gameOver();
        }
    }

    private void swipeDown(){

        Boolean merga = false;

        for(int x = 0;x<CARD_COUNT;x++){
            for(int y = CARD_COUNT-1;y>=0;y--){

                for(int y1 = y-1;y1>=0;y1--){
                    if(cardMap[x][y1].getNumber()>0){
                        if(cardMap[x][y].getNumber()<=0){
//                            cardMap[x][y1].startAnimation(cardMoveAnimation(MOVE_TO_Y,y-y1));
                            cardMap[x][y].setNumber(cardMap[x][y1].getNumber());
                            cardMap[x][y1].setNumber(0);
                            cardMap[x][y].startAnimation(cardMoveAnimation(DOWN));

                            y++;

                            merga = true;
                        }else if(cardMap[x][y].equals(cardMap[x][y1])){

                            Boolean canChange = true;
                            for(int i = 1;i<y-y1;i++){
                                if(cardMap[x][y-i].getNumber()>0){
                                    canChange = false;
                                }
                            }

                            if(canChange){
//                                cardMap[x][y1].startAnimation(cardMoveAnimation(MOVE_TO_Y,y-y1));
                                cardMap[x][y].setNumber(cardMap[x][y].getNumber()*2);
                                cardMap[x][y1].setNumber(0);
                                cardMap[x][y].startAnimation(cardMoveAnimation(DOWN));
                                addScore(cardMap[x][y].getNumber());
                                merga = true;
                            }
                        }
                        break;

                    }
                }
            }
        }
        if(merga){
            addRandomNum();
            gameOver();
        }
    }


    private Animation cardMoveAnimation(int moveTo){
        TranslateAnimation leftTranslate,rightTranslate,upTranslate,downTranslate;

        switch(moveTo){
            case LEFT:
                AnimationSet leftSet = new AnimationSet(true);

                leftTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0.2f,Animation.RELATIVE_TO_SELF,0F,Animation.RELATIVE_TO_SELF,0F);
                leftTranslate.setDuration(50);
                leftSet.addAnimation(leftTranslate);

                rightTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.2f,Animation.RELATIVE_TO_SELF,-0.2f,Animation.RELATIVE_TO_SELF,0F,Animation.RELATIVE_TO_SELF,0F);
                rightTranslate.setDuration(100);
                rightTranslate.setStartOffset(50);
                leftSet.addAnimation(rightTranslate);

                leftTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,-0.2f, Animation.RELATIVE_TO_SELF,0f, Animation.RELATIVE_TO_SELF,0F, Animation.RELATIVE_TO_SELF,0F);
                leftTranslate.setDuration(50);
                leftTranslate.setStartOffset(150);
                leftSet.addAnimation(leftTranslate);

                return leftSet;
            case RIGHT:
                AnimationSet rightSet = new AnimationSet(true);

                leftTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,-0.2f,Animation.RELATIVE_TO_SELF,0F,Animation.RELATIVE_TO_SELF,0F);
                leftTranslate.setDuration(50);
                rightSet.addAnimation(leftTranslate);

                rightTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,-0.2f,Animation.RELATIVE_TO_SELF,0.2f,Animation.RELATIVE_TO_SELF,0F,Animation.RELATIVE_TO_SELF,0F);
                rightTranslate.setDuration(100);
                rightTranslate.setStartOffset(50);
                rightSet.addAnimation(rightTranslate);

                leftTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.2f, Animation.RELATIVE_TO_SELF,0f, Animation.RELATIVE_TO_SELF,0F, Animation.RELATIVE_TO_SELF,0F);
                leftTranslate.setDuration(50);
                leftTranslate.setStartOffset(150);
                rightSet.addAnimation(leftTranslate);

                return rightSet;
            case UP:
                AnimationSet upSet = new AnimationSet(true);

                upTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0F,Animation.RELATIVE_TO_SELF,0.2F);
                upTranslate.setDuration(50);
                upSet.addAnimation(upTranslate);

                downTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0.2F,Animation.RELATIVE_TO_SELF,-0.2F);
                downTranslate.setDuration(100);
                downTranslate.setStartOffset(50);
                upSet.addAnimation(downTranslate);

                upTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f, Animation.RELATIVE_TO_SELF,0f, Animation.RELATIVE_TO_SELF,-0.2F, Animation.RELATIVE_TO_SELF,0F);
                upTranslate.setDuration(50);
                upTranslate.setStartOffset(150);
                upSet.addAnimation(upTranslate);

                return upSet;
            case DOWN:
                AnimationSet downSet = new AnimationSet(true);

                downTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0F,Animation.RELATIVE_TO_SELF,-0.2F);
                downTranslate.setDuration(50);
                downSet.addAnimation(downTranslate);

                upTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,-0.2F,Animation.RELATIVE_TO_SELF,0.2F);
                upTranslate.setDuration(100);
                upTranslate.setStartOffset(50);
                downSet.addAnimation(upTranslate);

                downTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f, Animation.RELATIVE_TO_SELF,0f, Animation.RELATIVE_TO_SELF,0.2F, Animation.RELATIVE_TO_SELF,0F);
                downTranslate.setDuration(50);
                downTranslate.setStartOffset(150);
                downSet.addAnimation(downTranslate);

                return downSet;
        }
        return null;
    }


    public class Card extends FrameLayout {

        //默认卡片背景色
        private static final int DEFAULT_CARD_BACKGROUND = 0xffffffff;

        //卡片颜色数组，存储不同数值卡片颜色,16个
        private int[] cardColor = new int[]{
                0xffF0F0F0,
                0xffE0E0E0,
                0xffD0D0D0,
                0xffC0C0C0,
                0xffB0B0B0,
                0xffA0A0A0,
                0xff909090,
                0xff808080,
                0xff707070,
                0xff606060,
                0xff505050,
                0xff404040,
                0xff303030,
                0xff202020,
                0xff101010,
                0xff101010
        };

        private int number = 0;

        private TextView label;
        CardView cv;

        public Card(Context context) {
            super(context);

            cv = new CardView(context);
            cv.setCardBackgroundColor(DEFAULT_CARD_BACKGROUND);
            cv.setRadius(dip2px(context,2));
            cv.setCardElevation(24);

            label = new TextView(context);
            label.setTextSize(32);
            label.setTextColor(Color.WHITE);
            label.setGravity(Gravity.CENTER);
            cv.addView(label);

            LayoutParams lp = new LayoutParams(-1,-1);
            addView(cv,lp);
            lp.setMargins(10,10,10,10);
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;

            if(number<=0) {
                label.setText("");
                cv.setCardBackgroundColor(DEFAULT_CARD_BACKGROUND);
            }else{
                label.setText(number+"");
                cv.setCardBackgroundColor(cardColor[sqrt2(number)]);
            }
            invalidate();
        }

        private int sqrt2(int num){
            int i = 1;
            while ((num/=2)> 1){
                i++;
            }
            return i;
        }

        public boolean equals(Card card) {
            return getNumber()==card.getNumber();
        }


        //dp转px
        public int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }
    }
}
