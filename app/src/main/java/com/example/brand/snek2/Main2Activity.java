package com.example.brand.snek2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;


public class Main2Activity extends AppCompatActivity {

    SnakeEngine snakeEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Initialize the result into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create a new instance of the SnakeEngine class
        snakeEngine = new SnakeEngine(this, size);

        // Make snakeEngine the view of the Activity
        setContentView(snakeEngine);

    }


    // Start the thread in snakeEngine
    @Override
    protected void onResume() {
        super.onResume();
        snakeEngine.resume();
    }

    // Stop the thread in snakeEngine
    @Override
    protected void onPause() {
        super.onPause();
        snakeEngine.pause();
    }
}



class SnakeEngine extends SurfaceView implements Runnable {

    private Thread thread = null;

    // To hold a reference to the Activity
    private Context context;

    // For tracking movement Heading
    public enum Heading {
        UP, RIGHT, DOWN, LEFT
    }
    public enum Colores {
        ROJO,ANARANJADO,AMARILLO,VERDE,AZUL,MORADO
    }

    // Start by heading to the right
    private Heading heading = Heading.RIGHT;
    private Colores color = Colores.ROJO;

    // To hold the screen size in pixels
    private int screenX;
    private int screenY;

    // How long is the snake
    private int snakeLength;

    // Where is Bob hiding?
    private int bobX;
    private int bobY;
    private int nobX;
    private int nobY;

    // The size in pixels of a snake segment
    private int blockSize;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int numBlocksHigh;

    // Control pausing between updates
    private long nextFrameTime;
    // Update the game 10 times per second
    private final long FPS = 10;
    // There are 1000 milliseconds in a second
    private final long MILLIS_PER_SECOND = 1000;
// We will draw the frame much more often

    // How many points does the player have
    private int score;
    public int highscore = 0;

    // The location in the grid of all the segments
    private int[] snakeXs;
    private int[] snakeYs;
    ArrayList<Point> nobs = new ArrayList<Point>();

    // Everything we need for drawing
// Is the game currently playing?
    private volatile boolean isPlaying;


    // A canvas for our paint
    private Canvas canvas;

    // Required to use canvas
    private SurfaceHolder surfaceHolder;

    // Some paint for our canvas
    private Paint paint;

    public SnakeEngine(Context context, Point size) {
        super(context);

        context = context;

        screenX = size.x;
        screenY = size.y;

        // Work out how many pixels each block is
        blockSize = screenX / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        numBlocksHigh = screenY / blockSize;


        // Initialize the drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        // If you score 200 you are rewarded with a crash achievement!
        snakeXs = new int[200];
        snakeYs = new int[200];

        // Start the game
        newGame();
    }

    @Override
    public void run() {

        while (isPlaying) {

            // Update 10 times a second
            if (updateRequired()) {
                update();
                draw();
            }

        }
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void newGame() {
        // Start with a single snake segment
        snakeLength = 1;
        snakeXs[0] = NUM_BLOCKS_WIDE / 2;
        snakeYs[0] = numBlocksHigh / 2;

        // Get Bob ready for dinner
        spawnBob();

        // Reset the score
        score = 0;

        // Setup nextFrameTime so an update is triggered
        nextFrameTime = System.currentTimeMillis();
    }

    public int ranRange(int s, int e){
        Random r = new Random();
        int d = e - s;
        int x = r.nextInt(d);
        int f = (x + 1) + s;
        return f;
    }

    public void spawnBob() {
        Random random = new Random();
        bobX = ranRange(1,NUM_BLOCKS_WIDE - 2);
        bobY = ranRange(3,numBlocksHigh - 2);
    }

    public void spawnNob(){
        Random random = new Random();
        nobX = ranRange(1,NUM_BLOCKS_WIDE - 2);
        nobY = ranRange(3,numBlocksHigh - 2);
        Point whereTheNob = new Point(nobX,nobY);
        nobs.add(whereTheNob);

    }


    private void eatBob() {
        //  Got him!
        // Increase the size of the snake
        snakeLength++;
        //replace Bob
        // This reminds me of Edge of Tomorrow. Oneday Bob will be ready!
        spawnBob();
        spawnNob();
        //add to the score
        score = score + 1;
    }


    private void moveSnake() {
        // Move the body
        for (int i = snakeLength; i > 0; i--) {
            // Start at the back and move it
            // to the position of the segment in front of it
            snakeXs[i] = snakeXs[i - 1];
            snakeYs[i] = snakeYs[i - 1];

            // Exclude the head because
            // the head has nothing in front of it
        }

        // Move the head in the appropriate heading
        switch (heading) {
            case UP:
                snakeYs[0]--;
                break;

            case RIGHT:
                snakeXs[0]++;
                break;

            case DOWN:
                snakeYs[0]++;
                break;

            case LEFT:
                snakeXs[0]--;
                break;
        }
    }

    private boolean detectDeath() {
        // Has the snake died?
        boolean dead = false;

        // Hit the screen edge
        if (snakeXs[0] <= 0) dead = true;
        if (snakeXs[0] >= NUM_BLOCKS_WIDE -1) dead = true;
        if (snakeYs[0] <= 3) dead = true;
        if (snakeYs[0] >= numBlocksHigh-1) dead = true;

        // Eaten itself?
        for (int i = snakeLength - 1; i > 0; i--) {
            if ((i > 4) && (snakeXs[0] == snakeXs[i]) && (snakeYs[0] == snakeYs[i])) {
                dead = true;
            }
        }
        //Have we eaten a Nob?
        for (int i = 0; i < nobs.size(); i++){
            if(snakeXs[0] == nobs.get(i).x && snakeYs[0] == nobs.get(i).y){
                dead = true;
            }
        }
        return dead;
    }

    public void update() {
        // Did the head of the snake eat Bob?
        if (snakeXs[0] == bobX && snakeYs[0] == bobY) {
            eatBob();
        }

        moveSnake();

        if (detectDeath()) {
            nobs.clear();
            if (score > Main3Activity.highscore) {
                Main3Activity.highscore = score;
            }
            newGame();
        }
    }

    public void draw() {
        // Get a lock on the canvas
        if (surfaceHolder.getSurface().isValid()) {

            canvas = surfaceHolder.lockCanvas();

            // Fill the screen with Game Code School blue
            canvas.drawColor(Color.argb(255,255,255,255));

            paint.setColor(Color.argb(200,23,25,156));

            canvas.drawRect(27,27,screenX-27,screenY-29,paint);

            // Set the color of the paint to draw the snake white
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Scale the HUD text
            paint.setTextSize(70);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Score:" + score, 30, 90, paint);


            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            canvas.drawLine(0,105,screenX,105,paint);

            paint.setStyle(Paint.Style.FILL);

            // Draw the snake one block at a time
            for (int i = 0; i < snakeLength; i++) {
                canvas.drawRect(snakeXs[i] * blockSize,
                        (snakeYs[i] * blockSize),
                        (snakeXs[i] * blockSize) + blockSize,
                        (snakeYs[i] * blockSize) + blockSize,
                        paint);
            }

            // Set the color of the paint to draw Bob red
            paint.setColor(Color.argb(255, 255, 0, 0));

            // Draw Bob
            canvas.drawRect(bobX * blockSize,
                    (bobY * blockSize),
                    (bobX * blockSize) + blockSize,
                    (bobY * blockSize) + blockSize,
                    paint);

            paint.setColor(Color.argb(255, 255, 255, 0));

            for (Point poin:nobs){
                canvas.drawRect(poin.x * blockSize,
                        (poin.y * blockSize),
                        (poin.x * blockSize) + blockSize,
                        (poin.y * blockSize) + blockSize,
                        paint);
            }

            // Unlock the canvas and reveal the graphics for this frame
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public boolean updateRequired() {

        // Are we due to update the frame
        if (nextFrameTime <= System.currentTimeMillis()) {
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            nextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;

            // Return true so that the update and draw
            // functions are executed
            return true;
        }

        return false;
    }
    private float initx, inity, endx, endy, totalx, totaly;
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                initx = motionEvent.getX();
                inity = motionEvent.getY();
            case MotionEvent.ACTION_UP:
                endx = motionEvent.getX();
                endy = motionEvent.getY();
                totalx = endx - initx;
                totaly = endy - inity;
                if (Math.abs(totalx) > Math.abs(totaly) && totalx < -20 && heading != Heading.RIGHT){
                    heading = Heading.LEFT;
                }
                if (Math.abs(totalx) > Math.abs(totaly) && totalx > 20 && heading != Heading.LEFT){
                    heading = Heading.RIGHT;
                }
                if (Math.abs(totaly) > Math.abs(totalx) && totaly < -20 && heading != Heading.DOWN) {
                    heading = Heading.UP;
                }
                if (Math.abs(totaly) > Math.abs(totalx) && totaly > 20 && heading != Heading.UP) {
                    heading = Heading.DOWN;
                }
        }
        return true;
    }
}