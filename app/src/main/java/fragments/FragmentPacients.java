package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ffudulu.licenta.NewInternare;
import com.example.ffudulu.licenta.R;
import com.example.ffudulu.licenta.SubmitPersonalDataPacient;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.android.caching.FileCacher;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import transformations.CircleTransform;
import users.UserMedic;
import users.UserPersonalData;

public class FragmentPacients extends Fragment {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mRef;
    private DatabaseReference mRef2;

    private ImageView mProfileImage;
    private ImageView mProfileImageSmall;
    private int mMaxScrollSize;

    private RecyclerView mRecyclerViewALlPacients;

    private FileCacher<UserMedic> userCacher;
    private FileCacher<UserPersonalData> userPacientCacher;
    private FileCacher<String> userPacientUidCacher;
    private FileCacher<String> actionCacher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_pacients, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Pacient");
        mRef2 = FirebaseDatabase.getInstance().getReference();

        userCacher = new FileCacher<>(getActivity(), firebaseUser.getUid());

        mRecyclerViewALlPacients = (RecyclerView) view.findViewById(R.id.RecycleAllPacients);
//        mRecyclerViewALlPacients.setHasFixedSize(true);
        mRecyclerViewALlPacients.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<UserPersonalData, PersonalDataHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<UserPersonalData, PersonalDataHolder>(
                        UserPersonalData.class,
                        R.layout.fragment_display_pacients,
                        PersonalDataHolder.class,
                        mRef

                ) {
                    @Override
                    protected void populateViewHolder(final PersonalDataHolder viewHolder, final UserPersonalData model, int position) {

                        if (model.getRoom() == null) {
                            viewHolder.mSeeMedicalRecord.setVisibility(View.GONE);
                            viewHolder.mSeeProfile.setVisibility(View.GONE);
                            viewHolder.mInternare.setVisibility(View.VISIBLE);
                            viewHolder.mFirstTreatment.setVisibility(View.GONE);
                            viewHolder.setName(model.getEmail());
                            viewHolder.setSalon("Nu s-a făcut internare!");

                            viewHolder.mInternare.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        userPacientUidCacher = new FileCacher<>(getActivity(), "UID");
                                        userPacientUidCacher.writeCache(model.getuId().toString());
                                        userPacientCacher = new FileCacher<>(getActivity(), model.getuId());
                                        userPacientCacher.writeCache(model);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Intent mSubmitData = new Intent(getActivity(), SubmitPersonalDataPacient.class);
                                    startActivity(mSubmitData);
                                }
                            });
                        } else {
                            //if(mRef.child("Tratamente").child(userPacientUidCacher.readCache()).getKey()!=null){
                            mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("Tratamente")
                                            .hasChild(model.getuId())) {
                                        viewHolder.mSeeMedicalRecord.setVisibility(View.VISIBLE);
                                        viewHolder.mSeeProfile.setVisibility(View.VISIBLE);
                                        viewHolder.mInternare.setVisibility(View.GONE);
                                        viewHolder.mFirstTreatment.setVisibility(View.GONE);
                                        //viewHolder.setPicture(model.getPhotoUrl().toString());
                                        viewHolder.setName(model.getFirstName() + " " + model.getLastName());
                                        viewHolder.setSalon(model.getRoom());
                                        viewHolder.setPicture(model.getPhotoUrl());

                                        viewHolder.mSeeProfile.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    userPacientUidCacher = new FileCacher<>(getActivity(), "UID");
                                                    userPacientUidCacher.writeCache(model.getuId().toString());
                                                    userPacientCacher = new FileCacher<>(getActivity(), model.getuId());
                                                    userPacientCacher.writeCache(model);

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                FragmentGeneralPacientAccount fragmentAccountPacientAccount =
                                                        new FragmentGeneralPacientAccount();
                                                android.support.v4.app.FragmentTransaction fragmentTransaction =
                                                        getFragmentManager().beginTransaction();
                                                fragmentTransaction.replace(R.id.main_fl_content, fragmentAccountPacientAccount);
                                                fragmentTransaction.commit();
                                            }
                                        });

                                        viewHolder.mSeeMedicalRecord.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    userPacientUidCacher = new FileCacher<>(getActivity(), "UID");
                                                    userPacientUidCacher.writeCache(model.getuId().toString());
                                                    userPacientCacher = new FileCacher<>(getActivity(), model.getuId());
                                                    userPacientCacher.writeCache(model);

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                FragmentTreatmentPacient fragmentTratament =
                                                        new FragmentTreatmentPacient();
                                                android.support.v4.app.FragmentTransaction fragmentTransaction =
                                                        getFragmentManager().beginTransaction();
                                                fragmentTransaction.replace(R.id.main_fl_content, fragmentTratament);
                                                fragmentTransaction.commit();

                                            }
                                        });
                                    } else {
                                        viewHolder.mSeeMedicalRecord.setVisibility(View.GONE);
                                        viewHolder.mSeeProfile.setVisibility(View.GONE);
                                        viewHolder.mInternare.setVisibility(View.GONE);
                                        viewHolder.mFirstTreatment.setVisibility(View.VISIBLE);

                                        viewHolder.setName(model.getFirstName() + " " + model.getLastName());
                                        viewHolder.setSalon(model.getRoom());
                                        viewHolder.setPicture(model.getPhotoUrl());

                                        viewHolder.mFirstTreatment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    userPacientUidCacher = new FileCacher<>(getActivity(), "UID");
                                                    userPacientUidCacher.writeCache(model.getuId().toString());
                                                    userPacientCacher = new FileCacher<>(getActivity(), model.getuId());
                                                    userPacientCacher.writeCache(model);

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                actionCacher = new FileCacher<>(getActivity(), "action");
                                                try {
                                                    actionCacher.writeCache("InitialTreatment");
                                                    Intent mSubmitData = new Intent(getActivity(), NewInternare.class);
                                                    startActivity(mSubmitData);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                };

        mRecyclerViewALlPacients.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PersonalDataHolder extends RecyclerView.ViewHolder{

        View mView;
        int status = -1;

        ImageView mSeeProfile;
        ImageView mSeeMedicalRecord;
        ImageView mInternare;
        ImageView mFirstTreatment;


        public PersonalDataHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mSeeProfile = (ImageView) mView.findViewById(R.id.AllPacients_seeProfile);
            mSeeMedicalRecord = (ImageView) mView.findViewById(R.id.AllPacients_seeMedicalRecord);
            mInternare = (ImageView) mView.findViewById(R.id.AllPacients_makeInternare);
            mFirstTreatment = (ImageView) mView.findViewById(R.id.AllPacients_firstTreatment);


        }

        public void setName(String fullName){
            TextView mFullName = (TextView) mView.findViewById(R.id.AllPacients_fullName);
            mFullName.setText(fullName);

        }

        public void setSalon(String salon){
            TextView mSalon = (TextView) mView.findViewById(R.id.AllPacients_salon);
            mSalon.setText(salon);
        }

        public void setPicture(String photoUrl){
            ImageView mImgProfilePic = (ImageView) mView.findViewById(R.id.AllPacients_photo);
            Picasso.with(mView.getContext()).load(photoUrl)
                    .transform(new CircleTransform()).noFade()
//                .centerInside().resize(mProfileImage.getMaxWidth(), mProfileImage.getMaxHeight())
                    .into(mImgProfilePic);
        }

    }

}
