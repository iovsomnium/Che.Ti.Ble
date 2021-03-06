package com.example.che_ti_bleEX;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity implements UserAdapter.OnUserListerner {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Teacher> arrayList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    LinearLayout usertimetable, userchat, userset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        recyclerView = findViewById(R.id.user_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();  //User 객체를 담을 어레이 리스트(어댑터쪽으로)
        usertimetable =(LinearLayout)findViewById(R.id.usertimetable);
        userchat =(LinearLayout)findViewById(R.id.userchat);
        userset =(LinearLayout) findViewById(R.id.userset);

        usertimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent usertimetable = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(usertimetable);

                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_in_right);
            }
        });
        userset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userset = new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(userset);

                overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_out_left);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("Teacher");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); //기존 배열리스트가 존재하지 않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    arrayList.add(teacher); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserActivity", String.valueOf(databaseError.toException()));
            }
        });

        adapter = new UserAdapter(arrayList,this,this);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onUserClick(int position) {
        Log.d("click","click" + position);

        String stUserId = arrayList.get(position).getKey();
        Intent intent = new Intent(this,ChatActivity.class);
        intent.putExtra("UserUid",stUserId);
        startActivity(intent);
    }

    private boolean backKeyPressedTwice = false;

    @Override
    public void onBackPressed(){
        if(backKeyPressedTwice) {
            super.onBackPressed();
            finishAffinity();
            System.runFinalization();
            System.exit(0);
            return;
        }

        backKeyPressedTwice = true;
        Toast.makeText(this, "한번 더 누를시 종료됩니다.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backKeyPressedTwice=false;
            }
        }, 2000);
    }
}
