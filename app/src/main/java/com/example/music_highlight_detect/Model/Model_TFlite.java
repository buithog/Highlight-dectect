package com.example.music_highlight_detect.Model;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class Model_TFlite {
    private Interpreter interpreter;

    public Model_TFlite(AssetManager assetManager, String modelPath) throws IOException {
        Interpreter.Options options = new Interpreter.Options();
        interpreter = new Interpreter(loadModelFile(assetManager, modelPath), options);

        // Kiểm tra nếu mô hình được tải thành công
        if (isModelLoaded()) {
            System.out.println("Model loaded successfully.");
        } else {
            System.out.println("Failed to load the model.");
        }
    }

    // Hàm để load tệp mô hình từ thư mục assets
    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    // Hàm kiểm tra xem mô hình đã được load chưa
    public boolean isModelLoaded() {
        return interpreter != null;
    }
}

