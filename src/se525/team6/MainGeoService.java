package se525.team6;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;

public class MainGeoService extends Service {

   public MainGeoService() {
      // TODO Auto-generated constructor stub
   }
   
   //no binder yet for this
   @Override
   public IBinder onBind(Intent intent) {
      //check binding permissions here
//      int callerOk = myPermissionCheck();
//      switch (callerOk) {
//      case PackageManager.PERMISSION_GRANTED:
//         return this.binder;
//      case PackageManager.PERMISSION_DENIED:
//         return null;
//      }
      
      return null;
    }
   
//   private int myPermissionCheck(){
//      //check the fact it might just be called from the activity in this package
//        if (Binder.getCallingPid() == Process.myPid()) {
//            return PackageManager.PERMISSION_GRANTED;
//        }
//        //can't tell it is me so check this way
//        else {
//         return checkCallingOrSelfPermission("se525.team6.MAINSERVICE");
//        }      
//    }
    
   public void onStart(Intent intent, int startId) {
      super.onStart(intent, startId);

      //create a handler for the updating of the GUI
//       _H = new Handler ();
       //create a thread to do long running process outside of the gui thread
      new Thread(doBackgroundThread).start();
   }

   public void onDestroy() {
      super.onDestroy();
   }
   
   public boolean GeoLocateMe(){
      return true;
   }
   
   private Runnable doBackgroundThread = new Runnable() {
      public void run() {
         GeoLocateMe();
      }
   };
   

}
