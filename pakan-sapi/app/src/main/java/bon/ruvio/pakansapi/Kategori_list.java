package bon.ruvio.pakansapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ValueEventListener;

import bon.ruvio.pakansapi.model.StatusMakan;

public class Kategori_list extends AppCompatActivity {
    private RecyclerView list_pentas;
    private static DatabaseReference mDatabase;
    private Query mQueryCurrent;

    private FirebaseRecyclerAdapter<StatusMakan, Kategori_list.EntryViewHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori_list);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("K01").child("status_makan");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v("key_makan", dataSnapshot.getKey());
                //Log.v("id_makan", dataSnapshot.getValue());
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Log.v("id_makan", snapshot.getKey());
                    //Log.v("app_downloads", snapshot.getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef.addListenerForSingleValueEvent(eventListener);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("K01").child("status_makan");
        mDatabase.keepSynced(true);
        mQueryCurrent = mDatabase.orderByChild("jam");
        Log.d("tgggg", "onCreate: " + mQueryCurrent.toString());

        list_pentas = (RecyclerView) findViewById(R.id.recycleKategori);
        list_pentas.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        list_pentas.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<StatusMakan> options = new FirebaseRecyclerOptions.Builder<StatusMakan>()
                .setQuery(mQueryCurrent, StatusMakan.class)
                .build();
//        options.getSnapshots();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<StatusMakan, EntryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EntryViewHolder entryViewHolder, int i, @NonNull StatusMakan statusMakan) {
                entryViewHolder.setTanggal(statusMakan.getTanggal());
            }

            @NonNull
            @Override
            public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }
        };
    }


    public class EntryViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tanggal;
        boolean Status;
        int Jam;
        String status_str, id_monitoring;

        public EntryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

//        TextView btnEdit = (TextView) mView.findViewById(R.id.txtKategori);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        }

        public void setTanggal(String title) {
            tanggal = (TextView) mView.findViewById(R.id.txtKategori);
            tanggal.setText(title);

        }

        public void setStatus(boolean status) {
            Status = status;
            if (Status) {
                status_str = "Sudah diberi Makan";
            } else {
                status_str = "Belum diberi Makan";
            }

        }


        public void setId_monitoring(String id) {
            id_monitoring = id;
        }

        public void setJam(int dJam) {
            Jam = dJam;
        }


    }

}