package com.example.attendify_student;

public class ClassModel {
    public String id;
    public String classTitle;

    ClassModel(String id,String classTitle){
        this.id = id;
        this.classTitle = classTitle;
    }

    public String getId(){
        return  this.id;
    }

    public  String getClassTitle(){
        return  this.classTitle;
    }

    @Override
    public String toString(){
        return  this.classTitle;
    }
}
