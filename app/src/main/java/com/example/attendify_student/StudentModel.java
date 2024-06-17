package com.example.attendify_student;

public class StudentModel {
    String id;
    String name;
    String rollNo;
    String profilePicPath;

    public StudentModel(String id,String name,String rollNo,String profilePicPath){
        this.id = id;
        this.name = name;
        this.rollNo = rollNo;
        this.profilePicPath = profilePicPath;
    }
}
