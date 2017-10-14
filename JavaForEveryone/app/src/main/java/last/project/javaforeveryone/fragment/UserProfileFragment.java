package last.project.javaforeveryone.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

import last.project.javaforeveryone.R;
import last.project.javaforeveryone.utility.Utils;

/**
 * Created by plame_000 on 26-Oct-17.
 */

public class UserProfileFragment extends AnimatedFragment {
    public static final int GALLERY_REQUEST = 30;
    private static final String FB_STORAGE_PATH = "images/";

    private String userType, userImage, userUrl, userDisplyName;
    private int userPtsFromDB, userStageFormDB;

    private Button btnCancel, btnUpdate, btnUploadImage;
    private ImageView image;
    private EditText username, pass, confirm, oldPass;
    private TextView userPts, userStage;

    boolean flagUsername, flagPass = false;
    boolean isImageUpload = false;
    boolean proggressFinish = false;

    private Uri resultUri;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference mStorageReference;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userType = bundle.getString("userType");
            userPtsFromDB = bundle.getInt("userPts");
            userStageFormDB = bundle.getInt("userStage");
            userImage = bundle.getString("userImage");
            userDisplyName = bundle.getString("username");

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.update_profile_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        image = (ImageView) getActivity().findViewById(R.id.imgProfileChange);

        userPts = (TextView) getActivity().findViewById(R.id.txtUserPts);
        userPts.setText("Current points: "+userPtsFromDB);
        userStage = (TextView) getActivity().findViewById(R.id.txtUserCurrentStage);
        userStage.setText("Current stage: "+userStageFormDB);

        username = (EditText) getActivity().findViewById(R.id.txtUsernameChange);
        pass = (EditText) getActivity().findViewById(R.id.txtPassChange);
        confirm = (EditText) getActivity().findViewById(R.id.txtPassConfirmChange);
        oldPass = (EditText) getActivity().findViewById(R.id.txtOldPasswordChange);
        editTextListener();

        btnCancel = (Button) getActivity().findViewById(R.id.btnProfileCancel);
        btnUpdate = (Button) getActivity().findViewById(R.id.btnProfileUpdate);
        btnUploadImage = (Button) getActivity().findViewById(R.id.btnProfileImgUpload);
        buttonListener();

        if(userType.equals("google")){
            setupFields();
        }else if(userType.equals("facebook")){
            setupFields();
        }else{
            username.setText(userDisplyName);
            setupUserImage();
        }

    }

    private void setupUserImage() {

        StorageReference gsReference = FirebaseStorage.getInstance().getReference();
        gsReference.child(userImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                userUrl = uri.toString();
                Bitmap bitmap = Utils.getUserImageFrom(userUrl,getActivity());

                RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                round.setCircular(true);
                image.setImageDrawable(round);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setupFields() {
        username.setTextColor(Color.WHITE);
        Picasso.with(getContext()).load(user.getPhotoUrl()).into(image);
        username.setTypeface(null, Typeface.BOLD);
        username.setText("Name: " + user.getDisplayName());
        username.setEnabled(false);

        btnUpdate.setClickable(false);
        btnUpdate.setBackgroundColor(Color.GRAY);

        oldPass.setTextColor(Color.WHITE);
        oldPass.setTypeface(null, Typeface.BOLD);
        oldPass.setText("000000");
        oldPass.setEnabled(false);

        pass.setTextColor(Color.WHITE);
        pass.setTypeface(null, Typeface.BOLD);
        pass.setText("000000");
        pass.setEnabled(false);

        confirm.setTextColor(Color.WHITE);
        confirm.setTypeface(null, Typeface.BOLD);
        confirm.setText("000000");
        confirm.setEnabled(false);

        btnUploadImage.setClickable(false);
        btnUploadImage.setBackgroundColor(Color.GRAY);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {

            Uri imageUri = data.getData();

            Intent intent = CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .setMinCropResultSize(100, 100)
                    .setMaxCropResultSize(2660, 2660)
                    .getIntent(getContext());
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == getActivity().RESULT_OK) {

                resultUri = result.getUri();

                InputStream inputStream;

                try {
                    inputStream = getActivity().getContentResolver().openInputStream(resultUri);

                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), image);
                    round.setCircular(true);

                    this.image.setImageDrawable(round);

                    isImageUpload = true;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    isImageUpload = false;
                    Toast.makeText(getActivity(), "Снимката НЕ е заредена...", Toast.LENGTH_LONG).show();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(), "Нещо се обърка с изрязването...", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void buttonListener() {

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .remove(UserProfileFragment.this).commit();
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });

    }


    @TargetApi(Build.VERSION_CODES.M)
    private void updateInfo() {

        if (flagPass && flagUsername && (pass.getText().toString().equals(confirm.getText().toString()))) {
            if(resultUri == null){
                userUrl = FB_STORAGE_PATH + user.getUid() + ".jpg";
                storeNewUserInfo(userUrl);
            }
            else if (resultUri != null) {
                final ProgressDialog dialog = new ProgressDialog(getContext());
                dialog.setTitle("Uploading image...");
                dialog.show();

                final String imagePath = FB_STORAGE_PATH + user.getUid() + "." + getImageExt(resultUri);

                StorageReference ref = mStorageReference.child(imagePath);
                ref.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();

                        storeNewUserInfo(imagePath);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                proggressFinish = true;
                                Toast.makeText(getActivity(), e.getMessage() + " on faliture", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                dialog.setMessage("Качено " + (int) progress + "%");

                            }
                        });
            }

        } else {
            Utils.createToast(getActivity(),"Невалидни полета...");
            return;
        }
    }

    // this method get ext. of image (jpg, png, etc.)
    public String getImageExt(Uri uri) {
        return uri.toString().substring(uri.toString().lastIndexOf(".") + 1);
    }

    private void storeNewUserInfo(String imagePath) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference();
        final String userID = user.getUid();

        ref.child("users").child(userID).child("image").setValue(imagePath);
        ref.child("users").child(userID).child("name").setValue(username.getText().toString());

        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldPass.getText().toString());

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    getActivity().recreate();
                                    getFragmentManager().beginTransaction()
                                    .remove(UserProfileFragment.this).commit();
                                }
                            });
                        } else {
                            Utils.createToast(getActivity(),"Промените НЕ са запазени... Проверете полетата...");
                        }
                    }
                });
    }

    private void editTextListener() {

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Utils.isValidUsername(s.toString())) {
                    flagUsername = true;
                } else {
                    flagPass = false;
                    username.setError("Само букви и цифри...");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Utils.isValidPassword(s.toString())) {
                    flagPass = true;
                } else {
                    flagPass = false;
                    pass.setError("Само букви и цифри. Повече от 6 символа.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
