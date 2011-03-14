package se525.gt;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

    public class WipeMe extends Service {
    	@Override
		public void onStart(Intent intent, int startId) {
    		super.onStart(intent, startId);
    		Toast.makeText(this, "WipeMe.onStart", Toast.LENGTH_SHORT).show();
    		serviceStarted(); // our own implementation down in this code
    	}
    	
    	Handler h;
    	boolean started = false; // boolean flag to signal stop
    // Need to start a thread or else "nothing else is going to happen". Have to
    // return onStart
    // quickly otherwise the application will get killed
    	private void serviceStarted() {
    		started = true;
    		h = new Handler();
    		Runnable r = new Runnable() {
    			public void run() {
    				if (started) { // check boolean flag that signals stop
    					Toast.makeText(WipeMe.this, "WipeMe says ping",
    							Toast.LENGTH_SHORT).show();
    					h.postDelayed(this, 5000);
    				} else {
    					Toast.makeText(WipeMe.this,
    							"WipeMe says stopped, no more pings",
    							Toast.LENGTH_SHORT).show();
    					// do not re "postDelayed" in effect stopping the service
    				}
    			}
    		};
    		h.postDelayed(r, 5000);
    	}
    	
    	static final int RESULT_ENABLE = 1;

        DevicePolicyManager mDPM;
        ActivityManager mAM;
        ComponentName mWipeMe;

            mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
            mAM = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    mWipeMe);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Additional text explaining why this needs to be added.");
            startActivityForResult(intent, RESULT_ENABLE);      
            boolean active = mDPM.isAdminActive(mDeviceAdminSample);
            if (active) {
                mDPM.wipeData(0); //gt002
                mDPM.lockNow();
            }
        }

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case RESULT_ENABLE:
                    if (resultCode == Activity.RESULT_OK) {
                        Log.i("WipeMe", "Admin enabled!");
                    } else {
                        Log.i("WipeMe", "Admin enable FAILED!");
                    }
                    return;
            }
        }

		private final WipeMe.Stub binder = new WipeMe.Stub() {
		// wipe
			public wipe() {
				return true;
			}
		};
		
		// Toast WipeMe.onBind
		@Override
		public IBinder onBind(Intent intent) {
			Toast.makeText(this, "WipeMe.onBind", Toast.LENGTH_SHORT).show();
			serviceStarted();
			return this.binder;
		}
    };