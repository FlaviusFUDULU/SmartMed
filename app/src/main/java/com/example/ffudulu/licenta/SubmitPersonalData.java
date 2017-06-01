package com.example.ffudulu.licenta;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import users.UserMedic;

public class SubmitPersonalData extends Activity {

    private String[] personalType = {"Doctor" , "Asistent"};
    private ArrayAdapter<String> adapterPersonalType;
    private Spinner mPersonalTypeSpinner;
    private FirebaseUser firebaseUser;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mHospitalName;
    private EditText mSectionName;
    private Button mBtnSave;
    private String firstName;
    private String lastName;
    private String hospitalName;
    private String sectionName;
    private String rank;
    private FirebaseAuth mAuth;
    private ImageButton mPhotoUploadBtn;
    private ImageButton mTakePhotoBtn;
    private ImageView mImgProfilePic;
    private ProgressBar mProgressBarUpload;
    private Uri photoUrl;

    private DatabaseReference databaseRef;

    private static final int CAMERA_REQUEST_CODE = 1;

    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;

    private StorageReference mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_personal_data);


        mTakePhotoBtn = (ImageButton) findViewById(R.id.imageButtonPhoto);
        mPhotoUploadBtn = (ImageButton) findViewById(R.id.imageButtonUpload);
        mImgProfilePic = (ImageView) findViewById(R.id.ProfilePicture);

        mProgressBarUpload = (ProgressBar) findViewById(R.id.progressBarUpload);

        mStorage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        mFirstName = (EditText) findViewById(R.id.txtFirstName);
        mLastName = (EditText) findViewById(R.id.txtLastName);

        mHospitalName = (EditText) findViewById(R.id.txtHospitalName);
        mSectionName = (EditText) findViewById(R.id.txtSectia);

        //Spinner
        mPersonalTypeSpinner = (Spinner) findViewById(R.id.spinnerFunction);
        adapterPersonalType = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, personalType);
        adapterPersonalType = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, personalType);
        adapterPersonalType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPersonalTypeSpinner.setAdapter(adapterPersonalType);

        //END

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();//.child("Doctor");
        String fullname = null;

        //The rest


        mBtnSave = (Button) findViewById(R.id.btnSaveChanges);

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableAll();
                firstName = mFirstName.getText().toString().trim();
                lastName = mLastName.getText().toString().trim();
                hospitalName = mHospitalName.getText().toString().trim();
                sectionName = mSectionName.getText().toString().trim();
                rank = mPersonalTypeSpinner.getSelectedItem().toString().trim();

                if (!TextUtils.isEmpty(firstName) || !TextUtils.isEmpty(lastName) ||
                        !TextUtils.isEmpty(hospitalName) || !TextUtils.isEmpty(sectionName)
                        || !TextUtils.isEmpty(firebaseUser.getPhotoUrl().toString().trim())) {

                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(firstName + " " + lastName).setPhotoUri(photoUrl)
                            .build();
                    firebaseUser.updateProfile(profileUpdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        UserMedic userMedic = new UserMedic(firebaseUser.getEmail(),
                                                firstName, hospitalName, lastName,
                                                rank, sectionName);

                                        savePersonalData(firebaseUser, userMedic);

                                        Toast.makeText(SubmitPersonalData.this,
                                                "înregistrare reușită!" ,
                                                Toast.LENGTH_SHORT).show();
                                        enableAll();
                                    }
                                    Intent mInitialize = new Intent(SubmitPersonalData.this, Initialization.class);
                                    startActivity(mInitialize);
                                }
                            });
                }
                else{
                    Toast.makeText(SubmitPersonalData.this, "Toate câmpurile sunt necesare!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        mTakePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mCamera= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (mCamera.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(mCamera, CAMERA_REQUEST_CODE);
                }
                //mProgressBarUpload.setVisibility(View.VISIBLE);
            }
        });
        mPhotoUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Alege poza!"), SELECT_PICTURE);

            }
        });
        //end

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);

                StorageReference filepath = mStorage.child(selectedImageUri.getLastPathSegment()).
                        child(selectedImageUri.getLastPathSegment());

                mProgressBarUpload.setVisibility(View.VISIBLE);
                disableAll();
                mImgProfilePic.setVisibility(View.GONE);

                filepath.putFile(selectedImageUri).addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(SubmitPersonalData.this,
                                "Încărcare reușită!" ,
                                Toast.LENGTH_SHORT).show();
                        mProgressBarUpload.setVisibility(View.GONE);
                        photoUrl = taskSnapshot.getDownloadUrl();
                        Picasso.with(SubmitPersonalData.this).load(photoUrl)
                                .resize(200, 200).noFade()
                                .into(mImgProfilePic);
                        enableAll();
                        mImgProfilePic.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            Uri uri = data.getData();
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    private void disableAll(){
        mFirstName.setEnabled(false);
        mLastName.setEnabled(false);
        mTakePhotoBtn.setEnabled(false);
        mPhotoUploadBtn.setEnabled(false);
        mBtnSave.setEnabled(false);
    }

    private void enableAll(){
        mFirstName.setEnabled(true);
        mLastName.setEnabled(true);
        mTakePhotoBtn.setEnabled(true);
        mPhotoUploadBtn.setEnabled(true);
        mBtnSave.setEnabled(true);
    }

    private void savePersonalData(FirebaseUser firebaseUser, UserMedic userMedic){
        databaseRef.child("Users").child(userMedic.getRank()).child(firebaseUser.getUid()).setValue(userMedic);
    }
}
