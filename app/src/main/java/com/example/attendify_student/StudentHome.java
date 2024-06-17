package com.example.attendify_student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class StudentHome extends AppCompatActivity {


    BottomNavigationView bnStudent;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        bnStudent = findViewById(R.id.bnStudent);

        loadFrag(new StudentClass(),true);




        bnStudent.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if(id == R.id.bnClass){
                    loadFrag(new StudentClass(),false);
                }else if(id == R.id.bnAttendance){
                    loadFrag(new StudentAttendance(),false);
                }else if(id == R.id.bnProfile){
                    loadFrag(new StudentProfile(bnStudent),false);
                }

                return true;
            }
        });


    }

    public void loadFrag(Fragment fragment,boolean flag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

    if(flag){
        ft.add(R.id.flHome, fragment);
    }
    else{
        ft.replace(R.id.flHome,fragment);
    }
        ft.commit();
    }
}