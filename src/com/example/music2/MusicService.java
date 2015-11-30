package com.example.music2;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MusicService extends Service{

	private MusicHelper musicHelper;
	//player.setAudioStreamType(AudioManager.STREAM_MUSIC);
	
	public void onCreate(){
		super.onCreate();
		musicHelper=new MusicHelper();
		Toast.makeText(this,"Service created",Toast.LENGTH_LONG).show();
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		 //musicHelper= new MusicHelper(this);
		
		Toast.makeText(this,"Task perform in service",Toast.LENGTH_LONG).show();
		//int res = super.onStartCommand(intent, flags, startId);
		musicHelper.playSong();
		return START_STICKY;
		//return res;
	}
	
    @Override
	public void onDestroy() {
		super.onDestroy();
		musicHelper.stopSong();
		Toast.makeText(this,"Service destroyed",Toast.LENGTH_LONG).show();
	
	}
    
}
