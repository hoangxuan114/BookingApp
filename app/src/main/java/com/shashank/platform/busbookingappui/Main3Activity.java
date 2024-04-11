package com.shashank.platform.busbookingappui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class Main3Activity extends AppCompatActivity {
    ImageView reload1;
    Button book;
    Button cancel;
    TextView date1;
    TextView from;
    TextView to;
    TextView name1;
    ImageView[] p;
    //ImageView p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14,p15,p16,p17,p18,p19,p20,p21,p22,p23,p24,p25,p26,p27,p28,p29,p30,p31,p32,p34,p35,p36;
    ArrayList<Integer> seatNumbers;
    boolean isSelected[];
    boolean isChoice[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mainy);

        Intent intent = getIntent();
        String tripKey = intent.getStringExtra("tripKey");
        name1 = findViewById(R.id.textView);
        name1.setText("Nhà xe "+tripKey);
        Intent intent1 = getIntent();
        String spiner1 = intent1.getStringExtra("spinner1");
        String spiner2 = intent1.getStringExtra("spinner2");
        String datevalue = intent1.getStringExtra("datevalue");
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        date1 = findViewById(R.id.textView3);
        from.setText(spiner1);
        to.setText(spiner2);
        date1.setText(datevalue);

        reload1 = findViewById(R.id.reload);

        p = new ImageView[37];
        isSelected = new boolean[37];
        isChoice = new boolean[37];

        for (int i = 1; i < p.length; i++) {
            int resourceId = getResources().getIdentifier("p" + (i), "id", getPackageName());
            p[i] = findViewById(resourceId);
            final int position = i; // Lưu trữ vị trí của ImageView

            if (seatNumbers != null && seatNumbers.contains(i)) {
                // Nếu vị trí i có trong danh sách seatNumbers, gán hình ảnh booked_img
                p[i].setImageResource(R.drawable.booked_img);
                isSelected[i] = true; // Đánh dấu là đã đặt chỗ
            } else {
                // Nếu vị trí i không có trong danh sách seatNumbers, gán hình ảnh available_img
                p[i].setImageResource(R.drawable.available_img);
                isSelected[i] = false; // Đánh dấu là chưa đặt chỗ
                isChoice[i] = false;
            }

            p[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isSelected[position]){
                        if (!isChoice[position]) {
                            // Chọn ImageView nếu chưa được chọn
                            p[position].setImageResource(R.drawable.your_seat_img);
                            isChoice[position] = true;
                        }else{
                            // Bỏ chọn ImageView nếu đã được chọn trước đó
                            p[position].setImageResource(R.drawable.available_img);
                            isChoice[position] = false;
                        }
                    }
                }
            });
        }

        // Tách ngày/tháng/năm thành các phần riêng biệt
        String[] dateParts = datevalue.split("/");
        int selectedDay = Integer.parseInt(dateParts[0]);
        int selectedMonth = Integer.parseInt(dateParts[1]);
        int selectedYear = Integer.parseInt(dateParts[2]);

        // Tham chiếu đến nút "trips" trong Firebase
        DatabaseReference tripsRef = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("trips");

        // Tạo một truy vấn (query) để tìm kiếm trip theo tripKey
        Query searchQuery = tripsRef.child(tripKey);

        seatNumbers = new ArrayList<>();
        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                seatNumbers.clear(); // Xóa dữ liệu cũ trong seatNumbers
                DataSnapshot tripSnapshot = dataSnapshot.child("trip").child("2023").child("05").child("22");

                if (tripSnapshot.hasChild("Booking")) {
                    for (DataSnapshot bookingSnapshot : tripSnapshot.child("Booking").getChildren()) {
                        Integer seatNumber = bookingSnapshot.child("seatNumber").getValue(Integer.class);
                        seatNumbers.add(seatNumber);
                    }
                }

                // Cập nhật lại giao diện khi có dữ liệu mới
                for (int i = 1; i < p.length; i++) {
                    if (seatNumbers != null && seatNumbers.contains(i)) {
                        // Nếu vị trí i có trong danh sách seatNumbers, gán hình ảnh booked_img
                        p[i].setImageResource(R.drawable.booked_img);
                        isSelected[i] = true; // Đánh dấu là đã đặt chỗ
                    } else {
                        // Nếu vị trí i không có trong danh sách seatNumbers, gán hình ảnh available_img
                        p[i].setImageResource(R.drawable.available_img);
                        isSelected[i] = false; // Đánh dấu là chưa đặt chỗ
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần thiết
            }
        });





        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main3Activity.this, Main2Activity.class);
                startActivity(intent);
            }
        });

        book = findViewById(R.id.book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference bookingsRef = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference("trips").child(tripKey).child("trip").child("2023").child("05").child("22").child("Booking");

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
                String bookingTime = sdf.format(new Date());

                ArrayList<Integer> selectedSeatNumbers = new ArrayList<>();
                for (int i = 1; i < isChoice.length; i++) {
                    if (isChoice[i]) {
                        selectedSeatNumbers.add(i);
                    }
                }

                for (Integer seatNumber : selectedSeatNumbers) {
                    final boolean[] isSeatNumberUnique = {true};

                    // Kiểm tra xem seatNumber đã tồn tại trong cơ sở dữ liệu hay chưa
                    bookingsRef.orderByChild("seatNumber").equalTo(seatNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                isSeatNumberUnique[0] = false;
                            }

                            // Nếu seatNumber không tồn tại trong cơ sở dữ liệu, thực hiện lưu thông tin booking
                            if (isSeatNumberUnique[0]) {
                                String bookingId = bookingsRef.push().getKey();
                                bookingsRef.child(bookingId).child("userId").setValue(uid);
                                bookingsRef.child(bookingId).child("bookingTime").setValue(bookingTime);
                                bookingsRef.child(bookingId).child("seatNumber").setValue(seatNumber);
                                Intent intent = new Intent(Main3Activity.this, Main1Activity.class);
                                startActivity(intent);
                                Toast.makeText(Main3Activity.this, "Đặt vé thành công", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(Main3Activity.this, "Vị trí bạn chọn đã được đặt", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Xử lý lỗi nếu cần thiết
                        }
                    });
                }
            }
        });
    }
}
