package last.project.javaforeveryone.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import last.project.javaforeveryone.R;

public final class Utils {

    public static final String USER_FIREBASE_CONST = "users";
    public static final String DEFAULT_AVATAR_URL = "images/lakfh03rh09w19hnq18hc01cj109wkd`mcm1290vm90_1.png";
    public static final String APP_LINK_URL = "https://fb.me/139078980150790";
    public static final String FB_INVITE_IMAGE_URL = "https://i.ytimg.com/vi/Fu8T-eUSrBc/hqdefault.jpg";

    private static final float FADE_AMMOUNT = 0.6f;
    private static final String PASS_REGEX = "^[a-zA-Z0-9]*$";
    private static AlphaAnimation btnFader;
    private static FirebaseDatabase database;
    private static String resultUrl;
    private static MediaPlayer mp;
    private static Toast toast;

    /**
     * A private constructor to ensure no one can
     * actually create an object from Utils class.
     */
    private Utils() {

    }


    /**
     * Fades the button with the constant FADE_AMMOUNT
     * use as follows : view.StartAnimation(Utils.getBtnFader)
     * @return the AlphaAnimation with the constant ammount
     */
    public static AlphaAnimation getBtnFader() {
        btnFader = new AlphaAnimation(1F, FADE_AMMOUNT);
        return btnFader;
    }

    /**
     * Checks if the user's username matches our
     * terms of use.
     * @param username - given username to check
     * @return boolean - is valid or is invalid
     */
    public static boolean isValidUsername(String username) {
        return (username == null || username.isEmpty() || (username.length() < 3 || username.length() == 0));
    }

    /**
     * Checks if the user's password matches regex - (PASS_REGEX)
     * and is not empty or small - (Required 6 symbols for firebase control)
     * @param password - given password to check
     * @return boolean - is valid or is invalid
     */
    public static boolean isValidPassword(String password) {
        return (password.isEmpty() || !password.matches(Utils.PASS_REGEX) || password.length() < 6);
    }

    /**
     * Checks if the user's email matches the android regex
     * patter for email.
     * @param email - given email to check
     * @return boolean - is valid or is invalid
     */
    public static boolean isValidEmail(String email) {
        return (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    /**
     * Showing dialog to exit the application
     * use - implemented in all activities.
     * @param act - the concrete activity from which the
     *            exit dialog must be shown.
     */
    public static void showExitAlert(final Activity act) {
        new LovelyStandardDialog(act)
                .setTopColorRes(R.color.colorRed)
                .setButtonsColorRes(R.color.authui_colorAccent)
                .setIcon(R.drawable.ic_ghost_exit)
                .setTitle("А сега на къде?!?")
                .setTitleGravity(Gravity.CENTER)
                .setMessage("Нали не ни напускаш???")
                .setPositiveButton(R.string.possitive_txt, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act.moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .setNegativeButton(R.string.negative_txt, null)
                .show();
    }

    /**
     * Downloads the user's Image from a specific URL
     * runs in a different thread by calling the
     * Utils method - doInBackground with a command
     * "download" as first argument.
     *
     * @param photoURL - the User's avatar URL
     * @param ctx - Context from which the method is called
     *            required in AsyncTask #doInBackground
     * @return
     */
    public static Bitmap getUserImageFrom(String photoURL, Context ctx) {
        doInBackground task = new doInBackground(ctx);
        Bitmap userPhoto = null;
        try {
            userPhoto = (Bitmap) task.execute("download", photoURL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return userPhoto;
    }

    /**
     * Class extending AsyncTask used to do specific
     * functions in a separate thread. Requires a Context
     * to start a ProgressDialog.
     *
     * The params are structured as follows
     * first element of the params - must always be the key showing
     * what the thread must do .
     *      ex.: params[0] = download
     *      - Downloads a Bitmap from a specific URL.
     *      - the second param must be the URL.
     *
     */
    private static class doInBackground extends AsyncTask<Object, Void, Object> {

        private Context context;
        private ProgressDialog mProgressDialog;

        private doInBackground(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();

        }

        @Override
        protected Object doInBackground(Object... params) {

            String key = params[0].toString();

            if (key.equals("download")) {
                try {
                    InputStream input = new java.net.URL(params[1].toString()).openStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (IOException e) {
                    return null;
                }

            }
            if (key.equals("wait")) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "asd";
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mProgressDialog.dismiss();
        }

    }

    /**
     * Method to play a sound with a specific Context
     * and by a given sound resource ID.
     * @param ctx - The concrete Context from which the function is called.
     * @param id - A specific sound resource id to play.
     */
    public static void playSound(Context ctx,int id){
        mp = MediaPlayer.create(ctx, id);
        mp.start();
    }

    /**
     * Creates a custom Toast showing in given Activity
     *
     * Required:
     * @param act -
     *            Specific Activity
     *            where to show the Toast.
     * @param imgID -
     *              Specific Image resource
     *              taken as an Icon for the
     *              Toast.
     * @param txt -
     *            A text to display withing
     *            the Toast.
     */
    public static void createToast(Activity act, int imgID, String txt){
        LayoutInflater inflater = act.getLayoutInflater();
        View layoutView = inflater.inflate(R.layout.toast_view, (ViewGroup) act.findViewById(R.id.toastlayout));
        ImageView img = (ImageView) layoutView.findViewById(R.id.img_toast);
        TextView txtView = (TextView) layoutView.findViewById(R.id.text_toast);

        if (imgID == -1){
            img.setVisibility(View.GONE);
        }else{
            img.setImageResource(imgID);
        }

        txtView.setText(txt);

        toast = new Toast(act.getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layoutView);
        toast.setGravity(Gravity.CENTER, 0,0);
        toast.show();
    }

    /**
     * Overloading the existing Custom Toast Method
     * but without an image.
     * Image is handled in the previous method.
     *
     * the given params are required by the overloaded
     * method.
     */
    public static void createToast(Activity act, String txt){
       Utils.createToast(act, -1, txt);
    }

    /**
     * Sets the given activity to be touchable or not.
     *
     * Required for the delay of the communication between
     * firebase and the application.
     *
     * Also if login/register button is clicked
     * disallowing the display clicks until the communication
     * is complete.
     *
     * @param act - Which activity clicks should be disabled.
     * @param option - Argument for option if the display should
     *               be set on enabled or disabled clicks.
     */
    public static void setActivityTouchable(Activity act, Boolean option){
        if (option){
            act.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else{
            act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
