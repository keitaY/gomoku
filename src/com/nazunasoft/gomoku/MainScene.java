package com.nazunasoft.gomoku;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.TextureRegion;
import org.java_websocket.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;



public class MainScene extends KeyListenScene implements IOnSceneTouchListener {
    private static final String TAG = "MainScene";
	Sprite[] goishi = new Sprite[200];
	Sprite goban;
	Sprite pregoishi;
	private Sound goishisnd;
	static int GshiftY = 200;
	static int Gpad=30;
	static int Ggridlen=35;
	int sentegote=0;//sente(black)=0 gote(white)=1
	int step=0;
	int[][] field = new int[25][25];//noStone=0,black=1,white=2
	int get=0;
	int connectionID;
    static public WebSocketClient mClient;
	public MainScene(MultiSceneActivity baseActivity){
		super(baseActivity);
		init();
	}
	
	public void init(){
		step=0;
		get=0;
        connectToServer();
		prepareGraphics();
		setOnSceneTouchListener(this);
		//-------------------
		setBackground();
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
	    switch (pSceneTouchEvent.getAction()) {
	    case TouchEvent.ACTION_DOWN:
	    	preStone(x,y);
	        break;
	    case TouchEvent.ACTION_UP:
	    	if(pregoishi.hasParent()){pregoishi.detachSelf();}
	    	solveGrid(x,y);
	        break;
	    case TouchEvent.ACTION_MOVE:
			if(pregoishi.hasParent()){pregoishi.detachSelf();}
	    	preStone(x,y);
	        break;
	    case TouchEvent.ACTION_CANCEL:
	        Log.d("TouchEvent", "getAction()" + "ACTION_CANCEL");
	        break;
	    }
		
		return true;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		return false;
	}

	public void prepareGraphics() {
		 goban =  getBaseActivity().getResourceUtil().getSprite("goban.jpg");
		 goban.setZIndex(-1);
	}
	
	public void setBackground(){
		goban.setPosition(0,GshiftY);
		attachChild(goban);
	}
	
	public void solveGrid(float x, float y){
		int gx = (int)((x-Gpad)/Ggridlen);
		int gy = (int)((y-Gpad-GshiftY)/Ggridlen);
		Log.d("grid", "[" + gx+","+gy+"]");
		if(gx>=0&&gy>=0&&gx<=12&&gy<=12&&field[gx][gy]==0){
			setStone(gx,gy,sentegote);
			sendStone(gx,gy);
		}
	}
	//----------------------------------------------------------send&receive
	public void sendStone(int gx, int gy){
		JSONObject data = new JSONObject();
		try {
			data.put("bw", sentegote);
			data.put("gx", gx);
			data.put("gy", gy);
			data.put("id", connectionID);
		} catch (JSONException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
		}
		
		try {
			mClient.send(data.toString());
		} catch (NotYetConnectedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}	
	}
	public void parseJson(String data){
        try {
			JSONObject jsonObject = new JSONObject(data);
			int gx = jsonObject.getInt("gx");
			int gy = jsonObject.getInt("gy");
			int bw = jsonObject.getInt("bw");
			int id = jsonObject.getInt("id");
			if(bw>=0&&id!=connectionID){
			setStone(gx,gy,bw);
			}else{
			connectionID = id;
			sentegote = connectionID%2;
			}
		} catch (JSONException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}
	
	//----------------------------------------------------------------UX

	public void preStone(float x,float y){
		int gx = (int)((x-Gpad)/Ggridlen);
		int gy = (int)((y-Gpad-GshiftY)/Ggridlen);
		if(gx>=0&&gy>=0&&gx<=12&&gy<=12&&field[gx][gy]==0){
			if(sentegote==0){
				 pregoishi=getBaseActivity().getResourceUtil().getSprite("black_s.png");
				 }else if(sentegote==1){
				 pregoishi=getBaseActivity().getResourceUtil().getSprite("white_s.png");
			}
			pregoishi.setPosition((float)(Gpad+(gx-0.5)*Ggridlen),(float)(GshiftY+Gpad+(gy-0.5)*Ggridlen));
			pregoishi.setAlpha(0.3f);
			attachChild(pregoishi);
		}
	}
	
	public void setStone(int gx,int gy, int bw){
		if(bw==0){
		 goishi[step]=getBaseActivity().getResourceUtil().getSprite("black_s.png");
		 }else if(bw==1){
		 goishi[step]=getBaseActivity().getResourceUtil().getSprite("white_s.png");
		 }
		field[gx][gy]=(bw+1);
		goishi[step].setZIndex(1);
		goishi[step].setPosition((float)(Gpad+(gx-0.5)*Ggridlen),(float)(GshiftY+Gpad+(gy-0.5)*Ggridlen));
		attachChild(goishi[step]);
		goishi[step].registerEntityModifier(new FadeInModifier(0.3f));
		stoneHasamu(gx,gy);
		if(checkGame(gx,gy)==1){
			try {
				mClient.send("uwaa");
		} catch (NotYetConnectedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}}
		step++;
		//if(sentegote==0){sentegote=1;
		//}else if(sentegote==1){sentegote=0;}
		goishisnd.play();
	}
	
	public void removeStone(int gx,int gy){
		field[gx][gy]=0;
		for(int i=0;i<step;i++){
			if(goishi[i].getX()==(float)(Gpad+(gx-0.5)*Ggridlen)&&goishi[i].getY()==(float)(GshiftY+Gpad+(gy-0.5)*Ggridlen))
			goishi[i].detachSelf();
		}
		get++;
	}
	
	
	//---------------------------------------------------------------gameAlgo
	public int checkGame(int gx, int gy){//find 5-lined stones
		int i=0;
		int count=0;
		for(i=0;i<12;i++){
			if(field[gx][i]!=0&&field[gx][i]==field[gx][i+1]){
				count++;
				if(count==4){
					 return 1;
				}
			}else{count=0;}
		}
		count=0;
		for(i=0;i<12;i++){
			if(field[i][gy]!=0&&field[i][gy]==field[i+1][gy]){
				count++;
				if(count==4){
					 return 1;
				}
			}else{count=0;}
		}
		count=0;
		int maxcross=gx+gy;
		for(i=0;i<maxcross;i++){
			if(maxcross-i-1>=0){
				if(field[i][maxcross-i]!=0&&field[i][maxcross-i]==field[i+1][maxcross-i-1]){
					count++;
					if(count==4){
						return 1;
					}
				}else{count=0;}
			}
		}
		count=0;
		int maxdecross=gx-gy;
		for(i=0;i<13;i++){
			if(maxdecross+i>=0){
				if(field[maxdecross+i][i]!=0&&field[maxdecross+i][i]==field[maxdecross+i+1][i+1]){
					count++;
					if(count==4){
						return 1;
					}
				}else{count=0;}
			}
		}
		return 0;
	}
	
	
	public void stoneHasamu(int gx, int gy){
		
		if(field[gx][gy]==field[gx][gy+3]&&field[gx][gy+1]==field[gx][gy+2]&&field[gx][gy+1]!=0&&field[gx][gy]!=field[gx][gy+1]){
			removeStone(gx,gy+1);
			removeStone(gx,gy+2);
		}
		if(gy-3>=0){
		if(field[gx][gy]==field[gx][gy-3]&&field[gx][gy-1]==field[gx][gy-2]&&field[gx][gy-1]!=0&&field[gx][gy]!=field[gx][gy-1]){
			removeStone(gx,gy-1);
			removeStone(gx,gy-2);
		}}
		if(field[gx][gy]==field[gx+3][gy]&&field[gx+1][gy]==field[gx+2][gy]&&field[gx+1][gy]!=0&&field[gx][gy]!=field[gx+1][gy]){
			removeStone(gx+1,gy);
			removeStone(gx+2,gy);
		}
		if(gx-3>=0){
		if(field[gx][gy]==field[gx-3][gy]&&field[gx-1][gy]==field[gx-2][gy]&&field[gx-1][gy]!=0&&field[gx][gy]!=field[gx-1][gy]){
			removeStone(gx-1,gy);
			removeStone(gx-2,gy);
		}}
		
		if(field[gx][gy]==field[gx+3][gy+3]&&field[gx+1][gy+1]==field[gx+2][gy+2]&&field[gx+1][gy+1]!=0&&field[gx][gy]!=field[gx+1][gy+1]){
			removeStone(gx+1,gy+1);
			removeStone(gx+2,gy+2);
		}
		if(gy-3>=0){
		if(field[gx][gy]==field[gx+3][gy-3]&&field[gx+1][gy-1]==field[gx+2][gy-2]&&field[gx+1][gy-1]!=0&&field[gx][gy]!=field[gx+1][gy-1]){
			removeStone(gx+1,gy-1);
			removeStone(gx+2,gy-2);
		}}
		if(gx-3>=0){
		if(field[gx][gy]==field[gx-3][gy+3]&&field[gx-1][gy+1]==field[gx-2][gy+2]&&field[gx-1][gy+1]!=0&&field[gx][gy]!=field[gx-1][gy+1]){
			removeStone(gx-1,gy+1);
			removeStone(gx-2,gy+2);
		}}
		if(gx-3>=0&&gy-3>=0){
		if(field[gx][gy]==field[gx-3][gy-3]&&field[gx-1][gy-1]==field[gx-2][gy-2]&&field[gx-1][gy-1]!=0&&field[gx][gy]!=field[gx-1][gy-1]){
			removeStone(gx-1,gy-1);
			removeStone(gx-2,gy-2);
		}}
		
	}
	
	@Override
	public void prepareSoundAndMusic() {
		try {
			goishisnd = SoundFactory.createSoundFromAsset(getBaseActivity().getSoundManager(),getBaseActivity(),"utu.wav");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	//-----------------------------------------------------------connection
	public void connectToServer(){
		
	        try {
	            URI uri = new URI(Constants.URI);
	            mClient = new WebSocketClient(uri) {
	                @Override
	                public void onOpen(ServerHandshake handshake) {
	                    Log.d(TAG, "onOpen");
	                }
	                @Override
	                public void onMessage(final String message) {
	                    Log.d(TAG, "Message:" + message);
	                    parseJson(message);
	                    MainActivity.mHandler.post(new Runnable() {
	                        @Override
	                        public void run() {
	                            Toast.makeText(getBaseActivity(), message, Toast.LENGTH_SHORT).show();
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
	
	
	

}
