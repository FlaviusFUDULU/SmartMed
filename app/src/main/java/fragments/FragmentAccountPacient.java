package fragments;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ffudulu.licenta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kosalgeek.android.caching.FileCacher;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import transformations.CircleTransform;
import transformations.FakePageFragment;
import users.UserPersonalData;

public class FragmentAccountPacient extends Fragment
        implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private ImageView mProfileImage;
    private ImageView mProfileImageSmall;
    private int mMaxScrollSize;

    private FileCacher<UserPersonalData> userCacherPacient;
    private final FileCacher<String> userCacherType = new FileCacher<>(getActivity(), "type");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_pacient_details, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);


        userCacherPacient = new FileCacher<>(getActivity(), firebaseUser.getUid());
        TextView mName = (TextView) view.findViewById(R.id.Pacient_cardView_text_nume);
        try {
            mName.setText(userCacherPacient.readCache().getFirstName()+ " "
                    + userCacherPacient.readCache().getLastName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView mEmailCard = (TextView) view.findViewById(R.id.Pacient_cardView_text_email);
        try {
            mEmailCard.setText(userCacherPacient.readCache().getEmail());
        } catch (IOException e) {
            e.printStackTrace();
        }

        AppBarLayout appbarLayout = (AppBarLayout) view.findViewById(R.id.PacientAccount_appbar);

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        TextView mFullName = (TextView) view.findViewById(R.id.PacientAccount_full_name);
        try {
            mFullName.setText(userCacherPacient.readCache().getFirstName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView mEmail = (TextView) view.findViewById(R.id.PacientAccount_email);
        try {
            mEmail.setText(userCacherPacient.readCache().getLastName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView mHospitalName = (TextView) view.findViewById(R.id.Pacient_cardView_text_spital);
        try {
            mHospitalName.setText(userCacherPacient.readCache().getId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView mCNP = (TextView) view.findViewById(R.id.Pacient_cardView_text_CNP);
        try {
            mCNP.setText(userCacherPacient.readCache().getCnp());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView mID = (TextView) view.findViewById(R.id.Pacient_cardView_text_ID);
        try {
            mID.setText(userCacherPacient.readCache().getId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView mAge = (TextView) view.findViewById(R.id.Pacient_cardView_text_age);
        try {
            mAge.setText(userCacherPacient.readCache().getAge());
        } catch (IOException e) {
            e.printStackTrace();
        }


        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("SmartMedProfile", Context.MODE_PRIVATE);
        File myProfilePic = new File(directory, firebaseUser.getUid());
        mProfileImage = (ImageView) view.findViewById(R.id.PacientAccount_profile_image);
        Picasso.with(getActivity()).load(myProfilePic)
                .transform(new CircleTransform()).noFade()
//                .centerInside().resize(mProfileImage.getMaxWidth(), mProfileImage.getMaxHeight())
                .into(mProfileImage);



        return view;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mProfileImageSmall = (ImageView) getView()
                .findViewById(R.id.PacientAccount_profile_image_small);
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;


        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            mProfileImage.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(200)
                    .start();
            mProfileImageSmall.setVisibility(getView().VISIBLE);

            ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
            File directory = cw.getDir("SmartMedProfile", Context.MODE_PRIVATE);
            File myProfilePicSmall = new File(directory, firebaseUser.getUid());
            Picasso.with(getActivity()).load(myProfilePicSmall)
                    .transform(new CircleTransform()).noFade()
//                 .centerInside().resize(mProfileImage.getMaxWidth(), mProfileImage.getMaxHeight())
                    .into(mProfileImageSmall);

        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            mProfileImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();
            mProfileImageSmall.setVisibility(getView().INVISIBLE);
        }
    }
    private static class TabsAdapter extends FragmentPagerAdapter {
        private static final int TAB_COUNT = 2;

        TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public Fragment getItem(int i) {
            return FakePageFragment.newInstance();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Tab " + String.valueOf(position);
        }
    }



}
