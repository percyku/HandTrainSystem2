package com.trainsystem.upperlimb.senior.handtrainsystem2.tools;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by percyku on 2017/3/14.
 */

public class BlueToothConnectFinal {


    Context context;

    public static BluetoothAdapter bluetoothAdapter;
    public static BluetoothSocket bluetoothSocket;
    public static BluetoothSocket bluetoothSocket2;
    public ConnectedThread connectedThread;
    public static Handler bluetoothIn;

    public static boolean bState = false;

    public final static int handlerState = 1;
    public final static int handlerState2 = 2;
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
    private final static int CONNECTING_STATUS2 = 4; // used in bluetooth handler to identify message status

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    int number = 0;

    public BlueToothConnectFinal(Context context, int number) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.number = number;
    }


    public void discover(BroadcastReceiver broadcastReceiver) {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Toast.makeText(context, "Cancel Search!", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.startDiscovery();
                Toast.makeText(context, "Discovery started", Toast.LENGTH_SHORT).show();
                context.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            } else {
                Toast.makeText(context, "Start Discovering", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void connectBt(String str) {

        final String address = str.substring(str.length() - 17);
        final String name = str.substring(0, str.length() - 17);

        if (number == 1) {

            new Thread() {
                public void run() {
                    Log.d("connectBt", "1");

                    boolean fail = false;
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    try {
                        Log.d("connectBt", "2");

                        bluetoothSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(context, "Socket creation failed", Toast.LENGTH_SHORT).show();
                        Log.d("connectBt", "2:Socket creation failed");

                    }

                    try {
                        bluetoothSocket.connect();
                        Log.d("connectBt", "3");
                        bState = true;
                    } catch (IOException e) {
                        try {
                            Log.d("connectBt", "4");
                            fail = true;
                            bluetoothSocket.close();
                            bluetoothIn.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();


                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(context, "Socket creation failed", Toast.LENGTH_SHORT).show();
                            Log.d("connectBt", "4:");
                        }

                    } finally {


                    }

                    if (fail == false) {
                        connectedThread = new ConnectedThread(bluetoothSocket);
                        connectedThread.start();
                        Log.d("connectBt", "5");
                        bluetoothIn.obtainMessage(CONNECTING_STATUS, -1, -1)
                                .sendToTarget();

                        Log.d("connectBt", "6");

                    }

                }


            }.start();

        }
        if (number == 2) {

            new Thread() {
                public void run() {
                    Log.d("connectBt", "1");

                    boolean fail = false;
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    try {
                        Log.d("connectBt", "2");

                        bluetoothSocket2 = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(context, "Socket creation failed", Toast.LENGTH_SHORT).show();
                        Log.d("connectBt", "2:Socket creation failed");

                    }

                    try {
                        bluetoothSocket2.connect();
                        Log.d("connectBt", "3");
                        bState = true;
                    } catch (IOException e) {
                        try {
                            Log.d("connectBt", "4");
                            fail = true;
                            bluetoothSocket2.close();

                            bluetoothIn.obtainMessage(CONNECTING_STATUS2, -1, -1)
                                    .sendToTarget();

                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(context, "Socket creation failed", Toast.LENGTH_SHORT).show();
                            Log.d("connectBt", "4:");
                        }

                    } finally {


                    }

                    if (fail == false) {
                        connectedThread = new ConnectedThread(bluetoothSocket2);
                        connectedThread.start();
                        Log.d("connectBt", "5");


                        bluetoothIn.obtainMessage(CONNECTING_STATUS2, -1, -1)
                                .sendToTarget();

                        Log.d("connectBt", "6");

                    }

                }


            }.start();

        }

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    public void mInput(String str) {
        connectedThread.write(str);
    }

    public void closeConnectBt(BroadcastReceiver blReceiver) {
        if(number==1){
            if (bluetoothSocket != null) {
                try {

                    bluetoothSocket.close();
                    bluetoothSocket = null;
                    context.unregisterReceiver(blReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
       if(number==2){
           if (bluetoothSocket2 != null) {
               try {

                   bluetoothSocket2.close();
                   bluetoothSocket2 = null;
                   context.unregisterReceiver(blReceiver);
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       }
    }

    public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        // creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                // Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            // The arduino is sending a small amount of data so a large buffer
            // is not needed
            byte[] buffer = new byte[1000];
            int bytes;

            // Keep looping to listen for received messages
            int a = 0;

            while (true) {

                try {
                    //Log.e("run", "number:"+a);
                    bytes = mmInStream.read(buffer); // read bytes from input
                    // buffer
                    if (number == 1)
                        bluetoothIn.obtainMessage(handlerState, bytes, -1, buffer).sendToTarget(); // Send message to handler
                    if (number == 2)
                        bluetoothIn.obtainMessage(handlerState2, bytes, -1, buffer).sendToTarget(); // Send message to handler
                } catch (IOException e) {
                    Log.e("run", "catch" + e.toString());
                    break;
                }
                a++;
            }
            buffer = null;
        }

        // write method

        public void write(String input) {
            byte[] msgBuffer = input.getBytes(); // converts entered String into
            // bytes
            try {
                mmOutStream.write(msgBuffer); // write bytes over BT connection


            } catch (IOException e) {
                // if you cannot write close the application
                //Toast.makeText(, "裝置有異狀",Toast.LENGTH_LONG).show();
                //finish();

            }
        }

    }

    public void checkBlueToothState() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(context,
                    "Device doesn't support bluetooth", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
//            Intent mIntentOpenBT = new Intent(
//                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            ((Activity)cont).startActivityForResult(mIntentOpenBT, REQUEST_ENABLE_BT);
            bluetoothAdapter.enable();
        }
    }


    public boolean checkArduinoConnectState() {

        if (number == 1) {
            if (bluetoothSocket == null) {
                return false;
            } else {
                if (bluetoothSocket.isConnected())
                    return true;
                else
                    return false;

            }

        } else if (number == 2) {
            if (bluetoothSocket2 == null) {
                return false;
            } else {
                if (bluetoothSocket2.isConnected())
                    return true;
                else
                    return false;

            }

        } else {
            return false;
        }

    }
}
