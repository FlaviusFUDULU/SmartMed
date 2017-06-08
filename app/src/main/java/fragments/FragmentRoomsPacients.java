package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ffudulu.licenta.R;
import com.example.ffudulu.licenta.SubmitPersonalDataPacient;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kosalgeek.android.caching.FileCacher;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import transformations.CircleTransform;
import users.UserMedic;
import users.UserPersonalData;

public class FragmentRoomsPacients extends Fragment {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mRef;

    private ImageView mProfileImage;
    private ImageView mProfileImageSmall;
    private int mMaxScrollSize;

    private RecyclerView mRecyclerViewALlPacients;

    private FileCacher<UserMedic> userCacher;
    private FileCacher<UserPersonalData> userPacientCacher;
    private FileCacher<String> userPacientUidCacher;
    private final FileCacher<String> userCacherType = new FileCacher<>(getActivity(), "type");
    private FileCacher<String> roomCacher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_pacient, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Pacient");

        userCacher = new FileCacher<>(getActivity(), firebaseUser.getUid());

        mRecyclerViewALlPacients = (RecyclerView) view.findViewById(R.id.RecycleRoomPacients);
//        mRecyclerViewALlPacients.setHasFixedSize(true);
        mRecyclerViewALlPacients.setLayoutManager(new LinearLayoutManager(getActivity()));
        roomCacher = new FileCacher<>(getActivity(), "room");
        TextView mTitle = (TextView) view.findViewById(R.id.PacientsRoom_title);
        try {
            mTitle.setText("Pacienții salonului: "+ roomCacher.readCache());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<UserPersonalData, PersonalDataHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<UserPersonalData, PersonalDataHolder>(
                        UserPersonalData.class,
                        R.layout.fragment_display_room_pacients,
                        PersonalDataHolder.class,
                        mRef

                ) {
                    @Override
                    protected void populateViewHolder(PersonalDataHolder viewHolder, final UserPersonalData model, int position) {
                        try {
                            roomCacher = new FileCacher<>(getActivity(), "room");
                            if (roomCacher.readCache().contains(model.getRoom())) {
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
                                        Intent mGotoT = new Intent(getActivity(), SubmitPersonalDataPacient.class);
                                        startActivity(mGotoT);
                                    }
                                });
                            } else {
                                viewHolder.mCardViewPacient.setVisibility(View.GONE);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
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
        CardView mCardViewPacient;


        public PersonalDataHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mSeeProfile = (ImageView) mView.findViewById(R.id.AllPacientsRoom_seeProfile);
            mSeeMedicalRecord = (ImageView) mView.findViewById(R.id.AllPacientsRoom_seeMedicalRecord);
            mCardViewPacient = (CardView) mView.findViewById(R.id.cardViewNume);


        }

        public void setName(String fullName){
            TextView mFullName = (TextView) mView.findViewById(R.id.AllPacientsRoom_fullName);
            mFullName.setText(fullName);

        }

        public void setSalon(String salon){
            TextView mSalon = (TextView) mView.findViewById(R.id.AllPacientsRoom_salon);
            mSalon.setText(salon);
        }

        public void setPicture(String photoUrl){
            ImageView mImgProfilePic = (ImageView) mView.findViewById(R.id.AllPacientsRoom_photo);
            Picasso.with(mView.getContext()).load(photoUrl)
                    .transform(new CircleTransform()).noFade()
//                .centerInside().resize(mProfileImage.getMaxWidth(), mProfileImage.getMaxHeight())
                    .into(mImgProfilePic);
        }

    }

}
