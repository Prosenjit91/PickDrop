package com.prosenjit.pickndrop.Application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.prosenjit.pickndrop .Receiver.ConnectivityReceiver;

public class PicknDropApplication extends Application {
	public static final String TAG = PicknDropApplication.class.getSimpleName();
	private static PicknDropApplication mInstance;
	private static Context mContext;
	private Activity mCurrentActivity;
	@Override
	public void onCreate() {
		super.onCreate();
		this.mContext=this;
		mInstance = this;

	}

	public static synchronized PicknDropApplication getInstance() {
		return mInstance;
	}

	public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
		ConnectivityReceiver.connectivityReceiverListener = listener;
	}


	@Override
	public void onTerminate() {
		super.onTerminate();

	}

	public static Context getContext() {
		return mContext;
	}

	public Activity getCurrentActivity() {
		return mCurrentActivity;
	}
	public void setCurrentActivity(Activity activity) {
		mCurrentActivity=activity;
	}
	public static PicknDropApplication getApplication() {
		return mInstance;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);

	}
	public static boolean hasNetwork ()
	{
		return mInstance.checkIfHasNetwork();
	}

	public boolean checkIfHasNetwork()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
}
