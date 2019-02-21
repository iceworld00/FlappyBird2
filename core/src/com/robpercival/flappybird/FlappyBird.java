package com.robpercival.flappybird;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.xml.soap.Text;

import sun.rmi.runtime.Log;

import static com.badlogic.gdx.Gdx.audio;


public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
    ShapeRenderer shapeRenderer;

    Texture gameover;

    Texture[] birds;
    Texture[] bonusbirds;

    int flapState = 0;
    float birdY = 0;
    float bonusbirdY = 0;
    float velocity = 0;
    Circle birdCircle;
    Rectangle heroRectangle;

    Circle bonusbirdCircle;
    int score = 0;
    int scoringTube = 0;
    BitmapFont font;
    float xrandom = 1;

    int gameState = 0;
    float gravity = 2;

    Texture topTube;
    Texture bottomTube;

    Texture pinkpanther;
    Texture truck01Orange;
    Texture ball01;
    Texture truck02;
    Texture Penguin;
    Texture Rock;
    Texture Boom;
    Texture lightning;

    float gap = 10;
    float maxTubeOffset;
    Random randomGenerator;
    float tubeVelocity = 4;
    int numberOfTubes = 5;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    float distanceBetweenTubes;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;

    HealthBar healthbar;

    Stage stage;
    Skin skin;

    int healtbarwidth = 500;
    int fullhealth = 1000;
    int groundIncreaseHealth = 1;
    int currenthealth;

    Sound jumpsound;
    Sound touchgroundsound;
    int touchgroundsoundplayed = 0;

    int cloudXOffset = 100;


    @Override
	public void create () {

        stage = new Stage();

		batch = new SpriteBatch();
		background = new Texture("bg.png");
        gameover = new Texture("gameover.png");
        shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();

        jumpsound = Gdx.audio.newSound(Gdx.files.internal("spin_jump.mp3"));
        touchgroundsound = Gdx.audio.newSound(Gdx.files.internal("punch_or_whack.mp3"));

        heroRectangle = new Rectangle();

        bonusbirdCircle = new Circle();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        bonusbirds = new Texture[2];
        bonusbirds[0] = new Texture("bird.png");
        bonusbirds[1] = new Texture("bird2.png");

        birds = new Texture[2];
        birds[0] = new Texture("Alex_Ready.png");
        birds[1] = new Texture("alex_jump01.png");

        topTube = new Texture("cloud01.png");

        lightning= new Texture("lightning_strike.png");

        bottomTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;

        pinkpanther = new Texture("pinkpanther004.png");
        truck01Orange = new Texture("truck01Orange.png");
        ball01 = new Texture("ball01.png");
        truck02 = new Texture("truck02.png");
        Penguin = new Texture("Pinguin.png");
        Rock = new Texture("Rock.png");
        Boom = new Texture("Boom.png");


        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 4 / 4;
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];


        healthbar = new HealthBar(healtbarwidth, 60);
//        healthbar = new HealthBar(300, 40);

        healthbar.setPosition(10, Gdx.graphics.getHeight() - 40);


        stage.addActor(healthbar);

        currenthealth = fullhealth;

        startGame();

	}

    public void startGame() {

        birdY = 20;
        bonusbirdY = Gdx.graphics.getHeight() - birds[0].getHeight();
        currenthealth = fullhealth;

        cloudXOffset = (int) ( cloudXOffset * (1 + randomGenerator.nextFloat()));

        healthbar.setValue(healtbarwidth);

        for (int i = 0; i < numberOfTubes; i++) {

            tubeOffset[i] = (randomGenerator.nextFloat() * gap);

            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
            Gdx.app.log("tubex", Float.toString(tubeOffset[i]));
            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();

        }

    }






	@Override
	public void render () {
        long lastUpdate = 0L;

        xrandom = randomGenerator.nextFloat();

        batch.begin();

        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {

            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {

                score++;

                Gdx.app.log("Score", String.valueOf(score));

                if (scoringTube < numberOfTubes - 1) {

                    scoringTube++;

                } else {

                    scoringTube = 0;

                }

            }

            if (Gdx.input.justTouched()) {

                Gdx.app.log("Score", "Gdx.input.justTouched()");
                if (healthbar.getValue() > 0) {
                    jumpsound.play();
                    velocity = -30;

                }

            }



            for (int i = 0; i < numberOfTubes; i++) {

                if (tubeX[i] < - topTube.getWidth()) {

                    tubeX[i] += numberOfTubes * distanceBetweenTubes * (1 + randomGenerator.nextFloat());
                    //tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
                    tubeOffset[i] = (randomGenerator.nextFloat() * gap);
               } else {

                    tubeX[i] = tubeX[i] - tubeVelocity;



                }

                //batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);

                if (i==0) {
                    //batch.draw(pinkpanther, tubeX[i], tubeOffset[i]);
                    batch.draw(pinkpanther, tubeX[i], tubeOffset[i]);
                    //Gdx.app.log("Score7", Float.toString(tubeOffset[i]));
                    //topTubeRectangles[i] = new Rectangle(tubeX[i] , Gdx.graphics.getHeight() - topTube.getHeight() - xrandom*200, 0, 0);
                    bottomTubeRectangles[i] = new Rectangle(tubeX[i], tubeOffset[i], pinkpanther.getWidth(), pinkpanther.getHeight());

                }
                if (i==1) {
                    batch.draw(topTube, tubeX[i] + cloudXOffset  , Gdx.graphics.getHeight() - topTube.getHeight() - 200); // + tubeOffset[i]);
                    batch.draw(Rock, tubeX[i], tubeOffset[i]);

                    topTubeRectangles[i] = new Rectangle(tubeX[i] + cloudXOffset , Gdx.graphics.getHeight() - topTube.getHeight() - 200, topTube.getWidth(), topTube.getHeight());
                    bottomTubeRectangles[i] = new Rectangle(tubeX[i], tubeOffset[i], Rock.getWidth(), Rock.getHeight());
                }
                if (i==2) {
                    batch.draw(truck01Orange, tubeX[i], tubeOffset[i]);
                    //topTubeRectangles[i] = new Rectangle(tubeX[i]  , Gdx.graphics.getHeight() - topTube.getHeight() - xrandom*200, topTube.getWidth(), topTube.getHeight());
                    bottomTubeRectangles[i] = new Rectangle(tubeX[i], tubeOffset[i], truck01Orange.getWidth(), truck01Orange.getHeight());
                }

                if (i==3) {
                    batch.draw(topTube, tubeX[i] + cloudXOffset , Gdx.graphics.getHeight() - topTube.getHeight() - 300); // + tubeOffset[i]);

                    batch.draw(ball01, tubeX[i], tubeOffset[i]);
                    topTubeRectangles[i] = new Rectangle(tubeX[i] + cloudXOffset , Gdx.graphics.getHeight() - topTube.getHeight() - 300, topTube.getWidth(), topTube.getHeight());
                    bottomTubeRectangles[i] = new Rectangle(tubeX[i], tubeOffset[i], ball01.getWidth(), ball01.getHeight());
                }

                if (i>=4) {
                    batch.draw(truck02, tubeX[i], tubeOffset[i]);
                    //topTubeRectangles[i] = new Rectangle(tubeX[i] , Gdx.graphics.getHeight() - topTube.getHeight() - xrandom*200, topTube.getWidth(), topTube.getHeight());
                    bottomTubeRectangles[i] = new Rectangle(tubeX[i], tubeOffset[i], truck02.getWidth(), truck02.getHeight());
                }
            }



            if (birdY >= 0) {

                velocity = velocity + gravity;
                birdY -= velocity;
                if (birdY < 20 )
                {birdY = 20;}

            } else {
                birdY = 20;
                gameState = 1;

            }

        } else if (gameState == 0) {

            if (Gdx.input.justTouched()) {

                gameState = 1;


            }

        } else if (gameState == 2) {

            batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);

            if (Gdx.input.justTouched()) {

                gameState = 1;
                startGame();
                score = 0;
                scoringTube = 0;
                velocity = 0;


            }

        }

//       if (flapState == 0) {
//            flapState = 1;
//        } else {
//            flapState = 0;
//        }

        if (birdY > Gdx.graphics.getHeight() - birds[flapState].getHeight()){
            birdY = Gdx.graphics.getHeight() - birds[flapState].getHeight();
        }

       if (birdY > 20) {
           touchgroundsoundplayed = 0;
           flapState = 1;
           currenthealth = currenthealth - 1;
           //Gdx.app.log("healthbar-currenthealth", Integer.toString(currenthealth));

        } else {
            flapState = 0;
            if (touchgroundsoundplayed == 0){
                touchgroundsoundplayed = 1;
                touchgroundsound.play();
            }

           currenthealth = currenthealth + groundIncreaseHealth;
           healthbar.setValue(healthbar.getValue() + groundIncreaseHealth);

           //Gdx.app.log("healthbar-fullhealth", Integer.toString(fullhealth));
        }


        if (System.currentTimeMillis() - lastUpdate > TimeUnit.SECONDS.toMillis(5)) {
            healthbar.setValue(healthbar.getValue() - 0.1f);
            //loadingBarWithBorders.setValue(loadingBarWithBorders.getValue() + 0.1f);
            lastUpdate = System.currentTimeMillis();
            Gdx.app.log("healthbar-getValue", Float.toString(healthbar.getValue()));
        }
        //healthbar.setWidth((float) (currenthealth/fullhealth)*healtbarwidth);
        //healthbar.setWidth((float) 150);
        if (currenthealth/fullhealth > 1111){
            Gdx.app.log("healthbar", "red");
            Gdx.app.log("healthbar-healthbar.getWidth()", Float.toString(healthbar.getWidth()));
            Gdx.app.log("healthbar-total", Float.toString((currenthealth/fullhealth)*healtbarwidth));
            Gdx.app.log("healthbar-currenthealth", Float.toString((float) (currenthealth)));
            Gdx.app.log("healthbar-fullhealth", Float.toString((float) fullhealth));
            Gdx.app.log("healthbar-healtbarwidth", Float.toString((float) healtbarwidth));


            healthbar.setColor(Color.RED);
        }

        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);

        font.draw(batch, String.valueOf(score), 100, 200);

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);
        heroRectangle.set(Gdx.graphics.getWidth()/2- birds[flapState].getWidth() / 2, birdY, birds[flapState].getWidth(), birds[flapState].getHeight());

        //shape.setAutoShape(true);
        //shapeRenderer.setAutoShapeType(true);
        //shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        //shapeRenderer.setColor(Color.RED);
        //shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        //shapeRenderer.circle(50, 50, 50);
        //shapeRenderer.rect(Gdx.graphics.getWidth()/2 - birds[flapState].getWidth() / 2, birdY, birds[0].getWidth(), birds[0].getHeight());
        //shapeRenderer.rect(heroRectangle.getX(), heroRectangle.getY(), heroRectangle.getWidth(), heroRectangle.getHeight());

        for (int i = 0; i < numberOfTubes; i++) {

          //  shapeRenderer.rect(bottomTubeRectangles[i].getX(), bottomTubeRectangles[i].getY(), bottomTubeRectangles[i].getWidth(), bottomTubeRectangles[i].getHeight());

//                shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() - topTube.getHeight() - xrandom*200, topTube.getWidth(), topTube.getHeight());
            //shapeRenderer.rect(topTubeRectangles[i].getX(), topTubeRectangles[i].getY(), topTubeRectangles[i].getWidth(), topTubeRectangles[i].getHeight());

            if (Intersector.overlaps(heroRectangle, bottomTubeRectangles[i])) {
           // if (Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {

                gameState = 2;
                batch.draw(Boom, tubeX[i], tubeOffset[i] +400);
            }
            if (Intersector.overlaps(heroRectangle, topTubeRectangles[i])) {
           // if (Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {

                healthbar.setValue(healthbar.getValue() - 10);
                batch.draw(lightning,  -140, 100);
            }

        }

//        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.end();

        stage.draw();
        stage.act();

        //shapeRenderer.end();



    }


    public class HealthBar extends ProgressBar {

        /**
         * @param width of the health bar
         * @param height of the health bar
         */
        public HealthBar(int width, int height) {
            super(0f, 100f, 0.01f, false, new ProgressBarStyle());
            getStyle().background = Utils.Utils.getColoredDrawable(width, height, Color.ORANGE);
            getStyle().knob = Utils.Utils.getColoredDrawable(0, height, Color.GREEN);
            getStyle().knobBefore = Utils.Utils.getColoredDrawable(width, height, Color.GREEN);

            setWidth(width);
            setHeight(height);

            setAnimateDuration(0.0f);
            setValue(100f);

            setAnimateDuration(0.25f);
        }
    }

}
