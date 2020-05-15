package com.example.activityrecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.flex.FlexDelegate;
import org.tensorflow.lite.DataType;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class RecognizeActionActivity extends AppCompatActivity {
    Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_action);
        try {
            tflite = new Interpreter(loadModel());
            System.out.println("after load model");
        } catch (IOException e) {
            e.printStackTrace();
        }
        classify();
    }
    private MappedByteBuffer loadModel() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("converted_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        System.out.println("in load model");
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public void classify(){
        System.out.println("in classify");
        float[] inputVal = new float[1];
        inputVal[0] = 1;
        float[][] outputval = new float[1][1];
        tflite.run(inputVal,outputval);
        System.out.println("end of classify");

    }
    public void testModel(){
        FlexDelegate delegate = new FlexDelegate();
        Interpreter.Options options = new Interpreter.Options().addDelegate(delegate);

    }


}
