package com.example.music2;

import java.util.ArrayList;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.widget.Toast;

public class MusicHelper {
	private Context context;
	Song song;
	ArrayList<Song> songs;
	int songPosition=0;
	MediaPlayer player=new MediaPlayer();
	public MusicHelper(Context context){
		this.context=context;
		Toast.makeText(context, "Helper object created", Toast.LENGTH_LONG).show();
	}
	public MusicHelper(){
		
	}
	public void getSong(Song song){
    	this.song=song;
    }
	public void getSongList(ArrayList<Song> songs){
		this.songs=songs;
		
	}
	public int getCurrentPosition(){
		return player.getCurrentPosition();
	}
		 
	public int getDuration(){
		return player.getDuration();
	}
		 
	public boolean isPlaying(){
		return player.isPlaying();
	}
		 
	public void pause(){
        player.pause();
	}
		 
	public void seekTo(int posn){
		player.seekTo(posn);
	}
		 
	public void start(){
		player.start();
	}
	public void stop(){
		player.stop();
	}
	public void release(){
		player.release();
	}
	public void reset(){
		player.reset();
	}
	public void prepare(){
		try{
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.prepare();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void setDataSource(String path){
		try{
			player.setDataSource(path);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void playSong(){
		String path=song.getData();
		if(player.isPlaying()){
	    	player.stop();
	    	player.reset();
	    	try{
	    		player.setDataSource(path);
	    		player.setOnPreparedListener(new OnPreparedListener(){

					@Override
					public void onPrepared(MediaPlayer mp) {
						player.start();// TODO Auto-generated method stub
						
					}
	    			
	    		});
			    player.prepareAsync();
			    
			    
	    	}
	    	catch (Exception e){
	    		e.printStackTrace();
	    		Log.e("Error in playing audio MusicService", "method is playSong");
	    		//Toast.makeText(context, "Playing error", Toast.LENGTH_LONG).show();
	    	}
	    	
	    }
	    else{
	    	try{
	    		player.setDataSource(path);
	    		player.setOnPreparedListener(new OnPreparedListener(){

					@Override
					public void onPrepared(MediaPlayer mp) {
						player.start();// TODO Auto-generated method stub
						
					}
	    			
	    		});
			    player.prepareAsync();
	    	}
	    	catch (Exception e){
	    		e.printStackTrace();
	    		Log.e("Error in playing audio MusicService", "method is playSong");
	    		//Toast.makeText(context, "Playing error", Toast.LENGTH_LONG).show();
	    	}
	    }
		player.setOnErrorListener(new OnErrorListener(){

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.e("Error occured",String.valueOf(what));// TODO Auto-generated method stub
				mp.reset();
				songPosition++;
				if(songPosition==songs.size()){
					songPosition=0;
				}
				playNext(songPosition);
				return true;
			}
	    	
	    });
		
		player.setOnCompletionListener(new OnCompletionListener(){

			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.reset();
				songPosition++;
				if(songPosition==songs.size()){
					songPosition=0;
				}
				playNext(songPosition);
			}
			
		});
		MainActivity.getInstanceOfMainActivity().showNotification(song);
	}
	public void stopSong(){
		player.stop();
		player.release();
		player.reset();
	}
	public void playNext(int songPosition){
		this.songPosition=songPosition;
		Song song=songs.get(songPosition);
		getSong(song);
	    
	    playSong();
		
	}
	public void playPrevious(int songPosition){
		this.songPosition=songPosition;
		
		Song song=songs.get(songPosition);
		getSong(song);
	    
	    playSong();
		
	}
	
	
}
