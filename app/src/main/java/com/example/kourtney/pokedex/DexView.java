package com.example.kourtney.pokedex;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Kourtney on 11/29/2015.
 */
public class DexView extends AppCompatActivity implements MediaController.MediaPlayerControl{

    private MediaPlayer mMediaPlayer;
    private boolean changedFav;

    @Override //http://stackoverflow.com/questions/13194081/how-to-open-a-second-activity-on-click-of-button-in-android-app
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        initSound();
        setDisplay();
        changedFav = false;
    }

    @Override
    protected void onStop() {
        if ( changedFav ) {
            RelativeLayout rl = (RelativeLayout) findViewById( R.id.load_detail );
            rl.setVisibility( RelativeLayout.VISIBLE );
            new UpdateFavs().execute();
        }
        else { stopTwo(); }
    }

    private void stopTwo() {
        RelativeLayout rl = (RelativeLayout) findViewById( R.id.load_detail );
        rl.setVisibility(RelativeLayout.INVISIBLE);
        super.onStop();
        finish();
    }

    private class UpdateFavs extends AsyncTask< Void, Integer, Integer > {
        protected Integer doInBackground( Void... unused ) {
            Pokemon.getInstance().updateFavs();
            mMediaPlayer.release();
            return 0;
        }
        protected void onProgressUpdate( Integer ... progress ) {
        }

        protected void onPostExecute( Integer result) {
            stopTwo();
        }
    }

    public void setDisplay(){

        //http://stackoverflow.com/questions/3648942/dynamic-resource-loading-android
        int id = Pokemon.getInstance().getId();

        TextView numDisplay = (TextView) findViewById(R.id.number_content);
        String idAndName = "#" + Integer.toString(Pokemon.getInstance().getId());
        numDisplay.setText(idAndName);

        TextView nameDisplay = (TextView) findViewById(R.id.name_content);
        nameDisplay.setText(Pokemon.getInstance().getName());

        WebView imageDisplay = (WebView) findViewById(R.id.image);
        imageDisplay.loadUrl("file:///android_res/drawable/image_" + id + ".gif");
        imageDisplay.setInitialScale(500);

        TextView blurbDisplay = (TextView) findViewById(R.id.blurb_content);
        blurbDisplay.setText(Pokemon.getInstance().getBlurb());

        TextView type1Display = (TextView) findViewById(R.id.type_content_1);
        type1Display.setText(Pokemon.getInstance().getType1());

        TextView type2Display = (TextView) findViewById(R.id.type_content_2);
        if ( !(Pokemon.getInstance().getType2()).equals( "x" ) ) {
            type2Display.setText(Pokemon.getInstance().getType2());
        }
        else {
            type2Display.setText("");
        }

        TextView heightDisplay = (TextView) findViewById(R.id.height_content);
        heightDisplay.setText(Pokemon.getInstance().getHeight());

        TextView weightDisplay = (TextView) findViewById(R.id.weight_content);
        weightDisplay.setText(Pokemon.getInstance().getWeight());

        TextView descriptionDisplay = (TextView) findViewById(R.id.description_content);
        descriptionDisplay.setText(Pokemon.getInstance().getDesc());

        if ( Pokemon.getInstance().getFav() ) {
            ImageButton favButton = (ImageButton) findViewById(R.id.favourite_button);
            favButton.setImageResource(R.drawable.closed_poke_ball_sprite);
        } else {
            ImageButton favButton = (ImageButton) findViewById(R.id.favourite_button);
            favButton.setImageResource(R.drawable.open_poke_ball_sprite);
        }
    }

    public void favourite(View v){

        changedFav = true;

        //http://stackoverflow.com/questions/18611947/android-change-image-button-background
        ImageButton favButton = (ImageButton) findViewById(R.id.favourite_button);

        if ( !Pokemon.getInstance().getFav() ) {
            favButton.setImageResource(R.drawable.closed_poke_ball_sprite);
            Pokemon.getInstance().setFav( true );
        } else {
            favButton.setImageResource(R.drawable.open_poke_ball_sprite);
            Pokemon.getInstance().setFav( false );
        }
    }

    private void initSound() {
        int id = Pokemon.getInstance().getId();
        int res = getResources().getIdentifier("sound_" + id, "raw", "com.example.kourtney.pokedex");
        try {
            mMediaPlayer = MediaPlayer.create(this, res);
        }
        catch (Resources.NotFoundException nfe ) {
            mMediaPlayer = null;
            // catch
        }
    }

    public void squeal(View v){
        if ( !(  mMediaPlayer == null ) ) {
            new SquealAs().execute();
        }
    }

    private class SquealAs extends AsyncTask< Void, Integer, Integer > {
         protected Integer doInBackground ( Void... unused ) {
             mMediaPlayer.start();
             return 0;
         }

         protected void onProgressUpdate(Integer... progress) {

         }

         protected void onPostExecute(Integer result) {

         }
    }

    public void start() {
        mMediaPlayer.start();
    }
    public void pause() {
        mMediaPlayer.pause();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void seekTo(int i) {
        mMediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    public int getAudioSessionId( ) {
        return 0;
    }

}
