package fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ffudulu.licenta.Notification;
import com.example.ffudulu.licenta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import users.UserPersonalData;

/**
 * Created by ffudulu on 13-Jun-17.
 */

public class FragmentMainDrawerPacient extends android.support.v4.app.Fragment {

    private ImageView mCall;
    private TextView mMessage;
    private FileCacher<UserPersonalData> userPacientCacher;
    private ImageView mAnulare;
    private ImageView mFinalizare;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseRef;
    private Notification notif = null;
    private FileCacher<Notification> notifCacher;
    private FileCacher<String> ownNotifCacher;
    private ProgressBar mProgressBarUpload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_drawer_pacient, container, false);

        mCall = (ImageView) view.findViewById(R.id.MainDrawerPacient_call);
        mMessage = (TextView) view.findViewById(R.id.MainDrawerPacient_message);
        mAnulare = (ImageView) view.findViewById(R.id.MainDrawerPacient_Anulare);
        mFinalizare = (ImageView) view.findViewById(R.id.MainDrawerPacient_Finalizare);
        mProgressBarUpload = (ProgressBar) view.findViewById(R.id.MainDrawerPacient_progressBarUpload);

        mMessage.setVisibility(View.GONE);
        mAnulare.setVisibility(View.GONE);
        mFinalizare.setVisibility(View.GONE);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ownNotifCacher = new FileCacher<>(getActivity(), "notifOwn");
                try {
                    if(dataSnapshot.hasChild("Notifications")) {
                        if (dataSnapshot.child("Notifications").hasChild(ownNotifCacher.readCache())) {
                            if (dataSnapshot.child("Notifications").child(ownNotifCacher.readCache())
                                    .getValue(Notification.class).getHandeled() != null) {
                                if (dataSnapshot.child("Notifications").child(ownNotifCacher.readCache())
                                        .getValue(Notification.class).getHandeled().contains("accesed")) {
                                    mMessage.setText("Se asteapta confirmare de la un cadru medical!");
                                    mMessage.setVisibility(View.VISIBLE);
                                    mCall.setVisibility(View.GONE);
                                    mAnulare.setVisibility(View.VISIBLE);
                                    mFinalizare.setVisibility(View.GONE);
                                } else if (dataSnapshot.child("Notifications").child(ownNotifCacher.readCache())
                                        .getValue(Notification.class).getHandeled().contains("In progress")) {
                                    mMessage.setText("Un cadru medical va veni la dumneavoastră!");
                                    mMessage.setVisibility(View.VISIBLE);
                                    mCall.setVisibility(View.GONE);
                                    mAnulare.setVisibility(View.GONE);
                                    mFinalizare.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAnulare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ownNotifCacher = new FileCacher<>(getActivity(), "notifOwn");
                try {
                    disableAll();
                    mProgressBarUpload.setVisibility(View.VISIBLE);
                    databaseRef.child("Notifications").child(ownNotifCacher.readCache()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    enableAll();
                                    mProgressBarUpload.setVisibility(View.GONE);
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mFinalizare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ownNotifCacher = new FileCacher<>(getActivity(), "notifOwn");
                try {
                    disableAll();
                    mProgressBarUpload.setVisibility(View.VISIBLE);
                    databaseRef.child("Notifications").child(ownNotifCacher.readCache()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    enableAll();
                                    mProgressBarUpload.setVisibility(View.GONE);
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseRef = FirebaseDatabase.getInstance().getReference();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                userPacientCacher = new FileCacher<>(getActivity(), firebaseUser.getUid());

                try {
                    notif = new Notification(
                            "Asistență medicală la salonul: "+ userPacientCacher.readCache().getRoom()
                                    +". Solicitat de pacientul " + userPacientCacher.readCache()
                                    .getFirstName()+" "+userPacientCacher.readCache().getLastName()
                            ,userPacientCacher.readCache() , "Staff only",
                            "0", "Asistenta Medicala", dateFormat.format(date).toString()
                    );
                    notif.setHandeled("accesed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMessage.setText("Se așteaptă răspuns de la un cadru medical...");
                mMessage.setVisibility(View.VISIBLE);
                ownNotifCacher = new FileCacher<>(getActivity(), "notifOwn");
                try {
                    ownNotifCacher.writeCache(notif.getDateAndTime().toString().replace("/","")
                            .replace(" ","").replace(":",""));
                    mCall.setEnabled(false);
                    mCall.setVisibility(View.GONE);
                    //getActivity().finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                disableAll();
                mProgressBarUpload.setVisibility(View.VISIBLE);
                databaseRef.child("Notifications").child(notif.getDateAndTime().toString().replace("/","")
                        .replace(" ","").replace(":","")).setValue(notif).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        enableAll();
                        mProgressBarUpload.setVisibility(View.GONE);
                    }
                });

            }
        });
        return view;
    }

    private void disableAll(){
        mCall.setVisibility(View.GONE);
        mMessage.setVisibility(View.GONE);
        mAnulare.setVisibility(View.GONE);
        mFinalizare.setVisibility(View.GONE);
    }

    private void enableAll(){
        mCall.setVisibility(View.VISIBLE);
        mMessage.setVisibility(View.VISIBLE);
        mAnulare.setVisibility(View.VISIBLE);
        mFinalizare.setVisibility(View.VISIBLE);
    }
}
