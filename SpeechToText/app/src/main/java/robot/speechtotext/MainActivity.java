package robot.speechtotext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button Start;
    Button Forward;
    Button Left;
    Button Right;
    Button Back;
    Button Stop;
    ActionBar actionBar;
    ImageButton speechButton;
    EditText speechText;
    MqttAndroidClient client;
    private  static final int RECOGNIZER_RESULT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        actionBar.hide();
        speechButton = findViewById(R.id.speechbutton);
        speechText = findViewById(R.id.speechtext);

        Start = findViewById(R.id.start);
        Forward = findViewById(R.id.forward);
        Left = findViewById(R.id.left);
        Right = findViewById(R.id.right);
        Back = findViewById(R.id.back);
        Stop = findViewById(R.id.stop);

        Start.setOnClickListener(mLisstener);
        Forward.setOnClickListener(mLisstener);
        Left.setOnClickListener(mLisstener);
        Right.setOnClickListener(mLisstener);
        Back.setOnClickListener(mLisstener);
        Stop.setOnClickListener(mLisstener);
        speechButton.setOnClickListener(mLisstener);
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://citlab.myftp.org:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("mqtt", "onSuccess");
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("mqtt", "onFailure");
                    Toast.makeText(MainActivity.this, "Connected Fail", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }
    String payload;
    View.OnClickListener mLisstener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            Start.setTextColor(Color.BLACK);    Start.setSelected(false);
            Forward.setTextColor(Color.BLACK);  Forward.setSelected(false);
            Left.setTextColor(Color.BLACK);     Left.setSelected(false);
            Right.setTextColor(Color.BLACK);    Right.setSelected(false);
            Back.setTextColor(Color.BLACK);     Back.setSelected(false);
            Stop.setTextColor(Color.BLACK);     Stop.setSelected(false);
            speechButton.setSelected(false);

            switch (id){
                case R.id.start:
                    speechText.setText("Start");
                    pub("Chao");
                    Start.setTextColor(Color.RED);
                    Start.setSelected(true);
                    break;
                case R.id.forward:
                    speechText.setText("Forward");
                    pub("Chao");
                    Forward.setTextColor(Color.RED);
                    Forward.setSelected(true);
                    break;
                case R.id.left:
                    speechText.setText("Left");
                    pub("Chao");
                    Left.setTextColor(Color.RED);
                    Left.setSelected(true);
                    break;
                case R.id.right:
                    speechText.setText("Right");
                    pub("Chao");
                    Right.setTextColor(Color.RED);
                    Right.setSelected(true);
                    break;
                case R.id.back:
                    speechText.setText("Back");
                    pub("Chao");
                    Back.setTextColor(Color.RED);
                    Back.setSelected(true);
                    break;
                case R.id.stop:
                    speechText.setText("Stop");
                    pub("Chao");
                    Stop.setTextColor(Color.RED);
                    Stop.setSelected(true);
                    break;
                case R.id.speechbutton:
                    Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hi speak something");
                    startActivityForResult(speechIntent,RECOGNIZER_RESULT);
                    speechText.setText("");
                    speechButton.setSelected(true);
                default:
                    break;
            }
        }
    };

    void pub(String content){
        String topic = "garden1/sensor1";
        payload = speechText.getText().toString();
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            speechText.setText(matches.get(0).toString());
            pub("chào cu");
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

}