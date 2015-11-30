package com.example.music2;

import android.content.Context;
import android.widget.MediaController;
//This controller has been created to Override the hide method so that it does not get hide after 3 seconds.
public class MusicController extends MediaController {
	 
	  public MusicController(Context c){
	    super(c);
	  }
	 /*
	  public void hide(){
		  super.hide();
	  }
	 */
	 
	}
