package last.project.javaforeveryone.controller;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ldoublem.loadingviewlib.view.LVGhost;

import last.project.javaforeveryone.model.DBOperations;
import last.project.javaforeveryone.R;
import last.project.javaforeveryone.model.UserModel;
import last.project.javaforeveryone.utility.Utils;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private static int SIGN_IN = 0;

    private TextView tvSignupInvoker,tvSigninInvoker;
    private LinearLayout llSignup,llSignin, loginAttrLayout, registerAttrLayout;
    private Button btnSignup,btnSignin;
    private EditText edtUsernameRegister, edtPasswordRegister, edtEmailRegister, edtEmailLogin, edtPasswordLogin;
    private boolean usernameFlag, passwordFlag, emailFlag;
    private LVGhost ghostProgress;

    private GoogleApiClient googleApiClient;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseUser firebaseUser;

    private ImageButton facebookLogin,googleLogin;
    private LoginButton facebookPerformClick;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        setUpVariables();
        setUpErrorListeners();
        buildGoogleAPI();
        callbackManager = CallbackManager.Factory.create();
        buttonClickers();

    }

    /**
     * Sets up the error listeners for the
     * input of email/password/username
     */
    private void setUpErrorListeners() {
        edtUsernameRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Utils.isValidUsername(s.toString())){
                    usernameFlag = true;
                }else{
                    usernameFlag = false;
                    edtUsernameRegister.setError(getString(R.string.username_error_requirements));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtPasswordRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Utils.isValidPassword(s.toString())){
                    passwordFlag = true;
                }else{
                    passwordFlag = false;
                    edtPasswordRegister.setError(getString(R.string.password_error_requirements));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtEmailRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Utils.isValidEmail(s.toString())){
                    emailFlag = true;
                }else{
                    emailFlag = false;
                    edtEmailRegister.setError(getString(R.string.email_error_requirements));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    /**
     * Checks if the user already exist in
     * the firebase,
     *
     *      if the user already
     *      exist prompt a greeting using the
     *      Utils.Toast method.
     *
     *      Else creates a user
     *      in firebase.
     *
     * Both sets the Activity window to be untouchable
     * to avoid any contact while the communication
     * is running.
     */
    private void checkExistingUser(){
        final DatabaseReference ref = database.getReference();
        firebaseUser = mAuth.getCurrentUser();
        final String userID = firebaseUser.getUid();
        ref.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // if user exist in database
                    Utils.createToast(MainActivity.this, getString(R.string.greeting_txt)+ firebaseUser.getDisplayName());
                }
                else {
                    // if user do not exist in database
                    String email = firebaseUser.getEmail();
                    if(email == null){
                        email = String.valueOf(database.getReference().push().getKey());
                    }
                    String displayName = firebaseUser.getDisplayName();
                    if(displayName == null){
                        displayName = edtUsernameRegister.getText().toString();
                    }
                    String urlImage;
                    if(firebaseUser.getPhotoUrl() == null){
                        urlImage = Utils.DEFAULT_AVATAR_URL;
                    }else{
                        urlImage = firebaseUser.getPhotoUrl().toString();
                    }
                    UserModel userModel = new UserModel(displayName, email,0,1,urlImage);
                    ref.child(Utils.USER_FIREBASE_CONST).child(userID).setValue(userModel);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Utils.setActivityTouchable(MainActivity.this, true);
            }
        });
    }

        callbackManager = CallbackManager.Factory.create();

        llSignin.setOnClickListener(this);
        llSignup.setOnClickListener(this);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    view.startAnimation(Utils.getBtnFader());
                    if (edtEmailLogin.getText().toString().isEmpty() || edtPasswordLogin.getText().toString().isEmpty()) {
                        Utils.createToast(MainActivity.this, getResources().getString(R.string.empty_fields));
                        return;
                    }
                    ghostProgress.setVisibility(View.VISIBLE);
                Utils.setActivityTouchable(MainActivity.this, false);
                    signInWithEmailAndPass();

            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(Utils.getBtnFader());
                if (usernameFlag && passwordFlag && emailFlag){
                    ghostProgress.setVisibility(View.VISIBLE);
                    Utils.setActivityTouchable(MainActivity.this, false);
                    registerUserWithEmailAndPAss();
                }else{
                    Utils.createToast(MainActivity.this, getResources().getString(R.string.empty_fields));
                }
            }
        });
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                    v.startAnimation(Utils.getBtnFader());
                Utils.setActivityTouchable(MainActivity.this, false);
                    facebookPerformClick.performClick();
                    facebookPerformClick.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            ghostProgress.setVisibility(View.VISIBLE);
                            facebookPerformClick.setEnabled(false);
                            handleFacebookAccessToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public void onError(FacebookException error) {

                        }
                    });

            }
        });
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    v.startAnimation(Utils.getBtnFader());
                Utils.setActivityTouchable(MainActivity.this, false);
                    ghostProgress.setVisibility(View.VISIBLE);
                    signIn();

            }
        });
        tvSignupInvoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignupForm();
            }
        });
        tvSigninInvoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSigninForm();
            }
        });

    }
    /**
     * Setting up all the required variables
     * which will be used within the
     * current Activity.
     */
    protected void setUpVariables(){
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        facebookLogin = (ImageButton) findViewById(R.id.btn_log_facebook);
        facebookPerformClick = (LoginButton)findViewById(R.id.btn_fb_performclick);
        googleLogin = (ImageButton) findViewById(R.id.btn_log_google);
        facebookPerformClick.setReadPermissions("email", "public_profile");

        llSignin = (LinearLayout) findViewById(R.id.llSignin);
        llSignup =(LinearLayout)findViewById(R.id.llSignup);
        tvSignupInvoker = (TextView) findViewById(R.id.tvSignupInvoker);
        tvSigninInvoker = (TextView) findViewById(R.id.tvSigninInvoker);

        btnSignup= (Button) findViewById(R.id.btn_register_llSignup);
        btnSignin= (Button) findViewById(R.id.btn_signin_llSignin);

        llSignup = (LinearLayout) findViewById(R.id.llSignup);
        llSignin = (LinearLayout) findViewById(R.id.llSignin);

        loginAttrLayout = (LinearLayout) llSignin.findViewById(R.id.layout_login);
        registerAttrLayout = (LinearLayout) llSignup.findViewById(R.id.layout_register);

        database = FirebaseDatabase.getInstance();

        edtUsernameRegister = (EditText)llSignup.findViewById(R.id.edt_username_register);
        edtPasswordRegister = (EditText)llSignup.findViewById(R.id.edt_password_register);
        edtEmailRegister = (EditText)llSignup.findViewById(R.id.edt_email_register);

        edtEmailLogin = (EditText)llSignin.findViewById(R.id.edt_email_login);
        edtPasswordLogin = (EditText)llSignin.findViewById(R.id.edt_password_login);

        DBOperations dbo = DBOperations.getInstance(getApplicationContext());
        dbo.getWritableDatabase();
        dbo.close();

        mAuth = FirebaseAuth.getInstance();

        ghostProgress = (LVGhost)findViewById(R.id.progress_ghost_main);
        ghostProgress.setViewColor(Color.WHITE);
        ghostProgress.setHandColor(Color.BLACK);
        ghostProgress.setVisibility(View.INVISIBLE);
        ghostProgress.startAnim();
    }

    /**
     * Method registering new user if the user has
     * choose to register in the application.
     *
     * on Successful registration proceed to
     * next activity, and sets the loading dialog
     * to disappear.
     * Else showing a Toast with failure.
     */
    private void registerUserWithEmailAndPAss(){

        (mAuth.createUserWithEmailAndPassword(edtEmailRegister.getText().toString(), edtPasswordRegister.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            checkExistingUser();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            ghostProgress.setVisibility(View.GONE);
                            startActivity(intent);
                        }else{
                            Utils.setActivityTouchable(MainActivity.this, true);
                            Toast.makeText(MainActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                            ghostProgress.setVisibility(View.GONE);
                        }
                    }
                });

    }

    /**
     * Method signing in already registered user
     *
     * on Successful sign in proceed to next
     * activity, and sets the loading dialog
     * to disappear.
     * Else showing a Toast with information.
     * That the user does not exist.
     */
    private void signInWithEmailAndPass() {

        (mAuth.signInWithEmailAndPassword(edtEmailLogin.getText().toString(), edtPasswordLogin.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            ghostProgress.setVisibility(View.GONE);
                            startActivity(intent);
                        }else{
                            Utils.setActivityTouchable(MainActivity.this, true);
                            ghostProgress.setVisibility(View.GONE);
                            Utils.createToast(MainActivity.this,getResources().getString(R.string.invalid_mail_or_pass));
                        }
                    }
                });
    }

    /**
     * Method to switch between Register(Sign up)
     * and Login (Sign In) forms.
     *
     * Handled from the "Invoker" buttons.
     */
    private void showSignupForm() {
        PercentRelativeLayout.LayoutParams paramsLogin = (PercentRelativeLayout.LayoutParams) llSignin.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoLogin = paramsLogin.getPercentLayoutInfo();
        infoLogin.widthPercent = 0.15f;
        llSignin.requestLayout();

        PercentRelativeLayout.LayoutParams paramsSignup = (PercentRelativeLayout.LayoutParams) llSignup.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoSignup = paramsSignup.getPercentLayoutInfo();
        infoSignup.widthPercent = 0.85f;
        llSignup.requestLayout();

        tvSignupInvoker.setVisibility(View.GONE);
        tvSigninInvoker.setVisibility(View.VISIBLE);
        Animation translate= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate_right_to_left);
        llSignup.startAnimation(translate);

        Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_left_to_right);
        btnSignup.startAnimation(clockwise);
        registerAttrLayout.startAnimation(clockwise);
    }
    /**
     * Method to switch between Register(Sign up)
     * and Login (Sign In) forms.
     *
     * Handled from the "Invoker" buttons.
     */
    private void showSigninForm() {
        PercentRelativeLayout.LayoutParams paramsLogin = (PercentRelativeLayout.LayoutParams) llSignin.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoLogin = paramsLogin.getPercentLayoutInfo();
        infoLogin.widthPercent = 0.85f;
        llSignin.requestLayout();

        PercentRelativeLayout.LayoutParams paramsSignup = (PercentRelativeLayout.LayoutParams) llSignup.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoSignup = paramsSignup.getPercentLayoutInfo();
        infoSignup.widthPercent = 0.15f;
        llSignup.requestLayout();

        Animation translate= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate_left_to_right);
        llSignin.startAnimation(translate);
        
        tvSignupInvoker.setVisibility(View.VISIBLE);
        tvSigninInvoker.setVisibility(View.GONE);
        Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_right_to_left);
        btnSignin.startAnimation(clockwise);
        loginAttrLayout.startAnimation(clockwise);
    }

    /**
     * Override onClick and using it in
     * "Invoker" Buttons to handle
     * the keyboard showing.
     *
     * @param v - Required from the override
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.llSignin || v.getId() ==R.id.llSignup){
            InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

    }

    /**
     * Instantiating the Firebase Authentication
     * Listener.
     *
     * Required by firebase.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
            finish();
        }
    }

    /**
     * Handled onBackPressed to show the
     * Utils.ExitDialog.
     */
    @Override
    public void onBackPressed() {
        Utils.showExitAlert(this);
    }

    /**
     * Method required by Google SignIn
     *
     * @param requestCode - Requested code send to
     *                    the Google callbackManager.
     * @param resultCode - Received back the result code
     *                   from Google callbackManager.
     * @param data - Retrieving the data granted from the
     *             sign in intent invoked by the GoogleSignIn
     *             Authentication.
     *             returns : GoogleSignInResult
     *                      - Which makes it possible to
     *                      check if the result is successful.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

        if(requestCode == SIGN_IN){
            GoogleSignInResult res = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(res.isSuccess()){
                GoogleSignInAccount acc = res.getSignInAccount();
                firebaseAuthWithGoogle(acc);
            }else{
                Utils.setActivityTouchable(MainActivity.this, true);
                ghostProgress.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Google API Builder,
     * Required for login with google account.
     */
    private void buildGoogleAPI() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

    }

    /**
     * Firebase Authenticating with Facebook login.
     *
     * If a users logs in with Facebook, to be logged in
     * Firebase as well.
     *
     * @param token
     */
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkExistingUser();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            ghostProgress.setVisibility(View.GONE);
                            finish();
                        } else {
                            Utils.setActivityTouchable(MainActivity.this, true);
                            Toast.makeText(MainActivity.this, "Facebook Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    /**
     * Firebase Authenticating with Google login.
     *
     * If a users logs in with Google, to be logged in
     * Firebase as well.
     *
     * @param acc - The retrieved GoogleSignInAccount
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acc){
        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        checkExistingUser();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        ghostProgress.setVisibility(View.GONE);
                        finish();
                    }
                });
    }

    /**
     * Google signIn Method
     * Required method to login successfully with
     * Google.
     *
     * GoogleSignInApi - Required for throwing an
     * intent to choose which google account the user
     * wants to use.
     *
     */
    private void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,SIGN_IN);
    }

    /**
     * Google Login method when connection fails (required from GoogleLogin)!
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Utils.setActivityTouchable(MainActivity.this, true);
    }

}
