package com.shashank.platform.busbookingappui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {

    //Button search_buses;
    private FirebaseAuth mAuth;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    Button SignIn;
    Button SignUp;
    Button Google_SignIn;
    private static final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main1);
        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // Người dùng đã đăng nhập, chuyển đến trang tiếp theo
            Intent intent = new Intent(MainActivity.this, Main1Activity.class);
            startActivity(intent);
            finish();
        }
        SignIn = findViewById(R.id.buttonSignIn);
        mEmailEditText = findViewById(R.id.editTextUsername);
        mPasswordEditText = findViewById(R.id.editTextPassword);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                String TAG = null;
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String uid = user.getUid();
                                    // Truy cập vào node "users" và kiểm tra uid của người dùng
                                    DatabaseReference usersRef = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
                                    usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            // Kiểm tra xem uid của người dùng có tồn tại trong node "users" hay không
                                            if (dataSnapshot.exists()) {
                                                Intent intent = new Intent(MainActivity.this, Main1Activity.class);
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(MainActivity.this, "Đăng nhập thành công",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                DatabaseReference usersRef = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
                                                DatabaseReference userNodeRef = usersRef.child(uid);
                                                String email = user.getEmail();
                                                userNodeRef.child("Email").setValue(email);
                                                userNodeRef.child("Name").setValue("");
                                                userNodeRef.child("Phone").setValue("");
                                                Intent intent = new Intent(MainActivity.this, Main5Activity.class);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Xử lý lỗi nếu cần thiết
                                        }
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Sai tên đăng nhập hoặc mật khẩu",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        Google_SignIn = findViewById(R.id.buttonGoogleSignIn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Khởi tạo GoogleSignInClient với các tùy chọn đã được chỉ định bởi gso
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        Google_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                if (account == null) {
                    // Start the sign in flow
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    // Sign in with the existing account
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();
                                // Truy cập vào node "users" và kiểm tra uid của người dùng
                                DatabaseReference usersRef = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
                                usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Kiểm tra xem uid của người dùng có tồn tại trong node "users" hay không
                                        if (dataSnapshot.exists()) {
                                            Intent intent = new Intent(MainActivity.this, Main1Activity.class);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(MainActivity.this, "Đăng nhập thành công",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            DatabaseReference usersRef = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
                                            DatabaseReference userNodeRef = usersRef.child(uid);
                                            String email = user.getEmail();
                                            userNodeRef.child("Email").setValue(email);
                                            userNodeRef.child("Name").setValue("");
                                            userNodeRef.child("Phone").setValue("");
                                            Intent intent = new Intent(MainActivity.this, Main5Activity.class);
                                            Toast.makeText(MainActivity.this, "Vui lòng cập nhật thông tin5.3",
                                                     Toast.LENGTH_SHORT).show();
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Xử lý lỗi nếu cần thiết
                                    }
                                });
                            } else {
                                // Sign in failed, display a message to the user
                                Toast.makeText(MainActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
                
        /*search_buses = findViewById(R.id.search_buses);
        search_buses.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
            startActivity(intent);
        });*/
        SignUp = findViewById(R.id.buttonSignUp);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }
}
