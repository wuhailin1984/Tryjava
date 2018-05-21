package com.example.tryjava;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private Context context;
    TextView textViewShowTemp;
    //EditText editTextInputWhichSensor = null;
    Button buttonGetTemp = null;
    CheckBox checkBoxContinuousRefresh = null;
    GetTempThread myGetTempThreadInstance = null;
    Spinner spinnerSensorNames;

    Handler handler = null;
    Socket soc;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    String messageRecv = null;

    //public static String IP_ADDRESS = "10.0.2.2";
    //public static String IP_ADDRESS = "10.46.40.128";
    //public static String IP_ADDRESS = "192.168.10.138";
    public static String IP_ADDRESS = "35.204.196.49";
    public static int PORT = 3000;
    private ArrayAdapter<String> arrayList;
    private String[] sensorNames = {"Office", "Home", "Somewhere"};
    private String[] roomNames= {"Office", "Home", "Somewhere"};
    HashMap<String,String> nameOfRoomAndSenor=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameOfRoomAndSenor=new HashMap<String, String>();
        nameOfRoomAndSenor.put("Office","Sensor1");
        nameOfRoomAndSenor.put("Home","Sensor2");
        nameOfRoomAndSenor.put("Somewhere","Sensor3");


        context = this;
        buttonGetTemp = (Button) findViewById(R.id.buttonGetTemp);
        textViewShowTemp = (TextView) findViewById(R.id.textViewShowTemp);               //to show the temperature
        //editTextInputWhichSensor = (EditText) findViewById(R.id.editTextWhichSensor);  //to choose which rum
        buttonGetTemp.setOnClickListener(new GetTemper());
        checkBoxContinuousRefresh = (CheckBox) findViewById(R.id.checkBoxContinuousRefresh);
        spinnerSensorNames=(Spinner)findViewById(R.id.spinnerSensorNames);
        checkBoxContinuousRefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (null == myGetTempThreadInstance) return;
                Bundle b = new Bundle();
                if (isChecked) {
                    //turn on flag to get temperature thread constantly sending request to the server
                    myGetTempThreadInstance.isContinuousRefreshChecked=true;
                } else {
                    //turn off flag to get temperature thread constantly sending request to the server
                    myGetTempThreadInstance.isContinuousRefreshChecked=false;
                }
            }
        });

        arrayList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sensorNames) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                return setCentered(super.getView(position, convertView, parent));
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                return setCentered(super.getDropDownView(position, convertView, parent));
            }

            private View setCentered(View view)
            {
                TextView textView = (TextView)view.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.CENTER);
                return view;
            }

        };

        spinnerSensorNames.setAdapter(arrayList);

        spinnerSensorNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
                //editTextInputWhichSensor.setText(sensorNames[position]);
                ( (TextView) findViewById(R.id.textViewSensorName) ).setText(roomNames[position]);
                if (null != myGetTempThreadInstance)
                    myGetTempThreadInstance.message=nameOfRoomAndSenor.get(sensorNames[position]);

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                if (null != myGetTempThreadInstance)
                    myGetTempThreadInstance.message="";
            }
        });

        /*
        editTextInputWhichSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextInputWhichSensor.setText("");
            }
        });
        */

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle b = msg.getData();             //to get Bundle from msg
                String str = b.getString("data");  //to get the value of the string whose key is data
                textViewShowTemp.setText(str);
            }
        };
    }

    class GetTemper implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (null == myGetTempThreadInstance) {
                //myGetTempThreadInstance = new GetTempThread(editTextInputWhichSensor.getText().toString());
                String tmpStr=nameOfRoomAndSenor.get(spinnerSensorNames.getSelectedItem().toString());
                myGetTempThreadInstance = new GetTempThread(tmpStr);
                myGetTempThreadInstance.start();
                Toast.makeText(context, "1111", Toast.LENGTH_SHORT).show();
            }
            else{
                myGetTempThreadInstance.commandRefresh=true;
                if(false==myGetTempThreadInstance.isStillAlive) {
                    Toast.makeText(context, "2222", Toast.LENGTH_SHORT).show();
                    myGetTempThreadInstance.run();
                }
            }
        }
    }

    class GetTempThread extends Thread {
        public volatile String message = null;
        //public Handler subTempThreadHandler = null;
        public volatile boolean isContinuousRefreshChecked = false;
        public volatile boolean commandRefresh=false;
        public volatile boolean isStillAlive=true;


        public GetTempThread(String msg) {
            message = msg;
            commandRefresh=true;
        }

        @Override
        public void run() {
            /*
            Looper.prepare();
            subTempThreadHandler= new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case REFRESH_TEMP: commandRefresh=true; break;
                        case IS_CHECKED: isContinuousRefreshChecked=true;break;
                        case IS_UNCHECKED: isContinuousRefreshChecked=false;break;
                        default: break;
                    }
                }
            };
            */
            isStillAlive=true;
            try{

                while (true) {
                    if (isContinuousRefreshChecked || commandRefresh) {
                        if(soc == null) soc = new Socket(IP_ADDRESS, PORT);
                        if(dis==null)  dis = new DataInputStream(soc.getInputStream());
                        if(dos==null)  dos = new DataOutputStream(soc.getOutputStream());

                        dos.writeUTF(message);
                        dos.flush();
                        messageRecv = dis.readUTF();    //if no message, then block
                        Message msg = handler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("data", messageRecv);
                        msg.setData(b);
                        handler.sendMessage(msg);
                        if (commandRefresh) commandRefresh = false;
                        sleep(2000);
                    }
                }

            }catch (Exception e) {
                // TODO Auto-generated catch block
                if(e instanceof InterruptedException)  System.out.println("sleep exception");
                else if(e instanceof IOException) System.out.println("I/O exception");
                e.printStackTrace();
                isStillAlive=false;
            }
        }
    }
}
