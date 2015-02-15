package com.nazunasoft.gomoku;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;



public class MainScene extends KeyListenScene implements IOnSceneTouchListener {
    private static final String TAG = "MainScene";
	Sprite[] goishi = new Sprite[300];
	Sprite goban;
	Sprite pregoishi;
	Sprite gauge;
	Sprite backgauge;
	Sprite back;
	Sprite bou;
	private Sound goishisnd;
	private Sound ishitorusnd;
	private Sound startsnd;
	private Sound winsnd;
	static int FIELD=11;
	static int TIME=600;
	static int GshiftY = 200;
	static int Gpad=40;
	static int Ggridlen=40;
	int Mybw=0;//sente(black)=0 gote(white)=1
	int step=0;
	int[][] field = new int[FIELD*9][FIELD*9];//noStone=0,black=1,white=2
	int getStone=0;
	int torareStone=0;
	int connectionID;
	int isWait=2;
	int wt=0;
	int touchEnable;
	int timerCounter;
	int isGamed=0;
	private Text stateText;
	private Text timerText;
	private Text ishiText;
	private Text gameText;
	
    static public WebSocketClient mClient;
	public MainScene(MultiSceneActivity baseActivity){
		super(baseActivity);
		init();
		registerUpdateHandler(updateHandler);
	}
	
	public void init(){
		isGamed=0;
		goishi = new Sprite[300];
		field = new int[FIELD*9][FIELD*9];
		timerCounter=TIME;
		step=0;
		getStone=0;
		torareStone=0;
		touchEnable=0;
		wt=0;
		isWait=2;
		//--------------------
        connectToServer();
		setOnSceneTouchListener(this);
		//-------------------
		prepareGraphics();
		setBackground();
		prepareText();
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		if(touchEnable==1){
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
		}
		
		return true;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		return false;
	}
	
	//----------------------------------------------------------prepareassets

	public void prepareGraphics() {
		 back =  getBaseActivity().getResourceUtil().getSprite("back.jpg");
		 back.setZIndex(-3);
		 goban =  getBaseActivity().getResourceUtil().getSprite("goban.jpg");
		 goban.setZIndex(-2);
		 gauge = getBaseActivity().getResourceUtil().getSprite("gauge1.png");
		 gauge.setZIndex(0);
		 bou = getBaseActivity().getResourceUtil().getSprite("bou.png");
		 bou.setZIndex(0);
		 backgauge = getBaseActivity().getResourceUtil().getSprite("gauge1.png");
		 backgauge.setAlpha(0.5f);
		 backgauge.setZIndex(-1);
		 
		for(int i=0;i<300;i=i+2){
				goishi[i]=getBaseActivity().getResourceUtil().getSprite("black_s.png");
				goishi[i+1]=getBaseActivity().getResourceUtil().getSprite("white_s.png");
		}
		
	}
	
	public void setBackground(){
		back.setPosition(0,0);
		attachChild(back);
		goban.setPosition(0,GshiftY);
		attachChild(goban);
		backgauge.setPosition(0,GshiftY+goban.getHeight()+35);
		attachChild(backgauge);
		gauge.setPosition(0,GshiftY+goban.getHeight()+35);
		attachChild(gauge);
		bou.setPosition(400,147);
		attachChild(bou);
	}
	
	public void prepareText(){
		Texture texture = new BitmapTextureAtlas(getBaseActivity().getTextureManager(), 480, 800,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		Typeface tegaki = Typeface.createFromAsset(getBaseActivity().getAssets(), "font/851tegaki.ttf");
		Font blackfont = new Font(getBaseActivity().getFontManager(), texture, tegaki, 22, true, Color.BLACK);
		getBaseActivity().getTextureManager().loadTexture(texture);
		getBaseActivity().getFontManager().loadFont(blackfont);
		stateText = new Text(30, 30, blackfont, "finding an opponent...", 100,
				new TextOptions(HorizontalAlign.LEFT), getBaseActivity()
						.getVertexBufferObjectManager());
		stateText.setZIndex(2);
		
		ishiText = new Text(30, 60, blackfont, "got stones:0 stolen stones:0", 100,
				new TextOptions(HorizontalAlign.LEFT), getBaseActivity()
						.getVertexBufferObjectManager());
		ishiText.setZIndex(2);
		
		gameText = new Text(30, 90, blackfont, "", 100,
				new TextOptions(HorizontalAlign.LEFT), getBaseActivity()
						.getVertexBufferObjectManager());
		gameText.setZIndex(2);
		
		timerText = new Text(24, gauge.getY()-29, blackfont, "Timer Gauge", 100,
				new TextOptions(HorizontalAlign.LEFT), getBaseActivity()
						.getVertexBufferObjectManager());
		timerText.setZIndex(2);

		attachChild(ishiText);
		attachChild(gameText);
		attachChild(stateText);
		attachChild(timerText);
	}
	
	public void popupDialogue(final String message){
        MainActivity.mHandler.post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(getBaseActivity())
                .setTitle(message)
                .setMessage("Want to try again?")
                .setPositiveButton(
                  "Yes", 
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                      DialogInterface dialog, int which) {  
                    	init();
                    }
                  })
                .setNegativeButton("No", null)
                .show();
            	
            }
        });
	}
	

	@Override
	public void prepareSoundAndMusic() {
		try {
			goishisnd = SoundFactory.createSoundFromAsset(getBaseActivity().getSoundManager(),getBaseActivity(),"utu.wav");
			ishitorusnd = SoundFactory.createSoundFromAsset(getBaseActivity().getSoundManager(),getBaseActivity(),"ishitoru.wav");
			startsnd = SoundFactory.createSoundFromAsset(getBaseActivity().getSoundManager(),getBaseActivity(),"start.wav");
			winsnd = SoundFactory.createSoundFromAsset(getBaseActivity().getSoundManager(),getBaseActivity(),"win.mp3");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//-------------------------------------------------------------solveGrid
	
	public void solveGrid(float x, float y){
		int gx = (int)((x-Gpad)/Ggridlen);
		int gy = (int)((y-Gpad-GshiftY)/Ggridlen);
		Log.d("grid", "[" + gx+","+gy+"]");
		if(gx>=0&&gy>=0&&gx<=12&&gy<=12&&field[gx][gy]==0){
			sendStone(gx,gy);
			setStone(gx,gy,Mybw);
		}
	}
	//----------------------------------------------------------send&receive
	public void sendStone(int gx, int gy){
		JSONObject data = new JSONObject();
		try {
			data.put("bw", Mybw);
			data.put("gx", gx);
			data.put("gy", gy);
			data.put("id", connectionID);
		} catch (JSONException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		
		try {
			mClient.send(data.toString());
		} catch (NotYetConnectedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
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
			if(bw!=-1&&id!=connectionID&&id!=-1&&gx!=-2){

		        Log.d("debug", "4");
				setStone(gx,gy,bw);
				timerCounter=TIME;
		        Log.d("debug", "4");
			}
			if(bw==-1){//OnConnect
				connectionID = id;
	            MainActivity.mHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                	Toast.makeText(getBaseActivity(), "Got Connection. Please wait...", Toast.LENGTH_SHORT).show();
	                }
	            });
			}
	        Log.d("debug", "4");
			if(id==-1){//OnStartGame
				startsnd.play();
				Mybw=bw;
	            MainActivity.mHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                	Toast.makeText(getBaseActivity(), "Game Start !", Toast.LENGTH_SHORT).show();
	                }
	            });
				if(Mybw==0){//black is sente
					touchEnable=1;isWait=1;}else{touchEnable=0;isWait=0;}
			}
	        Log.d("debug", "4");
			if(gx==-2&&id!=connectionID){//resigned
				Game(Mybw);
				if(gameText.getText()=="")gameText.setText("the opponent has disconnected.");
			}
	        Log.d("debug", "4");
		} catch (JSONException e) {
			// TODO 自動生成された catch ブロック
	        Log.d("debug", "e");
			e.printStackTrace();
		}

        Log.d("debug", "4");
	}
	
	//----------------------------------------------------------------UX

	public void preStone(float x,float y){
		int gx = (int)((x-Gpad)/Ggridlen);
		int gy = (int)((y-Gpad-GshiftY)/Ggridlen);
		if(gx>=0&&gy>=0&&gx<=FIELD-1&&gy<=FIELD-1&&field[gx][gy]==0){
			if(Mybw==0){
				 pregoishi=getBaseActivity().getResourceUtil().getSprite("black_s.png");
				 }else if(Mybw==1){
				 pregoishi=getBaseActivity().getResourceUtil().getSprite("white_s.png");
			}
			pregoishi.setPosition((float)(Gpad+(gx-0.5)*Ggridlen),(float)(GshiftY+Gpad+(gy-0.5)*Ggridlen));
			pregoishi.setAlpha(0.3f);
			attachChild(pregoishi);
		}
	}
	
	public void setStone(int gx,int gy, int bw){
		field[gx][gy]=(bw+1);
		goishi[step].setZIndex(1);
		goishi[step].setPosition((float)(Gpad+(gx-0.5)*Ggridlen),(float)(GshiftY+Gpad+(gy-0.5)*Ggridlen));
		attachChild(goishi[step]);
		goishi[step].registerEntityModifier(new FadeInModifier(0.3f));
		stoneHasamu(gx,gy);
		step++;
		if(bw!=Mybw){touchEnable=1;isWait=1;}else{touchEnable=0;isWait=0;}
		if(checkGame(gx,gy)==1){
				Game(bw);
				gameText.setText("5-stones!");
				}
		if(getStone==10||torareStone==10){
				Game(bw);
				gameText.setText("10-steels!");
				}
		goishisnd.play();
	}
	
	public void removeStone(int gx,int gy){
		sleep(100);
		ishitorusnd.play();
		Log.d("removest",""+gx+":"+gy);
		if(field[gx][gy]==Mybw+1){
			torareStone++;}else{getStone++;}
		field[gx][gy]=0;
		for(int i=0;i<step;i++){
			if(goishi[i].getX()==(float)(Gpad+(gx-0.5)*Ggridlen)&&goishi[i].getY()==(float)(GshiftY+Gpad+(gy-0.5)*Ggridlen)){
			if(goishi[i].hasParent()){
						goishi[i].setVisible(false);
				}
			}
		}
		ishiText.setText("got stones:"+getStone+" stolen stones:"+torareStone);
	}
	
	
	// --------------------------------------------------------------------timer
	int rot;
	public TimerHandler updateHandler = new TimerHandler(1f / 60f, true,
			new ITimerCallback() {
				public void onTimePassed(TimerHandler pTimerHandler) {

			        Log.d("debug", "5");
					if(isWait==1){

				    Log.d("debug", "6");
					wt=0;
					stateText.setText("your turn.");
			        Log.d("debug", "5");
					if(timerCounter>0){timerCounter--;}
					if(timerCounter>=0){gauge.setWidth(480 * timerCounter/TIME);}
					if(timerCounter==0){
						if(Mybw==0){Game(1);
						}else{Game(0);}
						sendStone(-2,-2);
						}
					}else if(isWait==0){
				        Log.d("debug", "7");
						bou.setRotation(wt*1.56f);
						wt++;
						if(wt==1){stateText.setText("waiting the opponent's turn.");}	
						if(wt==TIME/5){stateText.setText("waiting the opponent's turn...");}	
						if(wt==2*TIME/5){stateText.setText("waiting the opponent's turn.....");}	
						if(wt==3*TIME/5){stateText.setText("waiting the opponent's turn.......");}	
						if(wt==4*TIME/5){stateText.setText("waiting the opponent's turn.........");}	
					}else if(isWait==2){
						bou.setRotation(rot*1.91f);
						rot++;
					}
				}		
	});
	
	//---------------------------------------------------------------gameAlgo
	public void Game(int bw){
		if(isGamed==0){isGamed=1;
		mClient.close();
		if(bw==Mybw){//win
            MainActivity.mHandler.post(new Runnable() {
                @Override
                public void run() {
                	Toast.makeText(getBaseActivity(), "You Win", Toast.LENGTH_SHORT).show();
                }
            });
    		stateText.setText(" YOU WIN !!!");
    		popupDialogue(" YOU WIN !");
    		winsnd.play();
		}else{//lose
            MainActivity.mHandler.post(new Runnable() {
                @Override
                public void run() {
                	Toast.makeText(getBaseActivity(), "You Lose", Toast.LENGTH_SHORT).show();
                }
            });
    		stateText.setText(" YOU LOSE...");
    		popupDialogue(" YOU LOSE...");
		}
		touchEnable=0;
		timerCounter=0;
		isWait=2;
		}
	}
	
	public int checkGame(int gx, int gy){//find 5-lined stones
		int i=0;
		int count=0;
		for(i=0;i<FIELD-1;i++){
			if(field[gx][i]!=0&&field[gx][i]==field[gx][i+1]){
				count++;
				if(count==4){
					 return 1;
				}
			}else{count=0;}
		}
		count=0;
		for(i=0;i<FIELD-1;i++){
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
		for(i=0;i<FIELD;i++){
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

		if(gy+3<=FIELD-1){
		if(field[gx][gy]==field[gx][gy+3]&&field[gx][gy+1]==field[gx][gy+2]&&field[gx][gy+1]!=0&&field[gx][gy]!=field[gx][gy+1]){
			removeStone(gx,gy+1);
			removeStone(gx,gy+2);
		}}
		if(gy-3>=0){
		if(field[gx][gy]==field[gx][gy-3]&&field[gx][gy-1]==field[gx][gy-2]&&field[gx][gy-1]!=0&&field[gx][gy]!=field[gx][gy-1]){
			removeStone(gx,gy-1);
			removeStone(gx,gy-2);
		}}
		if(gx+3<=FIELD-1){
		if(field[gx][gy]==field[gx+3][gy]&&field[gx+1][gy]==field[gx+2][gy]&&field[gx+1][gy]!=0&&field[gx][gy]!=field[gx+1][gy]){
			removeStone(gx+1,gy);
			removeStone(gx+2,gy);
		}}
		if(gx-3>=0){
		if(field[gx][gy]==field[gx-3][gy]&&field[gx-1][gy]==field[gx-2][gy]&&field[gx-1][gy]!=0&&field[gx][gy]!=field[gx-1][gy]){
			removeStone(gx-1,gy);
			removeStone(gx-2,gy);
		}}
		if(gx+3<=FIELD-1&&gy+3<=FIELD-1){
		if(field[gx][gy]==field[gx+3][gy+3]&&field[gx+1][gy+1]==field[gx+2][gy+2]&&field[gx+1][gy+1]!=0&&field[gx][gy]!=field[gx+1][gy+1]){
			removeStone(gx+1,gy+1);
			removeStone(gx+2,gy+2);
		}}
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

		try {
			Thread.sleep((long) 0.3f);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
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
	
	 public synchronized void sleep(long msec)
	    {	
	    	try
	    	{
	    		wait(msec);
	    	}catch(InterruptedException e){}
	    }


}
