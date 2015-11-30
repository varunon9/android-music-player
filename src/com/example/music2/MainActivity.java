package com.example.music2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements MediaPlayerControl, SensorEventListener{
    
	private ArrayList<Song> songList;
	private ListView songView;
	static MainActivity mainActivity;
	private MusicController controller;
	Cursor musicCursor;
	MusicHelper musicHelper;
	int songPosition=0;
	//sensor variables
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private float[] mGravity;
	private float mAccel;
	private float mAccelCurrent;
	private float mAccelLast;
	//used to control sensor when phone is sleep
	int unregisterSensorFlag = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//sensor initialization begins here
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mAccel = 0.00f;
		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;
		//sensor initialization ends
		mainActivity=this;
		songView=(ListView)findViewById(R.id.song_list);
		songList=new ArrayList<Song>();
		getSongList();
		
		Collections.sort(songList,new Comparator<Song>(){
			public int compare(Song a,Song b){
				return a.getTitle().compareTo(b.getTitle());
			}
		});
		//ArrayAdapter<Song> adapter=new ArrayAdapter<Song>(this,android.R.layout.simple_list_item_1,songList);
		//songView.setAdapter(adapter);
		
		SongAdapter songAdt = new SongAdapter(this, songList);
		songView.setAdapter(songAdt);
		musicHelper=new MusicHelper();
		musicHelper.getSongList(songList);
		controller=new MusicController(this);
		

	    songView.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			songPosition=position;
			
			try{
			    Song song=(Song)songView.getItemAtPosition(position);
			    musicHelper.getSong(song);
			    
			    musicHelper.playSong();
			   
			   
			}
			catch(Exception e){
				Toast.makeText(MainActivity.this, "Error in playing", Toast.LENGTH_LONG).show();
			}
			try{
				setController();
			   	
			}
			catch(Exception e){
				Toast.makeText(MainActivity.this, "Error in media control", Toast.LENGTH_LONG).show();
			}
			//Intent intent=new Intent(MainActivity.this,MusicService.class);
			
			//startService(intent);
		}
		
	});
	
	
	
	
	}
    public void getSongList(){
    	ContentResolver musicResolver=getContentResolver();
    	Uri musicUri=android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        musicCursor=musicResolver.query(musicUri, null, null, null, null);
    	if(musicCursor!=null&&musicCursor.moveToFirst()){
    		int titleColumn=musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
    		int idColumn=musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
    		int artistColumn=musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
    		int dataColumn=musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA);
    		int durationColumn=musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);
    		do{
    			long thisId=musicCursor.getLong(idColumn);
    			String thisTitle=musicCursor.getString(titleColumn);
    			String thisArtist=musicCursor.getString(artistColumn);
    			String thisData=musicCursor.getString(dataColumn);
    			int thisDuration=musicCursor.getInt(durationColumn);
    			songList.add(new Song(thisId,thisTitle,thisArtist,thisData,thisDuration));
    		}while(musicCursor.moveToNext());
    	}
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_offSensor) {
			//unregisterSensorFlag = 0;
			Toast.makeText(MainActivity.this, "this facility is not available", Toast.LENGTH_LONG).show();
			return true;
		}
		if (id == R.id.action_pause) {
			pause();
			return true;
		}
		if (id == R.id.action_play) {
			start();
			return true;
		}
		if (id == R.id.action_info) {
			Toast.makeText(MainActivity.this, "Developed by Varun(1130328, NIT KKR)", Toast.LENGTH_LONG).show();
			return true;
		}
		if (id == R.id.action_previous) {
			songPosition--;
			  if(songPosition<0){
			      songPosition=songList.size()-1;
			  }
			  //Song song=songList.get(songPosition);
			  musicHelper.playPrevious(songPosition);
			return true;
		}
		if (id == R.id.action_next) {
			  songPosition++;
				if(songPosition==songList.size()){
					songPosition=0;
				}
			  //Song song=songList.get(songPosition);
			  musicHelper.playNext(songPosition);
			return true;
		}
		if (id == R.id.action_deleteCurrentSong) {
			if(songPosition != 0) {
				Song song=(Song)songView.getItemAtPosition(songPosition);
				File file = new File(song.getData());
				boolean deleted = file.delete();
				songList.remove(songPosition);
				if(deleted)
				    Toast.makeText(MainActivity.this, song.getTitle() + " deleted", Toast.LENGTH_LONG).show();
				Toast.makeText(MainActivity.this, song.getData() , Toast.LENGTH_LONG).show();
				songPosition++;
				if(songPosition==songList.size()){
					songPosition=0;
				}
				song=songList.get(songPosition);
				musicHelper.playNext(songPosition);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void start() {
		//mp.start();// TODO Auto-generated method stub
		musicHelper.start();
		
	}
	@Override
	public void pause() {
		//mp.pause();// TODO Auto-generated method stub
		musicHelper.pause();
		
	}
	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		//return mp.getDuration();
		return musicHelper.getDuration();
	}
	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		//return mp.getCurrentPosition();
		return musicHelper.getCurrentPosition();
	}
	@Override
	public void seekTo(int pos) {
		//mp.seekTo(pos);// TODO Auto-generated method stub
		musicHelper.seekTo(pos);
		
	}
	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		//return mp.isPlaying();
		return musicHelper.isPlaying();
	}
	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}
	/*
	@Override
	public void onStop(){
		Toast.makeText(this,"onStop called",Toast.LENGTH_LONG).show();
		super.onStop();
		//musicHelper.stop();
		//musicHelper.release();
	}
	*/
	private void setController(){
		  //set the controller up
		//controller = new MusicController(this);
		controller.setPrevNextListeners(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				  Toast.makeText(MainActivity.this, "Playing next", Toast.LENGTH_LONG).show();
				  songPosition++;
					if(songPosition==songList.size()){
						songPosition=0;
					}
				  //Song song=songList.get(songPosition);
				  musicHelper.playNext(songPosition);
				  //showNotification(song);
		
			  }
			}, new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				  Toast.makeText(MainActivity.this, "Playing previous", Toast.LENGTH_LONG).show();
				  songPosition--;
				  if(songPosition<0){
				      songPosition=songList.size()-1;
				  }
				  //Song song=songList.get(songPosition);
				  musicHelper.playPrevious(songPosition);
				  //showNotification(song);
			  }
		});
		controller.setMediaPlayer(this);
		controller.setAnchorView(findViewById(R.id.song_list));
		controller.setEnabled(true);
		controller.show();
	}
	public void showNotification(Song song){
		String songName=song.getTitle();
		String artistName=song.getArtist();
		NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("deprecation")
		Notification notification=new Notification(R.drawable.ic_launcher,"Playing Music",System.currentTimeMillis());
		notification.defaults=notification.DEFAULT_LIGHTS;
		notification.flags=notification.FLAG_AUTO_CANCEL;
		Intent intent=new Intent(this,MainActivity.class);
		PendingIntent p=PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
		notification.setLatestEventInfo(MainActivity.this, songName, artistName, p);
		nm.notify(0, notification);
	}
	public static MainActivity getInstanceOfMainActivity(){
		return mainActivity;
	}
	//sensor methods goes here
	public void onResume() {
	    super.onResume();
	    sensorManager.registerListener(this, accelerometer,
	        SensorManager.SENSOR_DELAY_UI);
	}
	@Override
	protected void onPause() {
	    super.onPause();
	    if(unregisterSensorFlag == 1) {
	    	sensorManager.unregisterListener(this);
	    }
	}
	public void onSensorChanged(SensorEvent event) {
	    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
	        mGravity = event.values.clone();
	        // Shake detection
	        float x = mGravity[0];
	        float y = mGravity[1];
	        float z = mGravity[2];
	        mAccelLast = mAccelCurrent;
	        mAccelCurrent = FloatMath.sqrt(x*x + y*y + z*z);
	        float delta = mAccelCurrent - mAccelLast;
	        mAccel = mAccel * 0.9f + delta;
	            // Make this higher or lower according to how much
	            // motion you want to detect
	        if(mAccel > 3){ 
	            Toast.makeText(MainActivity.this, "Next song", Toast.LENGTH_SHORT).show();
	            songPosition++;
				if(songPosition==songList.size()){
					songPosition=0;
				}
			    Song song=songList.get(songPosition);
			    musicHelper.playNext(songPosition);
	        }
	    }

	}
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    // required method
	}
}
