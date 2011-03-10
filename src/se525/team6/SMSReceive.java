package se525.team6;

import java.util.ArrayList;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceive extends BroadcastReceiver {
	private StringBuffer _LatestSmsMessageBody;
	private StringBuffer _LatestSmsOriginatingAddress;
   private Context _Context;
   private ArrayList<ChallengeResponseItem> _ChallengeResponseItems;
   private ChallengeResponseDBAdapter _ChallengeResponseDBAdapter;
   private ChallengeResponseItem _ChallengeResponseItem;
   
	public SMSReceive() {
		// TODO Auto-generated constructor stub
		_LatestSmsMessageBody = new StringBuffer();		
		_LatestSmsOriginatingAddress = new StringBuffer();	
      _ChallengeResponseItems = new ArrayList<ChallengeResponseItem>();

	}

	@Override
	public void onReceive(Context context, Intent intent) {
	   _Context = context;
      _ChallengeResponseDBAdapter = new ChallengeResponseDBAdapter(_Context);
      // Open or create the database
      _ChallengeResponseDBAdapter.open();

	   //---get the SmsActivity message passed in---
      Bundle bundle = intent.getExtras();        
      SmsMessage[] msgs = null;
//      String str = "";     
      _LatestSmsMessageBody.setLength(0);
      _LatestSmsOriginatingAddress.setLength(0);
      if (bundle != null)
      {
          //---retrieve the SmsActivity message received---
          Object[] pdus = (Object[]) bundle.get("pdus");
          msgs = new SmsMessage[pdus.length];            
          for (int i=0; i<msgs.length; i++){
              msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
              _LatestSmsOriginatingAddress.append(msgs[i].getOriginatingAddress());
              _LatestSmsMessageBody.append(msgs[i].getMessageBody().toString());
          }
          //---display the new SmsActivity message---
          Toast.makeText(context
        		         , _LatestSmsOriginatingAddress.toString() + " " + _LatestSmsMessageBody.toString()
        		         , Toast.LENGTH_SHORT).show();
          CheckMessageForRemoteControl(_LatestSmsOriginatingAddress.toString(), _LatestSmsMessageBody.toString());
      }  
      
//      http://stackoverflow.com/questions/990217/android-app-with-service-only
//      if(intent.getAction() != null)
//      {
//              if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
//                  intent.getAction().equals(Intent.ACTION_USER_PRESENT))
//              {
//                      context.startService(new Intent(context, WeatherStatusService.class));
//              }
//      }

      
	}

	//good enum example --http://stackoverflow.com/questions/469287/c-vs-java-enum-for-those-new-to-c
//	private enum CommandMode {
//	   STOLEN ("stolen"),
//	   FOUND  ("found");
//
//	   private final String _command;
//	   CommandMode(String command) {
//	       this._command = command;
//	   }
//	   public String command()  { return _command; }
//
//	 }

   private enum SmsCommand {
      stolen
     ,found
     ,wipe
     ,alert
  }

	
	private void CheckMessageForRemoteControl(String phoneNum, String message){
	   SmsCommand sc;
	   String messages[] = message.split(":");
	   //if only array of 1 then it is not a response to a challenge
	   if(messages.length == 1) {
         try {
            sc = SmsCommand.valueOf(message.toLowerCase());
            switch (sc) {
               case stolen:
                  StartChallengeResponse(sc.name(), phoneNum);
                  break;
               case found:
   //               fileType = "text/html; charset=ISO-8859-1";
                  break;
               case wipe:
   //               fileType = "text/vnd.wap.wml";
                  break;
               default: // unknown request, tell them it is bad
   //               fileType = "unknown";
            }
         }
         catch (Exception e) {
            e.printStackTrace();
         }
	   }
	   //response to a challenge
	   else {
//	      _ChallengeResponseItem.get_TempIdent().
//	      messages[0]
	   }
   }
   
	private void StartChallengeResponse(String command, String phoneNum){
//    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"+phoneNum));
//    smsIntent.putExtra("sms_body", "A challenge");
//    smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    _Context.startActivity(smsIntent);
	   GetChallenge(command, phoneNum);
	   SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(_ChallengeResponseItem.get_PhoneNum(), null
                                ,Integer.toString(_ChallengeResponseItem.get_TempIdent()) + ":" + _ChallengeResponseItem.getChallenge(), null, null);
	   
	}
	
	private ChallengeResponseItem GetChallenge(String command, String phoneNum){
	   Random generator = new Random();
	   _ChallengeResponseItem = _ChallengeResponseDBAdapter.getRanChallengeResponseItem();
      _ChallengeResponseItem.set_SmsCommand(command);
	   _ChallengeResponseItem.set_PhoneNum(phoneNum);
	   _ChallengeResponseItem.set_TempIdent(generator.nextInt());
	   _ChallengeResponseItems.add(_ChallengeResponseItem);
	   return _ChallengeResponseItem;
	}
	
	
	
	public String get_LatestSmsMessageBody() {
		return _LatestSmsMessageBody.toString();
	}

}
