package com.example.mrozansk.chat;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SimpleChatActivity extends AppCompatActivity {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    //DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
    ArrayAdapter<String> adapter;

    String nick,ip;
    TextView nickTextView;
    ListView chatListView;
    EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_chat);

        nickTextView = (TextView) findViewById(R.id.nickTextView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);

        nick = getIntent().getStringExtra(SimpleChatMainActivity.NICK);
        ip = getIntent().getStringExtra(SimpleChatMainActivity.IP);

        nickTextView.setText(nick);

       //setContentView(nickTextView);

        Toast.makeText(SimpleChatActivity.this,nick,
                Toast.LENGTH_LONG).show();

        //obslugujemy dodawanie wiadomosci do listy
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listItems);
        chatListView.setAdapter(adapter);


        //uruchamiamy MQTT w tle
      

    }

    private static class MyHandler extends Handler{
        private final WeakReference<SimpleChatActivity> sActivity;
        MyHandler(SimpleChatActivity activity){
            sActivity = new WeakReference<SimpleChatActivity>(activity);
        }
        public void handleMessage(Message msg) {
            SimpleChatActivity activity = sActivity.get();
            activity.listItems.add("["+msg.getData().getString("NICK") + "]" +
                    msg.getData().getString("MSG"));
            activity.adapter.notifyDataSetChanged();
            activity.chatListView.setSelection(activity.listItems.size()-1);
        }
    }
    Handler myHandler = new MyHandler(this);

    public void postOnClick(View view){
        Message msg = myHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("NICK", nick);
        b.putString("MSG", messageEditText.getText().toString());
        msg.setData(b);
        myHandler.sendMessage(msg);
        messageEditText.setText("");
    }


    MqttClient sampleClient=null;
    private void startMQTT(){
        String clientId;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            String broker = "tcp://"+ip+":1883";
            clientId = nick;
            sampleClient = new MqttClient(broker, clientId, persistence);
            sampleClient.setCallback(new MqttCallback() {
                                         @Override
                                         public void connectionLost(Throwable throwable) {
                                             throwable.printStackTrace();
                                         }

                                         @Override
                                         public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                                             Message msg = myHandler.obtainMessage();
                                             Bundle b = new Bundle();
                                             b.putString("NICK", nick);
                                             b.putString("MSG", mqttMessage.toString());
                                             msg.setData(b);
                                             myHandler.sendMessage(msg);
                                             messageEditText.setText("");
                                         }

                                         @Override
                                         public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                                         }
                                     });

                    MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            sampleClient.subscribe("#");
        } catch (MqttException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }





/*
    MqttClient sampleClient=null;

    private void startMQTT(){
        String clientId;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            String broker = "tcp://"+ip+":1883";
            clientId = nick;
            sampleClient = new MqttClient(broker, clientId, persistence);
            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Message msg = myHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("NICK", nick);
                    b.putString("MSG", mqttMessage.toString());
                    msg.setData(b);
                    myHandler.sendMessage(msg);
                    messageEditText.setText("");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            }

        });
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            sampleClient.subscribe("#");
        } catch (MqttException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
*/

    protected void onDestroy() {
        super.onDestroy();
        if (sampleClient != null) {
            try {
                sampleClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


}
