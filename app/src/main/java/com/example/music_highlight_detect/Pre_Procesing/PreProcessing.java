package com.example.music_highlight_detect.Pre_Procesing;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.jlibrosa.audio.JLibrosa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class PreProcessing {
    public float[][][] chunk(float[][][] incoming, int n_chunk) {
        int inputLength = incoming[0].length;
        int chunkLength = inputLength / n_chunk;
        List<float[][]> outputs = new ArrayList<>();

        for (int i = 0; i < incoming.length; i++) {
            for (int j = 0; j < n_chunk; j++) {
                float[][] chunk = new float[incoming[i].length][incoming[i][0].length];
                for (int k = 0; k < incoming[i].length; k++) {
                    System.arraycopy(incoming[i][k], j * chunkLength, chunk[k], 0, chunkLength);
                }
                outputs.add(chunk);
            }
        }

        // Convert the list back to a 3D array
        return outputs.toArray(new float[outputs.size()][][]);
    }
    public float[][][] audioRead(String filePath, Context context) throws Exception {
        JLibrosa jLibrosa = new JLibrosa();
        String path = copyAssetToFiles(filePath,context);
        Log.d("path",path);
        float[] audioData = jLibrosa.loadAndRead(path, 22050, -1);
        int sampleRate = 22050; // Setting to a fixed sample rate of 22050 Hz

        float[][] melSpectrogram = jLibrosa.generateMelSpectroGram(audioData, sampleRate, 2048, 512, 128);

        for (int i = 0; i < melSpectrogram.length; i++) {
            for (int j = 0; j < melSpectrogram[i].length; j++) {
                melSpectrogram[i][j] = (float) Math.log(1 + 10000 * melSpectrogram[i][j]);
            }
        }

        float[][][] melSpectrogram3D = new float[1][melSpectrogram.length][melSpectrogram[0].length];
        for (int i = 0; i < melSpectrogram.length; i++) {
            System.arraycopy(melSpectrogram[i], 0, melSpectrogram3D[0][i], 0, melSpectrogram[i].length);
        }

        return melSpectrogram3D;
    }


    public float[][][] positionalEncoding(int batchSize, int nPos, int dPos) {
        float[][] positionEnc = new float[nPos][dPos];

        for (int pos = 0; pos < nPos; pos++) {
            if (pos != 0) {
                for (int j = 0; j < dPos; j++) {
                    double val = pos / Math.pow(10000, (2 * (j / 2)) / (double) dPos);
                    positionEnc[pos][j] = (float) (j % 2 == 0 ? Math.sin(val) : Math.cos(val));
                }
            }
        }

        float[][][] batchPosEnc = new float[batchSize][nPos][dPos];
        for (int i = 0; i < batchSize; i++) {
            for (int pos = 0; pos < nPos; pos++) {
                System.arraycopy(positionEnc[pos], 0, batchPosEnc[i][pos], 0, dPos);
            }
        }

        return batchPosEnc;
    }
    public String copyAssetToFiles(String fileName, Context context) {
        AssetManager assetManager = context.getAssets();
        File filesDir = context.getFilesDir(); // Thư mục files của ứng dụng
        File destinationFile = new File(filesDir, fileName);

        try (InputStream inputStream = assetManager.open(fileName);
             OutputStream outputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[512];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return destinationFile.getAbsolutePath();  // Trả về đường dẫn tới tệp trong thư mục files
    }

}
