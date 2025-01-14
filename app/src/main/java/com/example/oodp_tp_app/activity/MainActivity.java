package com.example.oodp_tp_app.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.oodp_tp_app.R;
import com.example.oodp_tp_app.adapter.ProjectAdapter;
import com.example.oodp_tp_app.classes.Leader;
import com.example.oodp_tp_app.classes.Member;
import com.example.oodp_tp_app.classes.Project;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TextView tv_name;
    private ImageView iv_profile;
    private Toolbar toolbar;
    private Button btn_project_add;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Project> projects;
    private FirebaseFirestore db;
    private CollectionReference projectCollection;
    private CollectionReference userCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String nickName = intent.getStringExtra("nickName");
        String photoUrl = intent.getStringExtra("photoUrl");


        tv_name = findViewById(R.id.user_name);
        tv_name.setText(nickName);
        iv_profile = findViewById(R.id.user_photo);
        btn_project_add = findViewById(R.id.btn_project_create);
        btn_project_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TaskCreateActivity.class);
                intent.putExtra("mode", "project");
                intent.setFlags(intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
        Glide.with(this).load(photoUrl).into(iv_profile);

        recyclerView = findViewById(R.id.project_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        projects = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        projectCollection = db.collection("Projects");
        userCollection = db.collection("Users");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        projectCollection.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(QueryDocumentSnapshot projectSnapshot : task.getResult()) {

                    Project project = projectSnapshot.toObject(Project.class);
                    projects.add(project);

                    Log.d("Projects Read::", projectSnapshot.getId() + " => " + projectSnapshot.getData());
                    System.out.println("Projects Read:: projectName => " + project.getProjectName());
                    System.out.println("Projects Read:: leader => " + project.getLeader().getDisplayName());
                    System.out.println("Projects Read:: members() => " + project.getMembers());

                }
                adapter.notifyDataSetChanged();
            } else {
                Log.d("Projects Read", "Error getting documents: ", task.getException());
            }
        });

        adapter = new ProjectAdapter(projects, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}