package com.shashank.platform.busbookingappui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class Main1Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button search_buses;
    Spinner spinner1;
    Spinner spinner2;
    ImageView profile;
    TextView textview1;
    private Calendar selectedDate = Calendar.getInstance();
    private boolean doubleBackToExitPressedOnce = false;
    private DatabaseReference mDatabase;


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn BACK lần nữa để thoát ứng dụng", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        DatabaseReference currentUserRef = mDatabase.child(userId);
        /*currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // node người dùng tồn tại trong database
                    String phone = dataSnapshot.child("Phone").getValue(String.class);
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String email = dataSnapshot.child("Email").getValue(String.class);

                    if (phone != null && !phone.isEmpty() && name != null && !name.isEmpty() && email != null && !email.isEmpty()) {
                        // các node Phone, Name, Email đều có giá trị
                        // TODO: xử lý logic khi người dùng đã có thông tin
                    } else {
                        Intent intent = new Intent(Main1Activity.this, Main5Activity.class);
                        startActivity(intent);
                    }
                } else {
                    // node người dùng không tồn tại trong database
                    // TODO: xử lý logic khi người dùng chưa có đủ thông tin
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // xử lý lỗi nếu có
            }
        });*/
        /*if (currentUser != null) {
            String userId = currentUser.getUid();

            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String email = dataSnapshot.child("Email").getValue(String.class);
                    String number = dataSnapshot.child("Number").getValue(String.class);

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(number)) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = user.getUid();

                        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("users");
                        DatabaseReference newRef = usersRef.child(userId);

                        Map<String, Object> newUserData = new HashMap<>();
                        newUserData.put("Phone", "");
                        newUserData.put("Name", "");
                        newUserData.put("Email", "");

                        newRef.setValue(newUserData);
                        Intent intent = new Intent(Main1Activity.this, Main5Activity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Main1Activity.this, "lỗi",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }*/

        profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main1Activity.this, Main4Activity.class);
                startActivity(intent);
            }
        });


        // Định nghĩa Spinner1 và Adapter
        spinner1 = findViewById(R.id.choice1);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        mAdapter.add(""); // Thêm phần tử trống vào đầu danh sách
        spinner1.setAdapter(mAdapter);
        // Tham chiếu đến nút "trips" trong Firebase
        DatabaseReference tripsRef = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("trips");
        // Sử dụng Set để lưu trữ các kết quả duy nhất
        Set<String> uniqueResults = new HashSet<>();
        // Lắng nghe sự thay đổi dữ liệu trong nút "trips"
        tripsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uniqueResults.clear();

                // Lặp qua các nút con trong nút "trips"
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot fromSnapshot = tripSnapshot.child("from");
                    if (fromSnapshot.exists()) {
                        String fromValue = fromSnapshot.getValue(String.class);
                        if (fromValue != null) {
                            uniqueResults.add(fromValue);
                        }
                    }
                }

                // Chuyển đổi Set thành danh sách và sắp xếp theo thứ tự ABC
                List<String> sortedResults = new ArrayList<>(uniqueResults);
                Collections.sort(sortedResults);

                // Xóa danh sách tùy chọn hiện tại
                mAdapter.clear();

                // Thêm phần tử trống vào đầu danh sách tùy chọn
                mAdapter.add("");

                // Thêm các tùy chọn đã sắp xếp vào Adapter
                mAdapter.addAll(sortedResults);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần thiết
            }
        });

        spinner2 = findViewById(R.id.choice2);
        ArrayAdapter<String> mAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        mAdapter2.add(""); // Thêm phần tử trống vào đầu danh sách
        spinner2.setAdapter(mAdapter2);
        // Tham chiếu đến nút "trips" trong Firebase
        DatabaseReference tripsRef2 = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("trips");
        // Sử dụng Set để lưu trữ các kết quả duy nhất
        Set<String> uniqueResults2 = new HashSet<>();
        // Lắng nghe sự thay đổi dữ liệu trong nút "trips"
        tripsRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uniqueResults2.clear();

                // Lặp qua các nút con trong nút "trips"
                for (DataSnapshot tripSnapshot2 : dataSnapshot.getChildren()) {
                    DataSnapshot fromSnapshot2 = tripSnapshot2.child("to");
                    if (fromSnapshot2.exists()) {
                        String fromValue = fromSnapshot2.getValue(String.class);
                        if (fromValue != null) {
                            uniqueResults2.add(fromValue);
                        }
                    }
                }
                // Chuyển đổi Set thành danh sách và sắp xếp theo thứ tự ABC
                List<String> sortedResults2 = new ArrayList<>(uniqueResults2);
                Collections.sort(sortedResults2);
                // Xóa danh sách tùy chọn hiện tại
                mAdapter2.clear();
                // Thêm phần tử trống vào đầu danh sách tùy chọn
                mAdapter2.add("");
                // Thêm các tùy chọn đã sắp xếp vào Adapter
                mAdapter2.addAll(sortedResults2);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần thiết
            }
        });

        textview1 = findViewById(R.id.date);
        textview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy ngày hiện tại
                int year = selectedDate.get(Calendar.YEAR);
                int month = selectedDate.get(Calendar.MONTH);
                int day = selectedDate.get(Calendar.DAY_OF_MONTH);
                // Tạo DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(Main1Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Lưu ngày đã chọn
                        selectedDate.set(year, monthOfYear, dayOfMonth);
                        // Hiển thị ngày đã chọn trong TextView
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String selectedDateString = dateFormat.format(selectedDate.getTime());
                        textview1.setText(selectedDateString);
                    }
                }, year, month, day);
                // Giới hạn ngày chọn chỉ cho phép chọn các ngày tiếp theo
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                // Hiển thị DatePickerDialog
                datePickerDialog.show();
            }
        });

        search_buses = findViewById(R.id.search_buses);
        search_buses.setOnClickListener(view -> {
            // Lấy giá trị đã chọn từ Spinner
            String selectedFrom = spinner1.getSelectedItem().toString();
            String selectedTo = spinner2.getSelectedItem().toString();

            // Lấy ngày đã chọn từ TextView
            String selectedDate = textview1.getText().toString();

            // Tách ngày/tháng/năm thành các phần riêng biệt
            String[] dateParts = selectedDate.split("/");
            int selectedDay = Integer.parseInt(dateParts[0]);
            int selectedMonth = Integer.parseInt(dateParts[1]);
            int selectedYear = Integer.parseInt(dateParts[2]);

            // Tham chiếu đến nút "trips" trong Firebase
            DatabaseReference tripsRef3 = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("trips");

            // Tạo một truy vấn (query) để tìm kiếm các trip theo các điều kiện
            Query searchQuery = tripsRef3.orderByChild("from").equalTo(selectedFrom);

            searchQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int foundResult = 0;

                    // Lặp qua các trip tìm thấy
                    for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                        // Lấy thông tin từ trip
                        String to = tripSnapshot.child("to").getValue(String.class);
                        String tripTime = tripSnapshot.child("time").getValue(String.class);
                        Integer availableSeats = tripSnapshot.child("trip").child(String.valueOf(selectedYear))
                                .child(String.format("%02d", selectedMonth)).child(String.format("%02d", selectedDay))
                                .child("Available").getValue(Integer.class);
                        // Kiểm tra các điều kiện tìm kiếm
                        if (to != null && to.equals(selectedTo) && availableSeats != null && availableSeats > 0) {
                            // Đã tìm thấy kết quả phù hợp
                            foundResult++;
                            // Do something with the data here
                            // Ví dụ: hiển thị kết quả tìm kiếm
                        }
                    }

                    if (foundResult==0) {
                        // Hiển thị thông báo không tìm thấy kết quả
                        Toast.makeText(Main1Activity.this, "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
                    } else {
                        // Chuyển hướng sang Main2Activity khi có kết quả tìm kiếm
                        Toast.makeText(Main1Activity.this, "Tìm thấy: " + foundResult+ " kết quả", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Main1Activity.this , Main2Activity.class);
                        intent.putExtra("spinner1", selectedFrom);
                        intent.putExtra("spinner2", selectedTo);
                        intent.putExtra("datevalue", selectedDate);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý lỗi nếu cần thiết
                }
            });
        });
    }
}
