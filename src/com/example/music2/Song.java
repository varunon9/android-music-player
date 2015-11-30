package com.example.music2;

public class Song {
    private long id;
    private String title;
    private String artist;
    private String data;
    private int duration;
    public Song(long id,String title,String artist,String data,int duration){
    	this.id=id;
    	this.title=title;
    	this.artist=artist;
    	this.data=data;
    	this.duration=duration;
    }
    public long getId(){
    	return id;
    }
    public String getTitle(){
    	return title;
    }
    public String getArtist(){
    	return artist;
    }
    public String getData(){
    	return data;
    }
    public int getDuration(){
    	return duration;
    }
}
