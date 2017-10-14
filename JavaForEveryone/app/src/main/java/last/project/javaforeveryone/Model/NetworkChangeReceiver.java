package last.project.javaforeveryone.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import last.project.javaforeveryone.utility.SplashScreen;

/**
 * Created by plame_000 on 24-Oct-17.
 */

public class NetworkChangeReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();

        if (activeNetwork == null) {
            Intent intent2 = new Intent(context, SplashScreen.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }
    }

}
