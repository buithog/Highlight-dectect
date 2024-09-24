//package com.example.music_highlight_detect.Model;
//
//import org.tensorflow.lite.DataType;
//import org.tensorflow.lite.Interpreter;
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
//import org.tensorflow.lite.support.common.FileUtil;
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
//import java.io.File;
//import java.io.IOException;
//import java.util.Arrays;
//
//public class TFLiteModel {
//
//    private Interpreter interpreter;
//    private int[] inputShape0;
//    private int[] inputShape1;
//    private int[] inputShape2;
//    private int outputShape;
//
//    public TFLiteModel(String modelPath) throws IOException {
//        // Load the TFLite model
//        interpreter = new Interpreter(FileUtil.loadMappedFile(new File(modelPath)));
//        inputShape0 = interpreter.getInputTensor(0).shape();
//        inputShape1 = interpreter.getInputTensor(1).shape();
//        inputShape2 = interpreter.getInputTensor(2).shape();
//        outputShape = interpreter.getOutputTensor(0).shape()[1];
//    }
//
//    public void extractTFLite(String[] filePaths, int length, boolean saveScore, boolean saveThumbnail, boolean saveWav) throws Exception {
//        for (String filePath : filePaths) {
//            String name = new File(filePath).getName().replace(".wav", "");
//
//            // Read audio and generate spectrogram
//            float[][] spectrogram = audioRead(filePath);
//            int duration = spectrogram.length;
//            int nChunk = duration / 3;
//            float[][][] chunkSpec = chunk(spectrogram, nChunk);
//            float[][] pos = positionalEncoding(1, nChunk, 256);
//
//            // Convert data to float32
//            float[][][] chunkSpecFloat = Arrays.stream(chunkSpec).map(a -> Arrays.stream(a).map(b -> Arrays.copyOf(b, b.length)).toArray(float[][]::new)).toArray(float[][][]::new);
//            float[][] posFloat = Arrays.stream(pos).map(a -> Arrays.copyOf(a, a.length)).toArray(float[][]::new);
//
//            // Resize tensor inputs
//            interpreter.resizeInput(0, posFloat.length, posFloat[0].length);
//            interpreter.resizeInput(2, chunkSpecFloat.length, chunkSpecFloat[0].length, chunkSpecFloat[0][0].length);
//            interpreter.allocateTensors();
//
//            // Set input tensors
//            TensorBuffer inputBuffer0 = TensorBuffer.createFixedSize(inputShape0, DataType.FLOAT32);
//            TensorBuffer inputBuffer1 = TensorBuffer.createFixedSize(inputShape1, DataType.FLOAT32);
//            TensorBuffer inputBuffer2 = TensorBuffer.createFixedSize(inputShape2, DataType.FLOAT32);
//
//            inputBuffer0.loadArray(posFloat);
//            inputBuffer1.loadArray(new int[]{nChunk});
//            inputBuffer2.loadArray(chunkSpecFloat);
//
//            interpreter.setInputTensor(0, inputBuffer0.getBuffer());
//            interpreter.setInputTensor(1, inputBuffer1.getBuffer());
//            interpreter.setInputTensor(2, inputBuffer2.getBuffer());
//
//            // Run inference
//            interpreter.run();
//
//            // Get output tensor
//            TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, outputShape}, DataType.FLOAT32);
//            interpreter.getOutputTensor(0).copyTo(outputBuffer.getFloatArray());
//
//            float[] attnScore = outputBuffer.getFloatArray();
//            // Process results
//            attnScore = Arrays.copyOf(attnScore, attnScore.length + (duration % 3));
//            attnScore = attnScore / Arrays.stream(attnScore).max().getAsFloat();
//
//            if (saveScore) {
//                // Save score
//                saveArrayToFile(name + "_score_lite.npy", attnScore);
//            }
//
//            float[] cumsumAttnScore = cumsum(attnScore);
//            float[] highlightedScore = new float[attnScore.length - length + 1];
//            for (int i = 0; i < highlightedScore.length; i++) {
//                highlightedScore[i] = cumsumAttnScore[i + length] - cumsumAttnScore[i];
//            }
//            int index = indexOfMax(highlightedScore);
//            int[] highlight = {index, index + length};
//
//            if (saveThumbnail) {
//                // Save highlight
//                saveArrayToFile(name + "_highlight_lite.npy", new float[]{highlight[0], highlight[1]});
//            }
//
//            if (saveWav) {
//                // Save audio file
//                saveAudioToFile(name + "_audio_lite.mp3", extractAudio(filePath, highlight));
//            }
//        }
//    }
//
//    private void saveArrayToFile(String fileName, float[] array) throws IOException {
//        // Implement saving array to file
//    }
//
//    private float[][] audioRead(String filePath) {
//        // Implement audio reading and return mel spectrogram
//        return new float[0][0];
//    }
//
//    private float[][][] chunk(float[][] spectrogram, int nChunk) {
//        // Implement chunking
//        return new float[0][][];
//    }
//
//    private float[][] positionalEncoding(int batchSize, int nPos, int dPos) {
//        // Implement positional encoding
//        return new float[0][0];
//    }
//
//    private float[] cumsum(float[] array) {
//        // Implement cumulative sum
//        return new float[0];
//    }
//
//    private int indexOfMax(float[] array) {
//        // Implement index of maximum
//        return 0;
//    }
//
//    private byte[] extractAudio(String filePath, int[] highlight) {
//        // Implement audio extraction
//        return new byte[0];
//    }
//
//    private void saveAudioToFile(String fileName, byte[] audio) throws IOException {
//        // Implement saving audio file
//    }
//}
