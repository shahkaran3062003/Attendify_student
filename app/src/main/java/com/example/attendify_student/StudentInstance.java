package com.example.attendify_student;

public class StudentInstance {
    public static String id;

    public static int PORT;


    public static String getId(){

        if(id==null){
            return  "-1";
        }
        return  id;
    }

    public  static void setId(String id_){
        id = id_;
    }


    public  static int getPort(){

        return PORT;
    }

    public  static void setPORT(int PORT_){
        PORT = PORT_;
    }
}
