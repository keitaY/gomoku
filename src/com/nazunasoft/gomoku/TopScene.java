package com.nazunasoft.gomoku;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;

import android.content.Intent;
import android.view.KeyEvent;

public class TopScene extends KeyListenScene implements IOnSceneTouchListener {
	
	Sprite tutorial1  =  getBaseActivity().getResourceUtil().getSprite("tutorial1.jpg");
	Sprite tutorial2  =  getBaseActivity().getResourceUtil().getSprite("tutorial2.jpg");
	Sound startsnd ;
	Sprite startButton;
	Sprite tutorialButton;
	public TopScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		init();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public void init() {
		setBackground();
		setButtons();
		prepareAssets();
	}

	public void setBackground(){
		Sprite top  =  getBaseActivity().getResourceUtil().getSprite("top.jpg");
		top.setPosition(0,0);
		attachChild(top);
	}
	
	public void prepareAssets(){
		tutorial1.setPosition(0,280);
		tutorial2.setPosition(0,280);
	}
	
	public void setButtons(){
		 tutorialButton = new Sprite(140, 600, (TextureRegion) getBaseActivity().getResourceUtil().getSprite("button_tutorial.jpg").getTextureRegion(), this.getBaseActivity().getVertexBufferObjectManager()){
			   @Override
			   public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
			    switch (pSceneTouchEvent.getAction()) {
			    case TouchEvent.ACTION_DOWN:
			    	startsnd.play();
			    	tutorial1();
			        break;
			    }
			    return true;
			   }
			  };
			  attachChild(tutorialButton);
			  registerTouchArea(tutorialButton);
			  
		  startButton = new Sprite(140, 500, (TextureRegion) getBaseActivity().getResourceUtil().getSprite("button_start.jpg").getTextureRegion(), this.getBaseActivity().getVertexBufferObjectManager()){
				   @Override
				   public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				    switch (pSceneTouchEvent.getAction()) {
				    case TouchEvent.ACTION_DOWN:
				    	startsnd.play();
				        start();
				    	
				        break;
				    }
				    return true;
				   }
				  };
				  attachChild(startButton);
				  registerTouchArea(startButton);
				  
			    	tutorial1 = new Sprite(0, 220, (TextureRegion) getBaseActivity().getResourceUtil().getSprite("tutorial1.jpg").getTextureRegion(), this.getBaseActivity().getVertexBufferObjectManager()){
						   @Override
						   public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
						    switch (pSceneTouchEvent.getAction()) {
						    case TouchEvent.ACTION_DOWN:
						    	startsnd.play();
						        tutorial2();
						        break;
						    }
						    return true;
						   }
						  };
					    	tutorial2 = new Sprite(0, 220, (TextureRegion) getBaseActivity().getResourceUtil().getSprite("tutorial2.jpg").getTextureRegion(), this.getBaseActivity().getVertexBufferObjectManager()){
								   @Override
								   public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
								    switch (pSceneTouchEvent.getAction()) {
								    case TouchEvent.ACTION_DOWN:
								    	startsnd.play();
								    	tutorial3();
								        break;
								    }
								    return true;
								   }
								  };
	}
	
	public void tutorial1(){
		unregisterTouchArea(tutorialButton);
		unregisterTouchArea(startButton);
		if(!tutorial1.hasParent())attachChild(tutorial1);
	  	tutorial1.registerEntityModifier(new FadeInModifier(0.1f));
    	tutorial1.registerEntityModifier(new MoveXModifier(
				0.15f, 40, 0));
		tutorial1.setAlpha(0.6f);
		registerTouchArea(tutorial1);
	}
	
	public void tutorial2(){
	   	tutorial1.registerEntityModifier(new MoveXModifier(
					0.45f, 0, -40));
		tutorial1.registerEntityModifier(new FadeOutModifier(0.45f));
		unregisterTouchArea(tutorial1);
		if(!tutorial2.hasParent())attachChild(tutorial2);
		tutorial2.registerEntityModifier(new FadeInModifier(0.1f));
    	tutorial2.registerEntityModifier(new MoveXModifier(
				0.15f, 40, 0));
		tutorial2.setAlpha(0.6f);
		registerTouchArea(tutorial2);
	}
	
	public void tutorial3(){
		unregisterTouchArea(tutorial2);
	   	tutorial2.registerEntityModifier(new MoveXModifier(
					0.30f, 0, -40));
		tutorial2.registerEntityModifier(new FadeOutModifier(0.3f));
		registerTouchArea(tutorialButton);
		registerTouchArea(startButton);
	}
	
	public void start(){
        ResourceUtil.getInstance(getBaseActivity()).resetAllTexture();
        KeyListenScene scene = new MainScene(getBaseActivity());
        getBaseActivity().getEngine().setScene(scene);
        getBaseActivity().appendScene(scene);
	}
	
	@Override
	public void prepareSoundAndMusic() {
		// TODO 自動生成されたメソッド・スタブ
		try {
			startsnd = SoundFactory.createSoundFromAsset(getBaseActivity().getSoundManager(),getBaseActivity(),"start.wav");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

}
