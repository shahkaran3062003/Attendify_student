package com.example.attendify_student;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class CustomDialogPopUp {

    private final View view;
    Activity activity;
    private AlertDialog otpPopUp;
    static OtpEditText otpText;
    private MaterialButton btnCancel,btnSubmit;
    public static InetAddress serverAdd;
    public static int PORT;

    boolean isWorkDone;

    public static String studentId;


    CustomDialogPopUp(Activity activity){
        this.activity = activity;
        view = LayoutInflater.from(activity).inflate(R.layout.activity_student_otppop_up,null);
        otpText = view.findViewById(R.id.edtOTP);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        studentId  = StudentInstance.getId();
        PORT = StudentInstance.getPort();
        isWorkDone =false;


        otpPopUp = new MaterialAlertDialogBuilder(activity)
                .setTitle("Attendance")
                .setView(view)
                .create();


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpPopUp.dismiss();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpText.getText().toString();
                CustomDialogPopUp.sendResponse(otp);
            }
        });
    }

    public void show(){
        if(otpPopUp != null && !otpPopUp.isShowing()){
            otpPopUp.show();
//            startListen();
        }
    }

    public void dismiss(){
        if(otpPopUp!=null && otpPopUp.isShowing()){
            otpPopUp.dismiss();
        }
    }



    public void startListen(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    PORT = StudentInstance.getPort();
                    DatagramSocket socket = new DatagramSocket(PORT);
                    Log.d("API", "ATTENDACNCE PORT: "+PORT);
                    isWorkDone = false;
                    while(!isWorkDone) {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                        socket.receive(receivePacket);

                        String receiveMessage = new String(receivePacket.getData(),0,receivePacket.getLength());

                        Log.d("API", "Receive Message = : "+receiveMessage);
                        serverAdd= receivePacket.getAddress();


                        if(receiveMessage.equals("attendance")){
                            Log.d("API", "run: in attendance if condition");
//                            otpPopUp.show();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    otpPopUp.show();
                                }
                            });
//                            otpPopUp.show();

                        }else if(receiveMessage.equals("invalid")){

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                    otpText.setText("");
                                }});
                        }else if(receiveMessage.equals("done")){
                            Log.d("API", "ATTENDANCE DONE: ");
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Attendance taken successfully", Toast.LENGTH_SHORT).show();
                                    otpPopUp.dismiss();
                                }
                            });
                            // Only stop work after handling "done"
                            stopWork();
                        }
                        else{
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    otpPopUp.dismiss();
                                }
                            });

                        }

                        Log.d("API", "receive Message: "+receiveMessage);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static void sendResponse(String otp){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    byte[] responseData = (otp+","+studentId).getBytes();
                    DatagramPacket sendResponsePacket = new DatagramPacket(responseData,responseData.length,serverAdd,PORT);
                    socket.send(sendResponsePacket);
                    socket.close();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private void stopWork(){
        isWorkDone = true;
    }


}
