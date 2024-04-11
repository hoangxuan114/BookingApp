package com.shashank.platform.busbookingappui;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.*;

public class Main2Activity extends AppCompatActivity {

    CardView cardView;
    TextView from;
    TextView to;
    TextView date1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mainz);
        cardView = findViewById(R.id.cardView);

        Intent intent1 = getIntent();
        String spiner1 = intent1.getStringExtra("spinner1");
        String spinner2 = intent1.getStringExtra("spinner2");
        String datevalue = intent1.getStringExtra("datevalue");
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        date1 = findViewById(R.id.date);
        from.setText(spiner1);
        to.setText(spinner2);
        date1.setText(datevalue);

        // Tách ngày/tháng/năm thành các phần riêng biệt
        String[] dateParts = datevalue.split("/");
        int selectedDay = Integer.parseInt(dateParts[0]);
        int selectedMonth = Integer.parseInt(dateParts[1]);
        int selectedYear = Integer.parseInt(dateParts[2]);

        // Tham chiếu đến nút "trips" trong Firebase
        DatabaseReference tripsRef3 = FirebaseDatabase.getInstance("https://doancs3-a062d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("trips");

        // Tạo một truy vấn (query) để tìm kiếm các trip theo các điều kiện
        Query searchQuery = tripsRef3.orderByChild("from").equalTo(spiner1);

        LinearLayout cardContainer = findViewById(R.id.linear1); // Thay thế "cardContainer" bằng ID của Layout chứa các CardView

        // ...

        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cardContainer.removeAllViews(); // Xóa các CardView hiện có trước khi tạo mới

                int foundResult = 0;
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    // Lấy thông tin từ trip
                    String to = tripSnapshot.child("to").getValue(String.class);
                    String tripTime = tripSnapshot.child("time").getValue(String.class);
                    Integer availableSeats = tripSnapshot.child("trip").child(String.valueOf(selectedYear))
                            .child(String.format("%02d", selectedMonth)).child(String.format("%02d", selectedDay))
                            .child("Available").getValue(Integer.class);

                    if (to != null && to.equals(spinner2) && availableSeats != null && availableSeats > 0) {
                        foundResult++;
                        // Tạo mới CardView và các thành phần bên trong
                        // Create a new CardView
                        CardView cardView = new CardView(Main2Activity.this);
                        cardView.setId(View.generateViewId());

                        LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        int cardViewMargin = getResources().getDimensionPixelSize(R.dimen.card_margin);
                        cardViewLayoutParams.setMargins(cardViewMargin, cardViewMargin, cardViewMargin, cardViewMargin);
                        cardView.setLayoutParams(cardViewLayoutParams);
                        cardView.setCardElevation(getResources().getDimensionPixelSize(R.dimen.card_corner_radius));
                        cardView.setUseCompatPadding(true);
                        cardView.setContentPadding(
                                getResources().getDimensionPixelSize(R.dimen.card_content_padding),
                                getResources().getDimensionPixelSize(R.dimen.card_content_padding),
                                getResources().getDimensionPixelSize(R.dimen.card_content_padding),
                                getResources().getDimensionPixelSize(R.dimen.card_content_padding)
                        );

                        LinearLayout linearLayout = new LinearLayout(Main2Activity.this);
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setWeightSum(100);

                        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                60
                        );

                        LinearLayout linearLayoutLeft = new LinearLayout(Main2Activity.this);
                        linearLayoutLeft.setLayoutParams(linearLayoutParams);
                        linearLayoutLeft.setOrientation(LinearLayout.VERTICAL);

                        TextView textView1 = new TextView(Main2Activity.this);
                        textView1.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        textView1.setText(tripTime);
                        textView1.setTextColor(Color.parseColor("#000000"));
                        textView1.setTypeface(Typeface.create("calibri", Typeface.BOLD));

                        TextView textView2 = new TextView(Main2Activity.this);
                        textView2.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        textView2.setText(tripSnapshot.getKey());
                        textView2.setTextColor(Color.parseColor("#4b4b4b"));
                        textView2.setTypeface(Typeface.create("calibri", Typeface.BOLD));

                        TextView textView3 = new TextView(Main2Activity.this);
                        textView3.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        textView3.setText(tripSnapshot.child("pickup").getValue(String.class));
                        textView3.setTextColor(Color.parseColor("#8a8a8a"));
                        textView3.setTypeface(Typeface.create("calibri", Typeface.NORMAL));

                        linearLayoutLeft.addView(textView1);
                        linearLayoutLeft.addView(textView2);
                        linearLayoutLeft.addView(textView3);

                        LinearLayout linearLayoutRight = new LinearLayout(Main2Activity.this);
                        linearLayoutRight.setLayoutParams(new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                40
                        ));
                        linearLayoutRight.setGravity(Gravity.CENTER);
                        linearLayoutRight.setOrientation(LinearLayout.VERTICAL);

                        TextView textView4 = new TextView(Main2Activity.this);
                        textView4.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        textView4.setText(String.valueOf(tripSnapshot.child("price").getValue(Integer.class)));
                        textView4.setTextColor(Color.parseColor("#f5a622"));
                        textView4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                        textView4.setTypeface(Typeface.create("calibri", Typeface.BOLD));
                        textView4.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

                        linearLayoutRight.addView(textView4);

                        TextView textView5 = new TextView(Main2Activity.this);
                        textView5.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        textView5.setText(tripSnapshot.child("phone").getValue(String.class));
                        textView5.setTextColor(Color.parseColor("#0060c4"));
                        textView5.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        textView5.setTypeface(Typeface.create("calibri", Typeface.BOLD));
                        textView5.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

                        linearLayoutRight.addView(textView5);

                        linearLayout.addView(linearLayoutLeft);
                        linearLayout.addView(linearLayoutRight);

                        cardView.addView(linearLayout);
                        cardContainer.addView(cardView);

                        final String tripKey = tripSnapshot.getKey();

                        cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Xử lý khi nhấn vào CardView
                                Intent intent = new Intent(Main2Activity.this, Main3Activity.class);
                                intent.putExtra("tripKey", tripKey);
                                intent.putExtra("spinner1", spiner1);
                                intent.putExtra("spinner2", spinner2);
                                intent.putExtra("datevalue", datevalue);
                                startActivity(intent);
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần thiết
            }
        });
    }
}
