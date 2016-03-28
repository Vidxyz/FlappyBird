package com.vidhyasagar.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	ShapeRenderer shapeRenderer;
	int gameScore;
	int scoringTube;
	BitmapFont font;

	Texture[] birds;
	float birdY = 0; //Use this to manipulate bird going up and down
	Boolean flapState = false;
	Boolean isGameActive = false;
	float velocity = 0;
	float gravity = 2;
	Circle birdCircle;

	Texture topTube;
	Texture bottomTube;
	Rectangle[] topRectangles;
	Rectangle[] bottomRectangles;
	float gap;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 5;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameScore = 0;
		scoringTube = 0;
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
//		shapeRenderer = new ShapeRenderer();

		//BIRDS
		birdCircle = new Circle();
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		birdY = Gdx.graphics.getHeight() / 2 - birds[1].getWidth() / 2;



		//TUBES
		topRectangles = new Rectangle[numberOfTubes];
		bottomRectangles = new Rectangle[numberOfTubes];
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		gap = 400;
		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 2/3;

		for(int i = 0; i < numberOfTubes; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 100);
			tubeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + (i *distanceBetweenTubes) + Gdx.graphics.getWidth();
			topRectangles[i] = new Rectangle();
			bottomRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(isGameActive) {

			if(birdCircle.x > tubeX[scoringTube]) {
				gameScore++;
				if(scoringTube < numberOfTubes-1) {
					scoringTube++;
				}
				else {
					scoringTube = 0;
				}
				Gdx.app.log("Score", Integer.toString(gameScore));
			}

			if(Gdx.input.justTouched()) {
				velocity = -27;
			}

			for(int i = 0; i < numberOfTubes; i++) {
				if(tubeX[i] < -topTube.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 100);
				}
				else {
					tubeX[i] = tubeX[i] - tubeVelocity;
				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i]);

				//SETTING TOP TUBE SHAPE
				topRectangles[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 + gap/2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomRectangles[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());

			}


			if(birdY >0 || velocity < 0) {
				velocity = velocity + gravity;
				birdY -= velocity;
			}

		}
		else {
			if(Gdx.input.justTouched()) {
				isGameActive = true;
			}

		}


		if(flapState) {
			batch.draw(birds[1], Gdx.graphics.getWidth() / 2 - birds[1].getWidth() / 2, birdY);
			flapState = false;
		}
		else {
			flapState = true;
			batch.draw(birds[0], Gdx.graphics.getWidth() / 2 - birds[0].getWidth() / 2, birdY);
		}

		font.draw(batch, String.valueOf(gameScore), 100, 200);
		batch.end();


		//SETTING BIRD SHAPE
//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[0].getHeight() / 2, birds[0].getWidth() / 2); //XY Coordinate and radius
//		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		//SETTING BOTTOM TUBE SHAPE
		for(int i = 0; i < numberOfTubes; i++) {
//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap/2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());

			//CHECK FOR COLLISION
			if(Intersector.overlaps(birdCircle, topRectangles[i]) || Intersector.overlaps(birdCircle, bottomRectangles[i])) {
				Gdx.app.log("Collision", "Yes");
			}

		}

//		shapeRenderer.end();
	}
}
