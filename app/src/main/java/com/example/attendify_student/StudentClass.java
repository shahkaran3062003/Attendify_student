package com.example.attendify_student;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class StudentClass extends Fragment {



    // TODO: Rename and change types of parameters
    private String studentId;
    String baseApi;
    CustomProgressBar pBar;
    String classTitle;

    TextView txtClassTitle;
    RecyclerView rcvStudent;
    RecyclerStudentAdapter adapter;
    CustomDialogPopUp popUp;

    private ArrayList<StudentModel> studentList = new ArrayList<StudentModel>();

    public StudentClass() {
        // Required empty public constructor




    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_class, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        baseApi = getContext().getString(R.string.baseApi);
        pBar = new CustomProgressBar(this.getActivity());
        studentId = StudentInstance.getId();


        if(studentId.equals("-1")){
            getActivity().finish();
        }



        txtClassTitle = view.findViewById(R.id.txtClassTitle);

        getClassDetails();

        rcvStudent = view.findViewById(R.id.rcvStudent);
        rcvStudent.setLayoutManager(new LinearLayoutManager(getContext()));


        popUp = new CustomDialogPopUp(getActivity());



    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//

    }

    void getClassDetails(){

        pBar.show();
        Log.d("API", "getClassDetails: Start");
        JSONObject params = new JSONObject();
        try {
            params.put("id", studentId);
            Log.d("API", "getClassDetails: SET");
        } catch (JSONException e) {
            Log.d("API", "getClassDetails: "+e);
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                baseApi + "/student/read_class.php",
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pBar.hide();
                        Log.d("API", "getClassDetails: in Response");
                        try {
                                if(response.has("message")){
                                        Toast.makeText(getContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                    }
                                else{
                                    JSONArray jsonArray = response.getJSONArray("data");
                                    classTitle = jsonArray.getJSONObject(0).get("class").toString();
                                    int PORT = (int) jsonArray.getJSONObject(0).get("port");
                                    StudentInstance.setPORT(PORT);
                                    Log.d("API", "onResponse: PORT NUMBER" + PORT);
                                    txtClassTitle.setText(classTitle);
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        studentList.add(new StudentModel(obj.get("id").toString(),obj.get("name").toString(),obj.get("rollNo").toString(),obj.get("profilePic").toString()));
                                    }

                                    adapter = new RecyclerStudentAdapter(getContext(),studentList);

                                    rcvStudent.setAdapter(adapter);
                                    popUp.startListen();
                                }

                            } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pBar.hide();
                        Log.d("API", "getClassDetails: In Error");
                        Toast.makeText(getContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }

        );

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);

    }


}