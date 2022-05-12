package com.automatizacion.alcohomidete.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread{
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    Handler bluetoothIn=null;
    int handlerState = 0;

    //creation of the connect thread
    public ConnectedThread(BluetoothSocket socket,Handler bluetoothIn,int handlerState) {
       this.bluetoothIn=bluetoothIn;
       this.handlerState=handlerState;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            //Create I/O streams for connection
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }


    public void run() {
        byte[] buffer = new byte[256];
        int bytes;
        // Keep looping to listen for received messages
        while (true) {
            try {
                bytes = mmInStream.read(buffer);         //read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);
                // Send the obtained bytes to the UI Activity via handler
                bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

}
