package com.nazunasoft.gomoku;

import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import com.nazunasoft.gomoku.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

public class MainActivity extends MultiSceneActivity {
    static Handler mHandler;
	private int CAMERA_WIDTH = 480;
	private int CAMERA_HEIGHT = 800;
	static SharedPreferences sp ;
	public static int whoisselected=0;
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions eo = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		eo.getAudioOptions().setNeedsSound(true);
		eo.getAudioOptions().setNeedsMusic(true);
		
		mHandler = new Handler();
		return eo;
	}
	
	@Override
	protected Scene onCreateScene() {
		SoundFactory.setAssetBasePath("mfx/");
		MusicFactory.setAssetBasePath("mfx/");
		//MainScene mainScene = new MainScene(this);
		Scene TopScene = new TopScene(this);
		sp =  getSharedPreferences("myprefs",Context.MODE_PRIVATE);
		return TopScene;
	}

	@Override
	protected int getLayoutID() {
		// ActivityのレイアウトのIDを返す
		return R.layout.main;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		// SceneがセットされるViewのIDを返す
		return R.id.renderview;
	}
	
	@Override
	public void appendScene(KeyListenScene scene) {
		
	}

	@Override
	public void backToInitial() {
		
	}
	
	@Override
	public void refreshRunningScene(KeyListenScene scene) {
	}
	
	@Override
	public void onGameDestroyed(){
		System.exit(0);
	}

    
}