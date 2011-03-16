package se525.team6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.widget.Toast;
import android.os.Process;

public class MainSmsService extends Service {
	protected int _ToastLength = Toast.LENGTH_SHORT;
	protected String _ClassName = this.getClass().getName();
    private StringBuffer _Hdr = new StringBuffer("");
    private StringBuffer _Err = new StringBuffer("");
    private Handler _H;
	private String _Key = "d7d373be095b26ccc6b6cb1d910d631a";
    private StringBuffer _CallHist = new StringBuffer("");
	
    
    private int myPermissionCheck(){
    	//check the fact it might just be called from the activity in this package
        if (Binder.getCallingPid() == Process.myPid()) {
            return PackageManager.PERMISSION_GRANTED;
        }
        //can't tell it is me so check this way
        else {
    		return checkCallingOrSelfPermission("se525.team6.MAINSERVICE");
        }      
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		//check binding permissions here
		int callerOk = myPermissionCheck();
		switch (callerOk) {
		case PackageManager.PERMISSION_GRANTED:
			return this.binder;
//			break;
		case PackageManager.PERMISSION_DENIED:
			return null;
//			break;
		}
		
		return null;
	}

	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		//create a handler for the updating of the GUI
	    _H = new Handler ();
	    
	   String actionToTake = intent.getStringExtra("ActionToTake");
	    //create a thread to do long running process outside of the gui thread
	   if(actionToTake.equals("Wipe")){
	      new Thread(doBackgroundThreadWipe).start();
	   }
	   else if (actionToTake.equals("GeoLocate")){
         new Thread(doBackgroundThreadGeo).start();	      
	   }
	}

	public void onDestroy() {
		super.onDestroy();
	}

   public void Wipe() {
      new Thread(doBackgroundThreadWipe).start();
   }
	
	private Runnable doBackgroundThreadWipe = new Runnable() {
		public void run() {
			MakeRequest("Starting Wipe");
			
	      Intent intent = new Intent(getBaseContext(), SMSWipeHelper.class);
	      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	      getApplication().startActivity(intent);

		}
	};

   private Runnable doBackgroundThreadGeo = new Runnable() {
      public void run() {
         MakeRequest("Starting GeoLocate");
         //create a thread to do long running process outside of the gui thread
         //we have it in a timed loop so it just keeps doing it
         try {
            while (true) {
               GeoLocate();
//               new Thread(doBackgroundThread).start();
               Thread.sleep(60 * 10 * 1000); //10 minutes
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

      }
   };

   public void GeoLocate() {
      LocationManager locationManager;
      String context = Context.LOCATION_SERVICE;
      locationManager = (LocationManager)getSystemService(context);
      Criteria criteria = new Criteria();
      criteria.setAccuracy(Criteria.ACCURACY_FINE);
      criteria.setAltitudeRequired(false);
      criteria.setBearingRequired(false);
      criteria.setCostAllowed(true);
      criteria.setPowerRequirement(Criteria.POWER_LOW);
      String provider = locationManager.getBestProvider(criteria, true);
      Location location = locationManager.getLastKnownLocation(provider);
//      Double geoLat = location.getLatitude()*1E6;
//      Double geoLng = location.getLongitude()*1E6;
      double lat = location.getLatitude();
      double lng = location.getLongitude();
      String latLongString;
      latLongString = "Lat:" + lat + "\nLong:" + lng;

      MakeRequest(latLongString);
   }

   
	//do the http request here	
	private void MakeRequest(String commandProcessing) {
      String user = "Atester";

//      StringBuffer uri = new StringBuffer("http://se525adg.appspot.com/notes/" + user);
      StringBuffer uri = new StringBuffer("http://10.0.2.2:8888/notes/Adam");//"http://10.0.2.2:8888/notes/Adam?backdoor"
      
      //only attempt if there is some string in there. and starts with http://
      if(commandProcessing.length()>0){        
         try {
              DefaultHttpClient httpclient = new DefaultHttpClient ();
              HttpPost request = new HttpPost(uri.toString());
              String testUri = request.getURI().getPath();
//            HttpGet request = new HttpGet(uri.toString());
              HttpResponse response = 
                httpclient.execute (request, new BasicHttpContext ());
              
              HttpEntity entity = response.getEntity ();
              _Hdr.append(response.getStatusLine ());
              for (Header header : response.getAllHeaders ()){
               _Hdr.append("\n" + header);                       
              }
      
              InputStream is = entity.getContent ();

              if(response.containsHeader("WWW-Authenticate")){
               final String macAlg = "HMACSHA1";
               // use key pick per user 
               SecretKey secKey = stringToKey(_Key);

               StringBuffer content_uri = new StringBuffer();
               content_uri.append(testUri);
               String encryptedCommand = CryptoTeam6.Encrypt(commandProcessing);
               content_uri.append(encryptedCommand);

               // Create MAC for plaintext.
               Mac macC = Mac.getInstance (macAlg);
               macC.init (secKey);
               byte[] signature = macC.doFinal (content_uri.toString().getBytes());


               request = new HttpPost(uri.toString());

                 String sendReq = Base64.encodeToString(signature, Base64.NO_WRAP);
                 // List with arameters and their values
                 List<NameValuePair> nameValuePairs;
                 nameValuePairs = new ArrayList<NameValuePair>(1);
                 nameValuePairs.add(new BasicNameValuePair( "content", encryptedCommand));
                 
                 request.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
//               request.addHeader("Authorization", "se525 QWRnIG5ldyB0ZXN0IGhlYWRlcg==");
                 request.addHeader("Authorization", "se525 " + sendReq);
                 response = 
                      httpclient.execute (request, new BasicHttpContext ());
                    
                entity = response.getEntity ();

              }
              
         } catch (IllegalStateException e) {
            e.printStackTrace();
            _Err.append("\n" + "IllegalStateException - " + e.toString());
         } catch (IOException e) {
            e.printStackTrace();
            _Err.append("\n" + "IOException - " + e.toString());

         }
         //use the handler from the gui thread to do updates to the gui
//       _H.post(doUpdateGUI);
         catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

	}
   //generate the secret key by calling this method with a string
   private SecretKey stringToKey(String secKeyString) {
      final String cryptoAlg = "AES";
      byte[] secKeyData = new byte[secKeyString.length() / 2];
      for (int i = 0; i < secKeyData.length; i++) {
         secKeyData[i] = (byte) Integer.parseInt(
               secKeyString.substring(2 * i, 2 * i + 1), 16);
      }
      SecretKeySpec secKeySpec = new SecretKeySpec(secKeyData, cryptoAlg);
      return secKeySpec;
   }

	//can't use this here because broadcast service is limited in its binding ability
	private final IMainSMSService.Stub binder = new IMainSMSService.Stub() {
		public boolean WipeIt() {
		   Wipe();
			return true;
		}
		
		public boolean GeoLocateLooper() {
			return true;
		}

		private void RecordCallHist() {
			Date anotherCurDate = new Date();  
			SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");  
			String formattedDateString = formatter.format(anotherCurDate);  
			
			_CallHist.append("CallingPid = " + getCallingPid());
			_CallHist.append(" @ " + formattedDateString + "\n");			
		}
	};

	
	
	
	
}
