package last.project.javaforeveryone.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ldoublem.loadingviewlib.view.LVGhost;

import last.project.javaforeveryone.R;
import last.project.javaforeveryone.controller.MainActivity;

public class SplashScreen extends AppCompatActivity {

    private LVGhost ghostProgress;
    private ConnectivityManager connMgr;
    private android.net.NetworkInfo activeNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new PrefetchData().execute();
    }

    @Override
    public void onBackPressed() {
        Utils.showExitAlert(this);
    }

    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ghostProgress = (LVGhost)findViewById(R.id.progress_ghost_main);
            ghostProgress.setViewColor(Color.WHITE);
            ghostProgress.setHandColor(Color.BLACK);
            ghostProgress.setVisibility(View.VISIBLE);
            ghostProgress.startAnim();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            while(true) {
                activeNetwork = connMgr.getActiveNetworkInfo();
                if(activeNetwork != null){
                    break;
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ghostProgress.stopAnim();
            Intent intent = new Intent(SplashScreen.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
