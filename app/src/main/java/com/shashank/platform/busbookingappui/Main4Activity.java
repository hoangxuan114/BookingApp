package com.shashank.platform.busbookingappui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Main4Activity extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button logout;
    TextView profileEmail;
    TextView profileName;
    TextView profileNumber;

    Button editp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user);

        profileEmail = findViewById(R.id.profile_email);
        profileName = findViewById(R.id.profile_name);
        profileNumber = findViewById(R.id.profile_number);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        String userId = currentUser.getUid();
        DatabaseReference myRef = mDatabase.getReference().child("users").child(userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy dữ liệu của node
                if (dataSnapshot.exists()) {
                    // Lấy dữ liệu của node
                    String email = dataSnapshot.child("Email").getValue(String.class);
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String phone = dataSnapshot.child("Phone").getValue(String.class);

                    profileEmail.setText(email);
                    profileName.setText(name != null ? name : "");
                    profileNumber.setText(phone != null ? phone : "");
                } else {
                    // Nếu không có dữ liệu, đặt giá trị mặc định là trống
                    profileName.setText("Chưa đặt tên");
                    profileNumber.setText("Chưa có số điện thoại");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Main4Activity.this, "Sai thông tin",
                        Toast.LENGTH_SHORT).show();
            }
        });


        editp = findViewById(R.id.edit_profile_button);
        editp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main4Activity.this, Main5Activity.class);
                startActivity(intent);
                finish();
            }
        });

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Main4Activity.this)
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.signOut();
                                Intent intent = new Intent(Main4Activity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(Main4Activity.this, "đăng xuất thành công",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
    }
}
