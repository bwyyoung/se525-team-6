package se525.team6;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

public class SMSWipeHelper extends Activity {
   static final int RESULT_ENABLE = 1;

   DevicePolicyManager mDPM;
   ActivityManager mAM;
   ComponentName mDeviceAdminSample;

   public SMSWipeHelper() {
      // TODO Auto-generated constructor stub
   }
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.sms_helper);

       mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
       mAM = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
       mDeviceAdminSample = new ComponentName(SMSWipeHelper.this, DeviceAdminSample.class);
       //put wipe on another thread
       new Thread(doBT_Wipe).start();

   }
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       switch (requestCode) {
           case RESULT_ENABLE:
               if (resultCode == Activity.RESULT_OK) {
                   Log.i("DeviceAdminSample", "Admin enabled!");
               } else {
                   Log.i("DeviceAdminSample", "Admin enable FAILED!");
               }
               return;
       }

       super.onActivityResult(requestCode, resultCode, data);
   }
   
   private Runnable doBT_Wipe = new Runnable() {
      public void run() {
         Wipe();
      }
   }; 
   
   //do the http request here 
   private void Wipe() {
      Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
      intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
            mDeviceAdminSample);
      intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
              "Additional text explaining why this needs to be added.");
      startActivityForResult(intent, RESULT_ENABLE);      
      boolean active = mDPM.isAdminActive(mDeviceAdminSample);
      if (active) {
          mDPM.wipeData(0); //gt002
          mDPM.lockNow();
      }

   }
 
}
