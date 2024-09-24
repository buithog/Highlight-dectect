package com.example.music_highlight_detect;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.music_highlight_detect.Model.Model_TFlite;
import com.example.music_highlight_detect.Pre_Procesing.PreProcessing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private Model_TFlite modelTFLite;
    private PreProcessing preProcessing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // load model
        AssetManager assetManager = getAssets();
        String modelPath = "music_highlighter.tflite";
        try {
            for (String file : assetManager.list("")) {
                Log.d("Assets", "File in assets: " + file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        preProcessing = new PreProcessing();
        String audioFilePath = "BAK.wav";
        Log.d("File Path", audioFilePath);
        try {
            // Test audioRead
            float[][][] melSpectrogram = preProcessing.audioRead(audioFilePath,this);
          //  Log.d(TAG, "Mel-Spectrogram: " + arrayToString(melSpectrogram));

            int nChunk = 3;
            float[][][] chunkedSpectrogram = preProcessing.chunk(melSpectrogram, nChunk);

            //   Log.d(TAG, "Chunked Spectrogram: " + arrayToString(chunkedSpectrogram));

            // Test positionalEncoding
            int batchSize = 1;
            int nPos = nChunk;
            int dPos = 256;
            float[][][] positionalEncoding = preProcessing.positionalEncoding(batchSize, nPos, dPos);
        //    Log.d(TAG, "Positional Encoding: " + arrayToString(positionalEncoding));

        } catch (Exception e) {
         //   Log.e(TAG, "Error processing audio file", e);
        }


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

    @NonNull
    private String arrayToString(float[][][] array) {
        StringBuilder sb = new StringBuilder();
        for (float[][] matrix : array) {
            sb.append(arrayToString(matrix)).append("\n");
        }
        return sb.toString();
    }
}