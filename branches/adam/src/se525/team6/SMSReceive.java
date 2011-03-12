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
            boolean WillChallenge = false;
            sc = SmsCommand.valueOf(message.toLowerCase());
            switch (sc) {
               case stolen:
               case found:
               case wipe:

                  WillChallenge = true;
                  break;
               default: // unknown request, tell them it is bad
                  WillChallenge = false;
            }
            if(WillChallenge){
               StartChallengeResponse(sc.name(), phoneNum);
            }
         }
         catch (Exception e) {
            e.printStackTrace();
         }
	   }
	   //response to a challenge
	   else if (messages.length == 2) {      
	      _ChallengeResponseItem = _ChallengeResponseDBAdapter.getInUseChallengeResponseItem(Integer.parseInt(messages[0]));
	      if (_ChallengeResponseItem != null &&
	          phoneNum.equals(_ChallengeResponseItem.get_PhoneNum()) &&
	          messages[1].equals(_ChallengeResponseItem.getResponse())) {
   	         try {
   	            sc = SmsCommand.valueOf(_ChallengeResponseItem.get_SmsCommand().toLowerCase());
   	            switch (sc) {
   	               case stolen:
   	                  //create the intent for this
   	                  Toast.makeText(_Context
   	                        , _LatestSmsOriginatingAddress.toString() + " " + _LatestSmsMessageBody.toString() + " " + sc.name()
   	                        , Toast.LENGTH_LONG).show();
                        break;
   	               case found:
   	                  
                        break;
   	               case wipe:
   
   	                  break;
   	               default: // unknown request, tell them it is bad
   	                  
                        break;
   	            }
   	         }
   	         catch (Exception e) {
   	            e.printStackTrace();
   	         }
	      }
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
      //enter this in the DB
      _ChallengeResponseDBAdapter.updateChallengeInUse(_ChallengeResponseItem.getChallenge(), _ChallengeResponseItem.get_SmsCommand(), _ChallengeResponseItem.get_PhoneNum(), _ChallengeResponseItem.get_TempIdent());
	   
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
