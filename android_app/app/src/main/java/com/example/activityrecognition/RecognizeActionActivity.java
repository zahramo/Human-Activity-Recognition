package com.example.activityrecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.flex.FlexDelegate;
import org.tensorflow.lite.DataType;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class RecognizeActionActivity extends AppCompatActivity {

    // options for model interpreter
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();
    // tflite graph
    private Interpreter tflite;
    // holds all the possible labels for model
    private List<String> labelList;
    // holds the selected image data as bytes
    private ByteBuffer videoData = null;
    // holds the probabilities of each label for non-quantized graphs
    private float[][] labelProbArray = null;
    // array that holds the labels with the highest probabilities
    private String[] topLables = null;
    // array that holds the highest probabilities
    private String[] topConfidence = null;

    private static final int RESULTS_TO_SHOW = 5;
    private static final int BATCH_SIZE = 1;
    private static final int NUM_OF_FRAMES = 32;
    private static final int NUM_OF_FILTERS = 3;
    private static final int WIDTH = 224;
    private static final int HEIGHT = 224;
    private static final int TOP_SIZE = 5;

    // activity elements
    private ImageView selected_image;
    private Button classify_button;
    private Button back_button;
    private TextView label1;
    private TextView label2;
    private TextView label3;
    private TextView label4;
    private TextView label5;

    private TextView Confidence1;
    private TextView Confidence2;
    private TextView Confidence3;
    private TextView Confidence4;
    private TextView Confidence5;


    // priority queue that will hold the top results from the CNN
    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            tflite = new Interpreter(loadModel(), tfliteOptions);
            labelList = loadLabelList();
            System.out.println("after load model");
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoData =
                ByteBuffer.allocateDirect(
                        BATCH_SIZE * NUM_OF_FRAMES * NUM_OF_FILTERS * WIDTH * HEIGHT);

        videoData.order(ByteOrder.nativeOrder());

        setContentView(R.layout.activity_recognize_action);

        // labels that hold top five results of CNN
        label1 = (TextView) findViewById(R.id.label1);
        label2 = (TextView) findViewById(R.id.label2);
        label3 = (TextView) findViewById(R.id.label3);
        label4 = (TextView) findViewById(R.id.label4);
        label5 = (TextView) findViewById(R.id.label5);
        // displays the probabilities of top labels
        Confidence1 = (TextView) findViewById(R.id.Confidence1);
        Confidence2 = (TextView) findViewById(R.id.Confidence2);
        Confidence3 = (TextView) findViewById(R.id.Confidence3);
        Confidence4 = (TextView) findViewById(R.id.Confidence4);
        Confidence5 = (TextView) findViewById(R.id.Confidence5);

        // initialize imageView that displays selected image to the user
        // selected_image = (ImageView) findViewById(R.id.selected_image);

        // initialize array to hold top labels
        topLables = new String[RESULTS_TO_SHOW];
        // initialize array to hold top probabilities
        topConfidence = new String[RESULTS_TO_SHOW];

        classify();
    }

    public void classify(){
//        System.out.println("in classify");
//        float[] inputVal = new float[1];
//        inputVal[0] = 1;
//        float[][] outputval = new float[1][1];
//        tflite.run(inputVal,outputval);
//        System.out.println("end of classify");
        tflite.run(videoData, labelProbArray);
        printTopKLabels();

    }

    private MappedByteBuffer loadModel() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("converted_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    // loads the labels from the label txt file in assets into a string array
    private List<String> loadLabelList() throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    // print the top labels and respective confidences
    private void printTopKLabels() {
        // add all results to priority queue
        for (int i = 0; i < labelList.size(); ++i) {
            sortedLabels.add(
                    new AbstractMap.SimpleEntry<>(labelList.get(i), labelProbArray[0][i]));
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }

        // get top results from priority queue
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            topLables[i] = label.getKey();
            topConfidence[i] = String.format("%.0f%%",label.getValue()*100);
        }

        // set the corresponding textviews with the results
        label1.setText("1. "+topLables[4]);
        label2.setText("2. "+topLables[3]);
        label3.setText("3. "+topLables[2]);
        label4.setText("4. "+topLables[1]);
        label5.setText("5. "+topLables[0]);
        Confidence1.setText(topConfidence[4]);
        Confidence2.setText(topConfidence[3]);
        Confidence3.setText(topConfidence[2]);
        Confidence4.setText(topConfidence[1]);
        Confidence5.setText(topConfidence[0]);
    }

    public void testModel(){
        FlexDelegate delegate = new FlexDelegate();
        Interpreter.Options options = new Interpreter.Options().addDelegate(delegate);

    }


}
