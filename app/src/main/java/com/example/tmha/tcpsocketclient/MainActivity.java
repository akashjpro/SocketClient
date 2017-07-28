package com.example.tmha.tcpsocketclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private TextView mTxtMessage;
    private String mStr, mMessage = "";
    private EditText mEdtMessage, mEdtIpAddress;
    private Button mBtnSend;
    private Handler mHandler = new Handler();
    private String SERVER_IP = "192.168.2.83";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTxtMessage = (TextView) findViewById(R.id.txt_message);
        mEdtMessage = (EditText) findViewById(R.id.edt_messages);
        mBtnSend    = (Button) findViewById(R.id.btn_send);
        mEdtIpAddress = (EditText) findViewById(R.id.edt_ip_address);
        mEdtIpAddress.setText(SERVER_IP);


//        //Create an instance of AsyncTask
//        ClientAsyncTask clientAST = new ClientAsyncTask();
//        //Pass the server ip, port and client message to the AsyncTask
//        clientAST.execute(new String[] { SERVER_IP, "8080","Hello from client" });

    }

    public void send(View view) {
        Thread thread = new Thread(new SentMessage());
        thread.start();
    }

    public void connect(View view) {
        Thread thread = new Thread(new ClientThread());
        thread.start();
    }

    class ClientAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                //Create a client socket and define internet address and the port of the server
                Socket socket = new Socket(params[0],
                        Integer.parseInt(params[1]));
                //Get the input stream of the client socket
                InputStream is = socket.getInputStream();
                //Get the output stream of the client socket
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                //Write data to the output stream of the client socket
                out.println(params[2]);
                //Buffer the data coming from the input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                //Read data in the input buffer
                result = br.readLine();
                //Close the client socket
                socket.close();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String s) {
            //Write server message to the text view
            mTxtMessage.setText(s);
        }
    }

    class SentMessage implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                InetAddress serverAddr =
                        InetAddress.getByName("192.168.2.83");
                Socket socket = new Socket(serverAddr, 8080);
                //create client socket
                DataOutputStream os = new
                        DataOutputStream(socket.getOutputStream());
                mStr = mEdtMessage.getText().toString();
                mStr = mStr + "\n";
                mMessage = mMessage + "Client : " + mStr;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTxtMessage.setText(mMessage);
                    }
                });
                os.writeBytes(mStr);
                os.flush();
                os.close();
                socket.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public class ClientThread implements Runnable {
        public void run() {
            try {
                while(true) {
                    InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                    Socket socket = new Socket(serverAddr, 8080);
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        mMessage = mMessage + "Server : " + line + "\n";
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mTxtMessage.setText(mMessage);
                        } }); }

                    in.close();
                    socket.close();
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

