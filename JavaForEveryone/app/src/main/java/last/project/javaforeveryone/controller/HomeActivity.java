package last.project.javaforeveryone.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import last.project.javaforeveryone.R;
import last.project.javaforeveryone.fragment.AchievementFragment;
import last.project.javaforeveryone.fragment.ExamFragment;
import last.project.javaforeveryone.fragment.RankListFragment;
import last.project.javaforeveryone.iface.IFragmentChangeListener;
import last.project.javaforeveryone.fragment.StageFragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import last.project.javaforeveryone.fragment.UserProfileFragment;
import last.project.javaforeveryone.model.UserModel;
import last.project.javaforeveryone.utility.Utils;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IFragmentChangeListener {

    private static final int CHANGE_PROFILE = 50;

    private FirebaseDatabase firebaseDB;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference dbRef;
    private String userID, appBarTitle;

    private ImageButton btnStageIntro, btnStageOop, btnStageCollections, btnStageIteration, btnStageInner, btnFinalTest;
    private Fragment currentFragment;
    private FragmentManager fragmentManager;

    private NavigationView navigationView;
    private View navHeaderView;
    private TextView emailNavDrawer, usernameNavDrawer, pointsNavDrawer;
    private ImageView avatarNavDrawer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggler;
    private LinearLayout lastTestll;

    private UserModel currentUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_home);
        if (currentUserModel == null){
            Utils.setActivityTouchable(HomeActivity.this, false);
        }else {
            Utils.setActivityTouchable(HomeActivity.this, true);
        }
        setUpVariables();

        setUpUserInfo(firebaseUser);

        setUpButtons();

    }

    /**
     * Override required so the
     * ActionBar now displays
     * adequate text.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserModel == null){
            Utils.setActivityTouchable(HomeActivity.this, false);
        }else {
            Utils.setActivityTouchable(HomeActivity.this, true);
        }
        appBarTitle = getResources().getString(R.string.home_txt);
    }

    /**
     * Setting up all the required variables
     * which will be used within the
     * current Activity.
     */
    protected void setUpVariables() {
        appBarTitle = getResources().getString(R.string.home_txt);
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.home_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.home_layout);
        navHeaderView = navigationView.getHeaderView(0);
        emailNavDrawer = (TextView) navHeaderView.findViewById(R.id.nav_head_mail);
        usernameNavDrawer = (TextView) navHeaderView.findViewById(R.id.nav_head_username);
        avatarNavDrawer = (ImageView) navHeaderView.findViewById(R.id.nav_head_img);
        pointsNavDrawer = (TextView) navHeaderView.findViewById(R.id.nav_head_points);

        btnStageIntro = (ImageButton) findViewById(R.id.btn_img_intro_home);
        btnStageOop = (ImageButton) findViewById(R.id.btn_oop_home);
        btnStageCollections = (ImageButton) findViewById(R.id.btn_collections_home);
        btnStageIteration = (ImageButton) findViewById(R.id.btn_iteration_home);
        btnStageInner = (ImageButton) findViewById(R.id.btn_inner_classes_home);
        btnFinalTest = (ImageButton) findViewById(R.id.btn_last_test_home);
        lastTestll = (LinearLayout)findViewById(R.id.last_test_ll);

        userID = firebaseUser.getUid();
        dbRef = firebaseDB.getReference();

        toggler = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_str, R.string.close_str);
        drawerLayout.addDrawerListener(toggler);
        toggler.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(appBarTitle);
        }
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
    }

    /**
     * Method to control the button background color
     * As follows -
     *          if user has already passed concrete
     *          stage the button background must be
     *          switched to green color.
     *            -
     *          if user has passed all the stages
     *          the visibility of the final/last
     *          exam button must be changed to
     *          VISIBLE.
     *
     * Required -
     *          The user's current stage. Taken from
     *          the currentUser obj.
     *
     * Background patterns can be found in R.drawable
     * in .xml format.
     */
    private void stageButtonVisibility(){
        int stageIndex = currentUserModel.getStagesID();
        if (stageIndex >= 2){
            btnStageIntro.setBackground(getResources().getDrawable(R.drawable.button_background_shape_circle_green));
        }
        if (stageIndex >= 3){
            btnStageOop.setBackground(getResources().getDrawable(R.drawable.button_background_shape_square_green));
        }
        if (stageIndex >= 4){
            btnStageCollections.setBackground(getResources().getDrawable(R.drawable.button_background_shape_square_green));
        }
        if (stageIndex >= 5){
            btnStageIteration.setBackground(getResources().getDrawable(R.drawable.button_background_shape_circle_green));
        }
        if (stageIndex >= 6){
            btnStageInner.setBackground(getResources().getDrawable(R.drawable.button_background_shape_circle_green));
            lastTestll.setVisibility(View.VISIBLE);
        }


    }

    /**
     * Functions which sets up all the stage buttons
     * in the activity.
     *
     * Each button switches to one and the same fragment
     * with different arguments - Check method (switchToFragment(...))
     *
     * Each button switches the appBarTitle
     * depending on which button is clicked, so the
     * adequate toolbar title be shown.
     * Also appBarTitle used as an argument for the
     * switchToFragment method. Showing the fragment
     * which data must be shown.
     */
    private void setUpButtons() {

        btnStageIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarTitle = getResources().getString(R.string.stages_intro_txt);
                switchToFragment(appBarTitle);
            }
        });
        btnStageOop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarTitle = getResources().getString(R.string.stages_oop_txt);
                if (currentUserModel.getStagesID() >= 2) {
                    switchToFragment(appBarTitle);
                }else{
                    showDenyDialog();
                }
            }
        });
        btnStageCollections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarTitle = getResources().getString(R.string.stages_collections_txt);
                if (currentUserModel.getStagesID() >= 3) {
                    switchToFragment(appBarTitle);
                }else{
                    showDenyDialog();
                }
            }
        });
        btnStageIteration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarTitle = getResources().getString(R.string.stages_iterations_txt);
                if (currentUserModel.getStagesID() >= 4) {
                    switchToFragment(appBarTitle);
                }else{
                    showDenyDialog();
                }
            }
        });
        btnStageInner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarTitle = getResources().getString(R.string.stages_inner_classes_txt);
                if (currentUserModel.getStagesID() >= 5) {
                    switchToFragment(appBarTitle);
                }else{
                    showDenyDialog();
                }
            }
        });
        btnFinalTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appBarTitle = getString(R.string.last_test_txt);
                    switchToFragment(appBarTitle);
                }
        });

    }

    /**
     * Function to switch between different fragments
     * used to show concrete fragment using a title as
     * an argument.
     *
     * The title is send to the called fragment and
     * handled there to show specific data depending on
     * the given param.
     *
     * @param title - Showing what data the fragment must show
     *              handled in the fragment itself.
     */
    private void switchToFragment(String title) {
        getSupportActionBar().setTitle(title);
        Bundle fragBundle = new Bundle();
        fragBundle.putCharSequence("title", title);
        fragBundle.putInt("index", 0);
        fragBundle.putSerializable("currentUser", currentUserModel);
        if(title.equals(getString(R.string.last_test_txt))){
            currentFragment = new ExamFragment();
            currentFragment.setArguments(fragBundle);
        }else{
            currentFragment = new StageFragment().newInstance(fragBundle);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_place, currentFragment)
                .commit();
    }

    /**
     * Required override to show the adequate
     * toolbar title if the screen is rotated.
     * @param outState
     * @param outPersistentState
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Utils.setActivityTouchable(HomeActivity.this, true);
        outState.putString("appBar",appBarTitle);
    }

    /**
     * Required override to close
     * the navigation drawer if opened as first option.
     *
     * Disabling the back press if the user is currently
     * in the Last Exam.
     *
     * And Finally showing the ExitDialog if the user
     * is anywhere else.
     *
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        if (currentFragment instanceof ExamFragment){
            return;
        }

        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.transparent_primary)
                .setIcon(R.drawable.ic_ghost_exit)
                .setButtonsColorRes(R.color.colorPrimaryDark)
                .setTitle("А сега на къде?!?")
                .setTitleGravity(Gravity.CENTER)
                .setMessage("Нали не ни напускаш???")
                .setMessageGravity(Gravity.CENTER)
                .setPositiveButton(getResources().getString(R.string.log_out), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.home_txt), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getSupportFragmentManager().findFragmentById(R.id.fragment_place) != null) {
                            getSupportActionBar().setTitle(R.string.home_txt);
                            fragmentManager.beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_place))
                                    .commit();
                        }
                        return;
                    }
                })
                .show();
    }

    /**
     * NavigationView method to handle the user
     * command and show specific info by click
     *
     * Used final params for a better code reading.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int logout = R.id.nav_logout;
        final int profile = R.id.nav_profile;
        final int home = R.id.nav_home;
        final int achievements = R.id.nav_achievement;
        final int inviteFriend = R.id.nav_friend;
        final int rankList = R.id.nav_rank;

        switch (item.getItemId()) {
            case logout:
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case profile:
                getSupportActionBar().setTitle(R.string.profile_txt);
                currentFragment = new UserProfileFragment();
                Bundle bundle = new Bundle();
                String type = checkUserLogin(firebaseUser);
                bundle.putString("userType", type);
                bundle.putInt("userPts", currentUserModel.getAchPts());
                bundle.putInt("userStage", currentUserModel.getStagesID());
                bundle.putString("userImage", currentUserModel.getImage());
                bundle.putString("username", currentUserModel.getName());
                currentFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_place, currentFragment)
                        .commit();

                drawerLayout.closeDrawers();
                break;
            case home:
                getSupportActionBar().setTitle(R.string.home_txt);
                if (getSupportFragmentManager().findFragmentById(R.id.fragment_place) != null) {
                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_place))
                            .commit();
                }
                drawerLayout.closeDrawers();
                break;
            case achievements:
                getSupportActionBar().setTitle(R.string.achievements_txt);
                Bundle achBundle = new Bundle();
                achBundle.putSerializable("user", currentUserModel);
                currentFragment = new AchievementFragment();
                currentFragment.setArguments(achBundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_place, currentFragment)
                        .commit();
                drawerLayout.closeDrawers();
                break;
            case inviteFriend:

                drawerLayout.closeDrawers();

                String appLinkUrl, previewImageUrl;

                appLinkUrl = Utils.APP_LINK_URL;
                previewImageUrl = Utils.FB_INVITE_IMAGE_URL;

                if (AppInviteDialog.canShow()) {
                    AppInviteContent content = new AppInviteContent.Builder()
                            .setApplinkUrl(appLinkUrl)
                            .setPreviewImageUrl(previewImageUrl)
                            .build();
                    AppInviteDialog.show(this, content);
                }
                break;
            case rankList:
                getSupportActionBar().setTitle(R.string.rankList);
                Bundle rankBundle = new Bundle();
                rankBundle.putSerializable("user", currentUserModel);
                currentFragment = new RankListFragment();
                currentFragment.setArguments(rankBundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_place, currentFragment)
                        .commit();
                drawerLayout.closeDrawers();
                break;
        }

        return false;
    }


    /**
     * NavDrawer required method
     * depends on option selected.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggler.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks from where does the logged in user comes
     *      - Params can be
     *          : Facebook
     *          : Google
     *          : JavaForEveryone custom registered user.
     *
     * Required to handle the user edit profile correctly.
     * as well as user's profile picture.
     *
     * @param user - Required FirebaseUser to checks
     *             the providerData from the Authentication.
     * @return - String showing from where the user comes
     *          handled with string for better understanding.
     */
    private String checkUserLogin(FirebaseUser user) {
        int finalInfo = -1;
        for (UserInfo info : user.getProviderData()) {
            if (info.getProviderId().equals("facebook.com")) {
                finalInfo = 0;
                break;
            } else if (info.getProviderId().equals("google.com")) {
                finalInfo = 1;
                break;
            } else {
                finalInfo = 2;
            }
        }

        if (finalInfo == 0) {
            return "facebook";
        } else if (finalInfo == 1) {
            return "google";
        } else {
            return "mail";
        }
    }

    /**
     * Sets up the user information in the Navigation Drawer,
     * as his photo, username, mail etc.
     *
     * Depends on user Provider, handled in method
     * (checkUserLogin)
     *
     * @param user - Argument to send in method
     *             checkUserLogin.
     */
    private void setUpUserInfo(FirebaseUser user) {
        String userProvider = checkUserLogin(user);

        if (userProvider.equalsIgnoreCase("facebook")) {
            getCurrentUser();
            String facebookUserId = "";
            facebookUserId = Profile.getCurrentProfile().getId();
            String photoURL = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";
            Bitmap bitmap = Utils.getUserImageFrom(photoURL, this);

            RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            round.setCircular(true);
            avatarNavDrawer.setImageDrawable(round);

            return;
        }
        if (userProvider.equalsIgnoreCase("google")) {
            getCurrentUser();
            Bitmap bitmap = Utils.getUserImageFrom(user.getPhotoUrl().toString(), this);

            RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            round.setCircular(true);
            avatarNavDrawer.setImageDrawable(round);
            return;
        }

        getCurrentUser();

    }

    /**
     * Gets the current user from the firebase
     *
     * Checking if the currentUser is null and if
     * it is the Activity is recreated to avoid
     * wrong information placed in NavDrawer
     * and button's bacground.
     *
     * Usually recreating because of slow network.
     */
    private void getCurrentUser() {
        dbRef.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentUserModel = dataSnapshot.getValue(UserModel.class);
                if (currentUserModel == null) {
                    recreate();
                } else {
                    usernameNavDrawer.setText(currentUserModel.getName());
                    if(Utils.isValidEmail(currentUserModel.getEmail())){
                        emailNavDrawer.setText(getResources().getString(R.string.unverified_mail));
                    }else {
                        emailNavDrawer.setText(currentUserModel.getEmail());
                    }
                    pointsNavDrawer.setText(String.valueOf(currentUserModel.getAchPts()));
                    if (checkUserLogin(firebaseUser).equals("mail")) {
                        StorageReference gsReference = FirebaseStorage.getInstance().getReference();
                        gsReference.child(currentUserModel.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Bitmap bitmap = Utils.getUserImageFrom(uri.toString(), HomeActivity.this);

                                RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                round.setCircular(true);
                                avatarNavDrawer.setImageDrawable(round);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(HomeActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Utils.setActivityTouchable(HomeActivity.this, true);
                    stageButtonVisibility();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHANGE_PROFILE) {
            if (resultCode == RESULT_CANCELED) {
                Utils.createToast(this,getString(R.string.cancel_change_profile));
            } else if (resultCode == RESULT_OK) {
                recreate();
            }
        }

    }

    /**
     * A Dialog showing the user has not reached
     * the stage yet, and must finish the previous
     * stage/s first.
     */
    private void showDenyDialog(){
        new LovelyStandardDialog(HomeActivity.this)
                .setTopColorRes(R.color.colorRed)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_ghost_exit)
                .setTitle(R.string.deny_dialog_not_ready_yet)
                .setTitleGravity(Gravity.CENTER)
                .setMessage(R.string.deny_dialog_previous_stage)
                .setNeutralButton(R.string.ok_txt, null)
                .show();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

}

