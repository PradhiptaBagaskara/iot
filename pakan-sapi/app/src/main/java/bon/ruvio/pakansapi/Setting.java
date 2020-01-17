package bon.ruvio.pakansapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import bon.ruvio.pakansapi.model.SettingModel;

public class Setting extends AppCompatActivity {
    DatabaseReference active,nyala, setting,jamMakan;
    SwitchButton sOtomasi,sMode;
    LinearLayout lMode,lLama;
    EditText EditlamaNyala, jamMakan1, jamMakan2;
    Button saveLamaNyala;
    TextView statusView;

    SettingModel mSeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSeting = new SettingModel();
        lLama = (LinearLayout) findViewById(R.id.linierLama);
        lMode = (LinearLayout) findViewById(R.id.linierMode);
        sOtomasi = (SwitchButton) findViewById(R.id.modeAuto);

        sMode = (SwitchButton) findViewById(R.id.modeNyala);
        EditlamaNyala = (EditText) findViewById(R.id.edtLamaNyala);
        saveLamaNyala = (Button) findViewById(R.id.saveLama);
        jamMakan1 = (EditText) findViewById(R.id.edtMakan1);
        jamMakan2 =(EditText) findViewById(R.id.edtMakan2);
        lLama.setVisibility(View.GONE);
        lMode.setVisibility(View.GONE);
        statusView = (TextView) findViewById(R.id.statusView) ;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        active = database.getReference("K01").child("is_auto");
        jamMakan = database.getReference("K01").child("jam_makan");
        final Handler handler = new Handler();

        jamMakan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final HashMap<Integer, Integer> map =new HashMap<>();
                int num = 0;

                for (DataSnapshot data: dataSnapshot.getChildren()){

                    for( DataSnapshot dt: data.getChildren()){
                       Integer myData = dt.getValue(Integer.class);
                       Log.d("mydata", "onDataChange: "+myData);
                       map.put(num, myData);


                   }
                num = num+1;
                }

                        // Do something after 5s = 5000ms
                        try {
                            int data1=map.get(0);
                            if (String.valueOf(data1) == null) data1 = 5;
                            int data2=map.get(1);
                            if (String.valueOf(data2) == null){
                                data2 = 5;
                            }
                            Log.d("data1", "onDataChange: "+data1);
                            Log.d("data2", "onDataChange: "+data2);

                            jamMakan1.setText(String.valueOf(data1));
                            jamMakan2.setText(String.valueOf(data2));
                        } catch (Exception e) {
                            e.printStackTrace();

                        }





//                Log.d("map", "onDataChange: "+data1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        active.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean val = dataSnapshot.getValue(Boolean.class);
                if (val){
                    sOtomasi.setChecked(true);
                    lLama.setVisibility(View.VISIBLE);
                    lMode.setVisibility(View.GONE);
                }else {
                    sOtomasi.setChecked(false);
                    lLama.setVisibility(View.GONE);
                    lMode.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        nyala = database.getReference("K01").child("is_active");
        nyala.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean val = dataSnapshot.getValue(Boolean.class);
                if (val){
                    sMode.setChecked(true);
                    statusView.setText("mesin menyala");
                    statusView.setTextColor(getResources().getColor(R.color.content));

                }else {
                    sMode.setChecked(false);
                    statusView.setText("mesin mati");
                    statusView.setTextColor(getResources().getColor(R.color.colorAccent));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        setting = database.getReference("K01").child("setting").child("lama_nyala");
        setting.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                EditlamaNyala.setText(String.valueOf(value));

                //setLamaNyala(value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d("lama fungsi", "onDone: "+mSeting.getLamaNyala());
        sOtomasi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    active.setValue(b);
                    Snackbar.make(compoundButton, "Mode Automatis Diaktifkan ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }else {
                    active.setValue(b);
                    Snackbar.make(compoundButton, "Mode Automatis Dimatikan ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                }
            }
        });
        sMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    nyala.setValue(b);
                    Snackbar.make(compoundButton, "Mesin Dinyalakan", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }else {
                    nyala.setValue(b);
                    Snackbar.make(compoundButton, "Mesin Dimatikan", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            }
        });

        saveLamaNyala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int val = Integer.valueOf(EditlamaNyala.getText().toString());
                final int jam1 = Integer.valueOf(jamMakan1.getText().toString());
                final int jam2 = Integer.valueOf(jamMakan2.getText().toString());
                jamMakan.removeValue();

                setting.setValue(val);
                jamMakan.child(jamMakan1.getText().toString()).child("jam").setValue(jam1);
                jamMakan.child(jamMakan2.getText().toString()).child("jam").setValue(jam2);
//

                Intent i = new Intent(Setting.this, Setting.class);
                startActivity(i);
                finish();






            }
        });

    }



}
