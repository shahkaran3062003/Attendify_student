package com.example.attendify_student;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AttendanceRecive extends AppCompatActivity {


    OtpEditText edtText;
    MaterialButton btnConfirm;

    int PORT;

    boolean isWorkDone;

    String studentId;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_recive);



        Bundle bundle = getIntent().getExtras();
        studentId  = StudentInstance.getId();
        PORT = StudentInstance.getPort();

        edtText = findViewById(R.id.edtOTP);
        btnConfirm = findViewById(R.id.btnConfirm);
        isWorkDone =false;



        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

//    private void receiveSignal(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    DatagramSocket socket = new DatagramSocket(PORT);
//                    isWorkDone = false;
//                    while(!isWorkDone) {
//                        byte[] receiveData = new byte[1024];
//                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//
//                        socket.receive(receivePacket);
//
//                        String receiveMessage = new String(receivePacket.getData(),0,receivePacket.getLength());
//
//
//                        processSignal(receiveMessage);
////                        sendResponse(receivePacket.getAddress());
//                    }
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    private void stopWork(){
        isWorkDone = true;
    }

//    private void processSignal(final String receiveSignal) {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
////                txtReceiveData.setText(receiveSignal);
//                System.out.println(receiveSignal);
//            }
//        });
//    }

//    private void sendResponse(InetAddress serverAddr){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    DatagramSocket socket = new DatagramSocket();
//
//                    byte[] responseData = getIP().getBytes();
//
//
//                    DatagramPacket sendResponsePacket = new DatagramPacket(responseData,responseData.length,serverAddr,PORT);
//
//                    socket.send(sendResponsePacket);
//                    socket.close();
//
//                    stopWork();
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
}