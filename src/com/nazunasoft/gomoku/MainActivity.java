package com.nazunasoft.gomoku;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.java_websocket.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.nazunasoft.gomoku.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends MultiSceneActivity {
    private static final String TAG = "MainActivity";
    private Handler mHandler;
    static public WebSocketClient mClient;
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
		
        connectToServer();
		
		return eo;
	}
	
    /* 
          //          mClient.send(edit.getText().toString());
*/
    
	@Override
	protected Scene onCreateScene() {
		SoundFactory.setAssetBasePath("mfx/");
		MusicFactory.setAssetBasePath("mfx/");
		MainScene mainScene = new MainScene(this);
	//	Scene CharacterselectScene = new CharacterselectScene(this);
	//	sp =  getSharedPreferences("myprefs",Context.MODE_PRIVATE);
		return mainScene;
	}

	public void connectToServer(){
		
		   mHandler = new Handler();
	        try {
	            URI uri = new URI(Constants.URI);
	            mClient = new WebSocketClient(uri) {
	                @Override
	                public void onOpen(ServerHandshake handshake) {
	                    Log.d(TAG, "onOpen");
	                }
	                @Override
	                public void onMessage(final String message) {
	                    Log.d(TAG, "onMessage");
	                    Log.d(TAG, "Message:" + message);
	                    mHandler.post(new Runnable() {
	                        @Override
	                        public void run() {
	                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
	                        }
	                    });
	                }
	                @Override
	                public void onError(Exception ex) {
	                    Log.d(TAG, "onError");
	                    ex.printStackTrace();
	                }
	                @Override
	                public void onClose(int code, String reason, boolean remote) {
	                    Log.d(TAG, "onClose");
	                }
	            };
	            mClient.connect();
	        } catch (URISyntaxException e) {
	            e.printStackTrace();
	        }

		
	}
	@Override
	protected int getLayoutID() {
		// Activity�̃��C�A�E�g��ID��Ԃ�
		return R.layout.main;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		// Scene���Z�b�g�����View��ID��Ԃ�
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