package com.example.trixx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button btnNext, btnPrevious, btnPause;
    TextView songLabel;
    SeekBar songSeekbar;

    static MediaPlayer myMediaPlayer;
    int position;

    String sname;

    ArrayList<File> mySongs;
    Thread updateSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnNext = (Button)findViewById(R.id.next);
        btnPrevious = (Button)findViewById(R.id.previous);
        btnPause = (Button)findViewById(R.id.pause);
        songLabel = (TextView)findViewById(R.id.songLabel);
        songSeekbar = (SeekBar)findViewById(R.id.seekBar);


        // update seekbar
        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = myMediaPlayer.getDuration();  // know selected song duration.
                int currentPosition = 0;

                while(currentPosition < totalDuration){
                    try {
                        sleep(500); // step of seek bar
                        currentPosition = myMediaPlayer.getCurrentPosition();
                        songSeekbar.setProgress(currentPosition);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }




            }
        };

        if(myMediaPlayer != null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");

        sname = mySongs.get(position).getName().toString();

        String songName = intent.getStringExtra("songname");

        songLabel.setText(songName); // display song title
        songLabel.setSelected(true);

        position = bundle.getInt("pos", 0);

        Uri u = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        myMediaPlayer.start();
        songSeekbar.setMax(myMediaPlayer.getDuration());

        updateSeekBar.start();  // increasing seekBar(made seekBar movable, when song play) <------

        // change color of seek-bar
        songSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimarySurface), PorterDuff.Mode.MULTIPLY);
        songSeekbar.getThumb().setColorFilter(getResources().getColor(R.color.colorPink), PorterDuff.Mode.SRC_IN);


        // After playing song, Now apply listener on seekBar
        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress()); // apply change on duration, when seek bar move.
            }
        });


        // At last, Apply listener on pause,next and previous button.
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songSeekbar.setMax(myMediaPlayer.getDuration());

                if(myMediaPlayer.isPlaying()){
                    btnPause.setBackgroundResource(R.drawable.icon_play);
                    myMediaPlayer.pause();
                }else{
                    btnPause.setBackgroundResource(R.drawable.icon_pause);
                    myMediaPlayer.start();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myMediaPlayer.stop(); // first stop the music
                myMediaPlayer.release();
                // then, increase position of song
                position = (position+1) % mySongs.size(); // also consider boundary cases.

                Uri u = Uri.parse(mySongs.get(position).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).getName().toString();
                songLabel.setText(sname);

                myMediaPlayer.start(); // then, start song
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myMediaPlayer.stop(); // first stop the music
                myMediaPlayer.release();
                // then, decrease position of song
                position = ((position-1) < 0) ? (mySongs.size()-1):position-1; // also consider boundary case less than 0.

                Uri u = Uri.parse(mySongs.get(position).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).getName().toString();
                songLabel.setText(sname);

                myMediaPlayer.start(); // then, start song
            }
        });

    }


    // Perform action on go back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}