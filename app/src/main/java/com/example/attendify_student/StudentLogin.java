package com.example.attendify_student;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentLogin extends AppCompatActivity {

//    AutoCompleteTextView ddClass;
    TextView txtSignUp;

    MaterialButton btnLogin;
//    ArrayList<ClassModel> classList = new ArrayList<ClassModel>();
    CustomProgressBar pBar;
    String baseAPi;

//    String selectedClassId;
    String imei;
    TextInputEditText edtEmail,edtPassword;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        pBar = new CustomProgressBar(this);
        baseAPi = getString(R.string.baseApi).toString();

//        ddClass = findViewById(R.id.ddClass);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        txtSignUp = findViewById(R.id.txtSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        imei = Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


//        getAllClass();
//        ArrayDropDownAdapter adapter = new ArrayDropDownAdapter(this,classList);
//        ddClass.setAdapter(adapter);


//        ddClass.setOnItemClickListener((parent,view,position,id)->{
//            ClassModel classItem = classList.get(position);
//            selectedClassId = classItem.id;
//        });


        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(getApplicationContext(), StudentRegister.class);
                startActivity(signUp);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isEmptyField() && isEmailCorrect()){

                    pBar.show();

                    JSONObject params = new JSONObject();
                    try {
                        params.put("email",edtEmail.getText().toString());
                        params.put("password",edtPassword.getText().toString());
                        params.put("imei",imei);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,
                            baseAPi + "/student/login.php",
                            params,

                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    pBar.hide();
                                    if (response.has("message")) {
                                        try {
                                            Toast.makeText(StudentLogin.this, "" + response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                            if (response.get("message").toString().equals("success")) {
                                                String id;

                                                id = response.get("id").toString();


                                                SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = pref.edit();

                                                editor.putBoolean("flag", true);
                                                editor.putString("id", id);

                                                editor.apply();
                                                StudentInstance.setId(id);

                                                Toast.makeText(StudentLogin.this, "" + response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                                StudentLogin.this.startActivity(new Intent(StudentLogin.this, StudentHome.class));

                                                finish();
                                            }
                                        }catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }

                                        Log.d("API", "onResponse: " + response);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    pBar.hide();
                                    Toast.makeText(StudentLogin.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                                    Log.d("API", "onErrorResponse: "+error);
                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json");
                            return headers;
                        }
                    };

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(request);

                }

//
//                Intent home = new Intent(getApplicationContext(), StudentHome.class);
//                startActivity(home);
//                finish();
            }
        });


    }

    boolean isEmptyField() {
//        if (selectedClassId == null) {
//            Toast.makeText(this, "Select Profile Picture.", Toast.LENGTH_SHORT).show();
//            return true;
//        }
        if(TextUtils.isEmpty(edtEmail.getText())){
            Toast.makeText(this, "Enter Email.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(edtPassword.getText())){
            Toast.makeText(this, "Enter Password.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;

    }

    boolean isEmailCorrect() {
        if(Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()){
            return true;
        }
        Toast.makeText(this, "Invalid Email.", Toast.LENGTH_SHORT).show();
        return  false;

    }


//    void getAllClass(){
//        pBar.show();
//        StringRequest stringRequest = new StringRequest(
//                Request.Method.GET,
//                baseAPi + "/class/read_all.php",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        pBar.hide();
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//
//                            if(jsonObject.has("message")){
//                                Toast.makeText(StudentLogin.this, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//                                JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
//                                for(int i=0;i<jsonArray.length();i++){
//                                    JSONObject obj = jsonArray.getJSONObject(i);
//                                    classList.add(new ClassModel(obj.get("id").toString(),obj.get("name").toString()));
//                                }
//                            }
//                        } catch (JSONException e) {
//                            Toast.makeText(StudentLogin.this, "Catch Error", Toast.LENGTH_SHORT).show();
//                            Log.d("API", "onResponse: "+e.toString());
//                            throw new RuntimeException(e);
//                        }
//
//                    }
//
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        pBar.hide();
//                        Toast.makeText(StudentLogin.this, "No internet connection.", Toast.LENGTH_SHORT).show();
//                        Log.d("API", "onErrorResponse: "+error);
//                    }
//                }
//        );
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(stringRequest);
//    }
}