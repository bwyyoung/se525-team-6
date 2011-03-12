package se525.team6;

//import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class ChallengeResponseDBAdapter {
  private static final String DATABASE_NAME = "ChallengeResponseList.db";
  private static final String DATABASE_TABLE = "ChallengeResponseItems";
  private static final int DATABASE_VERSION = 1;
 
//  public static final String KEY_ID = "_id";
  public static final String _CHALLENGE = "challenge";
  public static final String _RESPONSE = "response";
  public static final String _USED = "used";
  public static final String _SC = "sc";
  public static final String _PHONENUM = "phonenum";
  public static final String _TEMPIDENT = "tempident";
  
  private SQLiteDatabase db;
  private final Context context;
  private ChallengeResponseDBOpenHelper dbHelper;

  public ChallengeResponseDBAdapter(Context _context) {
    this.context = _context;
    dbHelper = new ChallengeResponseDBOpenHelper(context, DATABASE_NAME, 
                                    null, DATABASE_VERSION);
  }
  
  public void close() {
    db.close();
  }
  
  public void open() throws SQLiteException {  
    try {
      db = dbHelper.getWritableDatabase();
// use the following to clear the DB
//      db.delete(DATABASE_TABLE, "", null);
    } catch (SQLiteException ex) {
      db = dbHelper.getReadableDatabase();
    }
  }  
  
  //Insert a new task
  public long insertChallengeResponse(ChallengeResponseItem challengeResponse) {
    // Create a new row of values to insert.
    ContentValues newTaskValues = new ContentValues();
    // Assign values for each row.
    //Maybe we could encrypt the challenge but it is impossible to use it as a primary key then
    newTaskValues.put(_CHALLENGE, challengeResponse.getChallenge());
    //we encrypt the response since it is stored on the DB
    String encryptedResp = CryptoTeam6.Encrypt(challengeResponse.getResponse());
//    newTaskValues.put(_RESPONSE, challengeResponse.getResponse());
    newTaskValues.put(_RESPONSE,encryptedResp);
    newTaskValues.put(_USED, 0); //set to false at first
    // Insert the row.
    return db.insert(DATABASE_TABLE, null, newTaskValues);
  }

  // Remove a task based on its index
  public boolean removeChallengeResponse(String challenge) {
	  boolean removed = false;
	  //check first if it has been used
	  ChallengeResponseItem cri = getChallengeResponseItem(challenge);
	  if (!cri.getUsed()) {
		  removed = db.delete(DATABASE_TABLE, _CHALLENGE + "=\"" + challenge + "\"", null) > 0;		  
	  }
	  return removed;
  }

  // Update a task
  public boolean updateResponse(String challenge, String response) {
	  boolean updated = false;
	  //check first if it has been used
	  ChallengeResponseItem cri = getChallengeResponseItem(challenge);
	  if (!cri.getUsed()) {
		    ContentValues newValue = new ContentValues();
		    String encryptedResp = CryptoTeam6.Encrypt(response);
//		    newValue.put(_RESPONSE, response);
		    newValue.put(_RESPONSE, encryptedResp);
		    updated = db.update(DATABASE_TABLE, newValue, _CHALLENGE + "=\"" + challenge + "\"", null) > 0;
	  }
	  return updated;
  }

  // Update a task
  public boolean updateChallengeInUse(String challenge, String sc, String phoneNum, int tempint) {
     boolean updated = false;
     //check first if it has been used
     ChallengeResponseItem cri = getChallengeResponseItem(challenge);
     if (!cri.getUsed()) {
          ContentValues newValue = new ContentValues();
          newValue.put(_SC, sc);
          newValue.put(_PHONENUM, phoneNum);
          newValue.put(_TEMPIDENT, tempint);
          updated = db.update(DATABASE_TABLE, newValue, _CHALLENGE + "=\"" + challenge + "\"", null) > 0;
     }
     return updated;
  }

  // Update a task
  public boolean updateChallengeUsed(int tempIdent, boolean isUsed) {
     boolean updated = false;
     //check first if it has been used
     ChallengeResponseItem cri = getInUseChallengeResponseItem(tempIdent);
     if (!cri.getUsed()) {
        int used = isUsed ? 1 : 0;

          ContentValues newValue = new ContentValues();
          newValue.put(_USED, used);
          updated = db.update(DATABASE_TABLE, newValue, _TEMPIDENT + "=" + tempIdent, null) > 0;
     }
     return updated;
  }

  
  public Cursor getAllToDoItemsCursor() {
    return db.query(DATABASE_TABLE, 
                    new String[] { _CHALLENGE, _RESPONSE, _USED}, 
                    null, null, null, null, null);
  }

  public Cursor setCursorToChallengeResponseItem(String challenge) throws SQLException {
    Cursor result = db.query(true, DATABASE_TABLE, 
	                           new String[] { _CHALLENGE, _RESPONSE, _USED},
	                           _CHALLENGE + "=\"" + challenge + "\"", null, null, null, 
                             null, null);
    if ((result.getCount() == 0) || !result.moveToFirst()) {
      throw new SQLException("No to do items found for row: " + challenge);
    }
    return result;
  }

  public ChallengeResponseItem getChallengeResponseItem(String chall) throws SQLException {
    Cursor cursor = db.query(true, DATABASE_TABLE, 
                             new String[] {_CHALLENGE, _RESPONSE, _USED},
                             _CHALLENGE + "=\"" + chall + "\"", null, null, null, 
                             null, null);
    if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
      throw new SQLException("No challenge response found for challenge: " + chall);
    }

    String challenge = cursor.getString(cursor.getColumnIndex(_CHALLENGE));
//    String response = cursor.getString(cursor.getColumnIndex(_RESPONSE));
    String response = CryptoTeam6.Decrypt(cursor.getString(cursor.getColumnIndex(_RESPONSE)));
    boolean used = cursor.getInt(cursor.getColumnIndex(_USED)) == 0 ? false : true;
		  
    ChallengeResponseItem result = new ChallengeResponseItem(challenge, response, used);
    return result;  
  }

  //get challenge response that is in use currently
  public ChallengeResponseItem getInUseChallengeResponseItem(int tempIdent) throws SQLException {
    Cursor cursor = db.query(true, DATABASE_TABLE, 
                             new String[] {_CHALLENGE, _RESPONSE, _USED, _SC, _PHONENUM, _TEMPIDENT},
                             _TEMPIDENT + "=" + tempIdent, null, null, null, 
                             null, null);
    if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
      throw new SQLException("No challenge response found for challenge tempident: " + tempIdent);
    }

    String challenge = cursor.getString(cursor.getColumnIndex(_CHALLENGE));
//    String response = cursor.getString(cursor.getColumnIndex(_RESPONSE));
    String response = CryptoTeam6.Decrypt(cursor.getString(cursor.getColumnIndex(_RESPONSE)));
    boolean used = cursor.getInt(cursor.getColumnIndex(_USED)) == 0 ? false : true;
    String sc = cursor.getString(cursor.getColumnIndex(_SC));
    String phonenum = cursor.getString(cursor.getColumnIndex(_PHONENUM));
    int tempident = cursor.getInt(cursor.getColumnIndex(_TEMPIDENT));
        
    ChallengeResponseItem result = new ChallengeResponseItem(challenge, response, used);
    result.set_SmsCommand(sc);
    result.set_PhoneNum(phonenum);
    result.set_TempIdent(tempident);
    return result;  
  }

  //only get challenges that haven't been used
  public ChallengeResponseItem getRanChallengeResponseItem() throws SQLException {
     Cursor cursor = db.query(true, DATABASE_TABLE, 
                              new String[] {_CHALLENGE, _RESPONSE, _USED},
                              _USED + "=" + "0", null, null, null, 
                              null, null);
     if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
       throw new SQLException("No challenge response item found");
     }

     String challenge = cursor.getString(cursor.getColumnIndex(_CHALLENGE));
//     String response = cursor.getString(cursor.getColumnIndex(_RESPONSE));
     String response = CryptoTeam6.Decrypt(cursor.getString(cursor.getColumnIndex(_RESPONSE)));
     boolean used = cursor.getInt(cursor.getColumnIndex(_USED)) == 0 ? false : true;
         
     ChallengeResponseItem result = new ChallengeResponseItem(challenge, response, used);
     return result;  
   }

  //create or update the DB
  private static class ChallengeResponseDBOpenHelper extends SQLiteOpenHelper {

	  public ChallengeResponseDBOpenHelper(Context context, String name,
	                          CursorFactory factory, int version) {
	    super(context, name, factory, version);
	  }

	  // SQL Statement to create a new database.
	  private static final String DATABASE_CREATE = "create table " + 
	    DATABASE_TABLE + " (" + _CHALLENGE + " text not null primary key, " +
	    _RESPONSE + " text not null, " + _USED + " INTEGER, " +
	    _SC + " text, " + _PHONENUM + " text, " + _TEMPIDENT + " INTEGER);";

	  @Override
	  public void onCreate(SQLiteDatabase _db) {
	    _db.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
	    Log.w("TaskDBAdapter", "Upgrading from version " + 
	                           _oldVersion + " to " +
	                           _newVersion + ", which will destroy all old data");

	    // Drop the old table.
	    _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
	    // Create a new one.
	    onCreate(_db);
	  }
	}
}