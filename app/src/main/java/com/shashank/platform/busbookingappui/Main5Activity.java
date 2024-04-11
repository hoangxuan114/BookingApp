package com.shashank.platform.busbookingappui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Main5Activity extends AppCompatActivity {
    Button cancel;
    Button change;
    EditText profileEmail;
    EditText profileName;
    EditText profileNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit);

        profileEmail = findViewById(R.id.email);
        profileName = findViewById(R.id.name);
        profileNumber = findViewById(R.id.number);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        String userId = currentUser.getUid();
        DatabaseReference myRef = mDatabase.getReference().child("users").child(userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy dữ liệu của node
                String email = dataSnapshot.child("Email").getValue(String.class);
                String name = dataSnapshot.child("Name").getValue(String.class);
                String phone = dataSnapshot.child("Phone").getValue(String.class);

                profileEmail.setText(email);
                profileName.setText(name != null ? name :"Chưa đặt tên");
                profileNumber.setText(phone != null ? phone :"Chưa có số điện thoại");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Main5Activity.this, "Sai thông tin",
                        Toast.LENGTH_SHORT).show();
            }
        });

        change = findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name1 = profileName.getText().toString().trim();
                String email1 = profileEmail.getText().toString().trim();
                String phone1 = profileNumber.getText().toString().trim();

                if (phone1.length() != 10) {
                    Toast.makeText(getApplicationContext(), "Số điện thoại phải có 10 chữ số", Toast.LENGTH_SHORT).show();
                } else if (name1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()) {
                    Toast.makeText(getApplicationContext(), "Định dạng email không đúng", Toast.LENGTH_SHORT).show();
                } else {
                        myRef.child("Name").setValue(name1);
                        myRef.child("Email").setValue(email1);
                        myRef.child("Phone").setValue(phone1);
                        Intent intent = new Intent(Main5Activity.this, Main4Activity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Lưu thông tin thành công", Toast.LENGTH_SHORT).show();
                        finish();
                }
            }
        });
                cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main5Activity.this, Main4Activity.class);
                startActivity(intent);
                Toast.makeText(Main5Activity.this, "Đã hủy",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
