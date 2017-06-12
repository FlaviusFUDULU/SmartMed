package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ffudulu.licenta.Notification;
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

public class FragmentNewsFeed extends Fragment {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mRef;
    private DatabaseReference mRef2;

    private ImageView mProfileImage;
    private ImageView mProfileImageSmall;
    private int mMaxScrollSize;

    private RecyclerView mRecyclerNewsFeed;

    private FileCacher<UserMedic> userCacher;
    private FileCacher<UserPersonalData> userPacientCacher;
    private FileCacher<String> userPacientUidCacher;
    private FileCacher<String> actionCacher;
    private FileCacher<String> actionCacherDrawer;
    private FileCacher<String> userCacherType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed_empty, container, false);

        mRef2 = FirebaseDatabase.getInstance().getReference();

        userCacher = new FileCacher<>(getActivity(), firebaseUser.getUid());

        mRecyclerNewsFeed = (RecyclerView) view.findViewById(R.id.RecycleNewsFeed);
//        mRecyclerViewALlPacients.setHasFixedSize(true);
        mRecyclerNewsFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        userCacherType = new FileCacher<>(getActivity(), "type");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        FirebaseRecyclerAdapter<Notification, PersonalDataHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Notification, PersonalDataHolder>(
                        Notification.class,
                        R.layout.fragment_template_news_feed,
                        PersonalDataHolder.class,
                        mRef

                ) {
                    @Override
                    protected void populateViewHolder(final PersonalDataHolder viewHolder,
                                                      final Notification model, int position) {
                        try {
                            if (userCacherType.readCache().contains("Staff")){
                                if (model.getType().contains("New Medic")) {
                                    viewHolder.setName(model.getUserMedic().getFirstName() + " " +
                                            model.getUserMedic().getLastName());
                                    viewHolder.setMessage(model.getMessage());
                                    viewHolder.setPhoto(model.getUserMedic().getPhotoUrl());
                                    viewHolder.mNewInternare.setVisibility(View.GONE);
                                }
                                if (model.getType().contains("New Pacient")){
                                    viewHolder.setName(model.getUserPacient().getEmail());
                                    viewHolder.setMessage(model.getMessage());
                                    //viewHolder.setPhoto(model.getUserMedic().getPhotoUrl());
                                    viewHolder.mNewInternare.setVisibility(View.VISIBLE);
                                    viewHolder.mNewInternare
                                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                userPacientUidCacher = new FileCacher<>(getActivity(), "UID");
                                                userPacientUidCacher.writeCache(model.getUserPacient().getuId().toString());
                                                userPacientCacher = new FileCacher<>(getActivity(), model.getUserPacient().getuId());
                                                userPacientCacher.writeCache(model.getUserPacient());

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            Intent mSubmitData = new Intent(getActivity(), SubmitPersonalDataPacient.class);
                                            startActivity(mSubmitData);
                                        }
                                    });

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

        mRecyclerNewsFeed.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PersonalDataHolder extends RecyclerView.ViewHolder{

        View mView;
        int status = -1;

        TextView mFullName;
        TextView mMessage;
        ImageView mProfilePic;
        ImageView mNewInternare;


        public PersonalDataHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mFullName = (TextView) mView.findViewById(R.id.NewsFeedTmp_fullName);
            mMessage = (TextView) mView.findViewById(R.id.NewsFeedTmp_message);
            mProfilePic = (ImageView) mView.findViewById(R.id.NewsFeedTmp_photo);
            mNewInternare = (ImageView) mView.findViewById(R.id.NewsFeedTmp_newInternare);

        }

        public void setName(String fullName){
            mFullName.setText(fullName);

        }
        public void setMessage(String message){
            mMessage.setText(message);

        }public void setPhoto(String photoUrl){
            Picasso.with(mView.getContext()).load(photoUrl)
                    .transform(new CircleTransform()).noFade()
//                .centerInside().resize(mProfileImage.getMaxWidth(), mProfileImage.getMaxHeight())
                    .into(mProfilePic);

        }

    }

}
