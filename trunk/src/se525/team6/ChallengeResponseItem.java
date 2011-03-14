package se525.team6;
/* Class to hold details o a challenge/response pair
 * a. gojdas
*/


//import java.text.SimpleDateFormat;
//import java.util.Date;

public class ChallengeResponseItem {
  String _Challenge;
  String _Response;
  boolean _Used;
  //
  String _SC;
  String _PhoneNum;
  int _TempIdent;
  

  public String get_SmsCommand() {
     return _SC;
  }

  public void set_SmsCommand(String _SC) {
     this._SC = _SC;
  }

  public int get_TempIdent() {
      return _TempIdent;
   }

   public void set_TempIdent(int _TempIdent) {
      this._TempIdent = _TempIdent;
   }

   public String get_PhoneNum() {
      return _PhoneNum;
   }

   public void set_PhoneNum(String _PhoneNum) {
      this._PhoneNum = _PhoneNum;
   }
  
  public String getResponse() {
    return _Response;
  }
  
  public String getChallenge() {
    return _Challenge;    
  }
  
  public boolean getUsed() {
	    return _Used;    
	  }

  public ChallengeResponseItem(String challenge, String response) {
	  this(challenge, response, false);
  }

  public ChallengeResponseItem(String challenge, String response, boolean used) {
	  _Challenge = challenge;
	  _Response = response;
	  _Used = used;
  }

  @Override
  public String toString() {
    return _Challenge +  "\n" + _Response; 
  }
}