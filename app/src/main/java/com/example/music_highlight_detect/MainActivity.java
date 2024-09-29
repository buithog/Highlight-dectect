package com.example.music_highlight_detect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_AUDIO_FILE = 1;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private Button button, applyButton, detectButton, playButton;
    private TextView resultTextView, textViewTime, audioFileName, currentTimeAudio, timeAudio;

    private Uri audioUri;
    private boolean isFileSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        applyButton = findViewById(R.id.apply);
        detectButton = findViewById(R.id.dectect);
        seekBar = findViewById(R.id.seekBar);
        resultTextView = findViewById(R.id.result);
        textViewTime = findViewById(R.id.textView);
        audioFileName = findViewById(R.id.audioFileName);
        currentTimeAudio = findViewById(R.id.curentTimeAudio);
        timeAudio = findViewById(R.id.timeAudio);
        playButton = findViewById(R.id.play);
        playButton.setBackgroundResource(R.drawable.baseline_play_arrow_24);
        button.setOnClickListener(v -> openFileChooser());
        playButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playButton.setBackgroundResource(R.drawable.baseline_play_arrow_24);
                } else {
                    mediaPlayer.start();
                    playButton.setBackgroundResource(R.drawable.baseline_pause_24);
                    updateSeekBar();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    currentTimeAudio.setText(formatTime(mediaPlayer.getCurrentPosition()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                currentTimeAudio.setText(formatTime(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this, 1000);  // Lặp lại sau 1 giây
            }
        }
    };

    private void updateSeekBar() {
        handler.post(updateSeekBarRunnable);  // Bắt đầu cập nhật seekbar
    }



    @NonNull
    private String arrayToString(float[][] array) {
        StringBuilder sb = new StringBuilder();
        for (float[] row : array) {
            for (float value : row) {
                sb.append(value).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_FILE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                audioUri = data.getData();
                isFileSelected = true;
                displayFileName(audioUri);
                playAudio(audioUri);
                displayFileName(audioUri);
                playButton.setBackgroundResource(R.drawable.baseline_pause_24);
            }
        }
    }

    private void displayFileName(Uri audioUri) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this, audioUri);
        String fileName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if (fileName == null) {
            fileName = "Tên không xác định";
        }
        audioFileName.setText("Tên bài hát: " + fileName);
    }

    private String formatTime(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void playAudio(Uri audioUri) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, audioUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            timeAudio.setText(formatTime(mediaPlayer.getDuration()));
            updateSeekBar();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to play audio", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBarRunnable);
    }
}
