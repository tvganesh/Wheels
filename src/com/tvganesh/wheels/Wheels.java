package com.tvganesh.wheels;

/*
 * Modeling a Car in Android
 * Designed and developed by Tinniam V Ganesh, 19 Jun 2013
 * Uses AndEngine 
 * Uses Box 2D physics Engine
 */

import java.util.ArrayList;
import java.util.Iterator;
import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;



import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;



public class Wheels extends SimpleBaseGameActivity implements  IAccelerationListener, IOnSceneTouchListener {
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;
	private static final float  DEGTORAD = 0.0174532925199432957f;
	public static final float PIXEL_TO_METER_RATIO_DEFAULT = 32.0f;
	
    
    private Scene mScene;
    
    private PhysicsWorld mPhysicsWorld;
    
	private BitmapTextureAtlas mBitmapTextureAtlas;   
	private TextureRegion mWheelTextureRegion, mWallTextureRegion;
	private TextureRegion mCarTextureRegion;
	private TextureRegion mBallTextureRegion,mBallBlackTextureRegion,mBallBlueTextureRegion;
	
	Rectangle r;
	Rectangle ground,roof,left,right;
	static Sprite lWall,rWall;
	Body lWallBody,rWallBody;
	Sprite wheel1,wheel2;
	Body wheelBody1,wheelBody2;
	Sprite car;
	Body  carBody;
	final FixtureDef gameFixtureDef = PhysicsFactory.createFixtureDef(10f, 0.0f, 0.2f);

    private static FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(50f, 0.0f, 0.5f);
	
    static RevoluteJoint rj1;
	static RevoluteJoint rj2;
	private int mBallCount = 0;
    

    public EngineOptions onCreateEngineOptions() {
		
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}
	
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");	
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 150, 560, TextureOptions.BILINEAR);		
		
		this.mWheelTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "wheel.png", 0, 0);		
		this.mWallTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "wall.png", 30, 30);		
		this.mCarTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "car.png", 40, 510);
		this.mBallTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "ball.png", 120, 530);
		this.mBallBlackTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "ball_black.png", 130, 540);
		this.mBallBlueTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "ball_blue.png", 140, 550);
		this.mBitmapTextureAtlas.load();
		
	
	}
	
	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		this.mScene.setOnSceneTouchListener(this);
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_MOON), false);
			
		this.initWheels(mScene);
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
	
		return mScene;		
		
	}
	

	public void initWheels(Scene mScene){
		
	
		//Create the floor		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);

		// Set a small friction for the wheels to roll
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.0f, 0.2f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
       
        
		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);

		// Create the left wall - Collisions happen between bodies
		lWall = new Sprite(0, 0, this.mWallTextureRegion, this.getVertexBufferObjectManager());
		lWallBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld, lWall, BodyType.StaticBody, wallFixtureDef);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(lWall, lWallBody, true, true));
		this.mScene.attachChild(lWall);
		
		// Create right wall - Collisions happen between bodies
		rWall = new Sprite(715, 0, this.mWallTextureRegion, this.getVertexBufferObjectManager());
		rWallBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld, rWall, BodyType.StaticBody, wallFixtureDef);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(rWall, rWallBody, true, true));
		this.mScene.attachChild(rWall);
		
		 
		// Create Wheels 1 & 2
		wheel1 = new Sprite(20, 300, this.mWheelTextureRegion, this.getVertexBufferObjectManager());
		wheelBody1 = PhysicsFactory.createCircleBody(this.mPhysicsWorld, wheel1, BodyType.DynamicBody, gameFixtureDef);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(wheel1, wheelBody1, true, true));
		this.mScene.attachChild(wheel1);
		
		
		wheel2 = new Sprite(60, 300, this.mWheelTextureRegion, this.getVertexBufferObjectManager());
		wheelBody2 = PhysicsFactory.createCircleBody(this.mPhysicsWorld, wheel2, BodyType.DynamicBody, gameFixtureDef);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(wheel2, wheelBody2, true, true));
		this.mScene.attachChild(wheel2);	
		
		
		float x = wheel1.getX();
		float y = wheel1.getY();
		
		
		
		//Create the body of the Car		
		car = new Car(x, y-30, this.mCarTextureRegion, this.getVertexBufferObjectManager(), mPhysicsWorld);
		final Body carBody = PhysicsFactory.createBoxBody(mPhysicsWorld,car,BodyType.DynamicBody,gameFixtureDef);		
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(car, carBody,true,true));
		this.mScene.attachChild(car);
		
	
		  /* Front Axle */
        Rectangle r2 = new Rectangle(30, 295, 10, 10, this.getVertexBufferObjectManager());
        r2.setColor(new Color(0, 255, 1));
        r2.setAlpha(1f);
        Body frontAxle = PhysicsFactory.createBoxBody(mPhysicsWorld,r2,BodyType.DynamicBody,gameFixtureDef);
        this.mScene.attachChild(r2);
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(r2, frontAxle));

  	    /* Rear Axle*/
        Rectangle r3 = new Rectangle(70, 295, 10, 10, this.getVertexBufferObjectManager());
        r3.setColor(new Color(0, 255, 1));
        r3.setAlpha(1f);
        Body rearAxle = PhysicsFactory.createBoxBody(mPhysicsWorld,r3,BodyType.DynamicBody,gameFixtureDef);
        this.mScene.attachChild(r3);
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(r3, rearAxle));
        
        /* Connect the front axle to car using a prismaticJoint */
        PrismaticJointDef front = new PrismaticJointDef();
        front.initialize(carBody, frontAxle, frontAxle.getWorldCenter(),new Vector2(0f,1f));
        front.collideConnected=false;
        front.enableMotor=false;
        front.enableLimit=true;
        front.upperTranslation=0.5f;
        front.lowerTranslation=-0.2f;
        PrismaticJoint mFront = (PrismaticJoint) mPhysicsWorld.createJoint(front);

        /* Connect the rear axle to car using a prismaticJoint */
        PrismaticJointDef rear = new PrismaticJointDef();
        rear.collideConnected=false;
        rear.initialize(carBody,rearAxle ,rearAxle.getWorldCenter(),new Vector2(0f,1f));
        rear.enableMotor=false;
        rear.enableLimit=true;
        rear.upperTranslation=0.5f;
        rear.lowerTranslation=-0.2f;
        PrismaticJoint mRear = (PrismaticJoint) mPhysicsWorld.createJoint(rear);
		
        //Connect front wheel to front axle using revoluteJoint
        final RevoluteJointDef revoluteJointDef1 = new RevoluteJointDef();	    
		revoluteJointDef1.initialize(wheelBody1, frontAxle, wheelBody1.getWorldCenter());
		revoluteJointDef1.enableMotor = true;
		revoluteJointDef1.motorSpeed = -50;
		revoluteJointDef1.maxMotorTorque = 10;
		rj1 = (RevoluteJoint) this.mPhysicsWorld.createJoint(revoluteJointDef1);			
		
	     //Connect rear wheel to rear axle using revoluteJoint
		 final RevoluteJointDef revoluteJointDef2 = new RevoluteJointDef();
	     revoluteJointDef2.initialize(wheelBody2, rearAxle, wheelBody2.getWorldCenter());
		 revoluteJointDef2.enableMotor = true;
		 revoluteJointDef2.motorSpeed = -50;
		 revoluteJointDef1.maxMotorTorque = 10;
		 rj2 = (RevoluteJoint) this.mPhysicsWorld.createJoint(revoluteJointDef2);	
		 
		 
		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}	

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
		
	}


	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerationSensor(this);

	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}
	
	private static class Car extends Sprite {
		private final PhysicsHandler mPhysicsHandler;
		
		public Car(final float pX, final float pY, final TextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager,PhysicsWorld mPW) {
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
			
			
		}
		
		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			// Check collisions
			if(rWall.collidesWith(this) || lWall.collidesWith(this) ){
              
				 // On collision reverse speed
                  rj1.setMotorSpeed(-(rj1.getMotorSpeed()));
                  rj2.setMotorSpeed(-(rj2.getMotorSpeed()));
                  
                  
                 
                 
			}
			
			
		}
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			if(pSceneTouchEvent.isActionDown()) {
				this.addBall(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				return true;
			}
		}
		return false;
	}
	
	// Add obstacles on the way
	private void addBall(final float pX, final float pY) {
	
		final  Sprite ball, ballBlack,ballBlue;
		final  Body ballBody,ballBlackBody,ballBlueBody;

		this.mBallCount++;
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.1f, 0.1f);

		if(this.mBallCount % 3 == 0) {
			ball = new Sprite(pX, pY, this.mBallTextureRegion, this.getVertexBufferObjectManager());
			ballBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld, ball, BodyType.DynamicBody, objectFixtureDef);
			this.mScene.attachChild(ball);
			this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(ball, ballBody, true, true));		
		} else if (this.mBallCount % 3 == 1){
			ballBlue = new Sprite(pX, pY, this.mBallBlueTextureRegion, this.getVertexBufferObjectManager());
			ballBlueBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, ballBlue, BodyType.DynamicBody, objectFixtureDef);
			this.mScene.attachChild(ballBlue);
			this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(ballBlue, ballBlueBody, true, true));				
		}
		else {
			ballBlack = new Sprite(pX, pY, this.mBallBlackTextureRegion, this.getVertexBufferObjectManager());
			ballBlackBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, ballBlack, BodyType.DynamicBody, objectFixtureDef);
			this.mScene.attachChild(ballBlack);
			this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(ballBlack, ballBlackBody, true, true));		
		}
		
	}

	
}	