package se525.team6;
/* Class used to add Challenge/Response pairs for the SMS remote controlling
 * a. gojdas
*/
import java.util.ArrayList;

//import java.util.Date;

//import com.paad.todolist.ToDoDBAdapter;

//import com.paad.todolist.R;
//import com.paad.todolist.ToDoDBAdapter;
//import com.paad.todolist.ToDoItem;

import se525.team6.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SmsActivity extends Activity {
	private SMSReceive _SMSReceive;
	private ChallengeResponseDBAdapter _ChallengeResponseDBAdapter;
	private Cursor _ChallengeResponseCursor;
	private ArrayList<ChallengeResponseItem> _ChallengeResponseItems;
	private EditText _myEditText01;
	private EditText _myEditText02;
	private Handler _H;
	private Button _myButton01;
	private ChallengeResponseItemAdapter cria;
	private ListView _myListView01;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_activity);
        
        _myEditText01 = (EditText)findViewById(R.id.EditText01);
        _myEditText01.setText("");
        _myEditText02 = (EditText)findViewById(R.id.EditText02);
        _myEditText02.setText("");
        _myButton01 = (Button)findViewById(R.id.Button01);
        _myListView01 = (ListView)findViewById(R.id.ListView01);
	    //create a handler for the updating of the GUI
	    _H = new Handler ();

	    _SMSReceive = new SMSReceive();
        SetupChallengeResponseDB();

		// capture onclick of button to do processing
		_myButton01.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {				
//				SetKey();
//		       String encrypted = CryptoTeam6.Encrypt("test the encrytpion");
//		       Toast.makeText(SmsActivity.this, encrypted, Toast.LENGTH_SHORT)
//		       .show();
//		       String decrypted = CryptoTeam6.Decrypt(encrypted);
//		       Toast.makeText(SmsActivity.this, decrypted, Toast.LENGTH_SHORT)
//		       .show();
				
				// create a thread to do long running process outside of the gui
				// thread
				new Thread(doBT_Button01).start();
			}
		});

	    registerForContextMenu(_myListView01);
//	    restoreUIState();

	    
    }
    //Button01 stuff in here *****************************************************************************
	private Runnable doBT_Button01 = new Runnable() {
		public void run() {
			AddChallengeResponse();
		}
	}; 
	//do the http request here	
	private void AddChallengeResponse() {

	   
		String challenge = new String(_myEditText01.getText().toString());
		String response = new String(_myEditText02.getText().toString());

		ChallengeResponseItem challengeResponseItem = new ChallengeResponseItem(challenge, response);
		_ChallengeResponseDBAdapter.insertChallengeResponse(challengeResponseItem);
        updateArray();
	}

		
	
	
    private void SetupChallengeResponseDB(){
    	_ChallengeResponseDBAdapter = new ChallengeResponseDBAdapter(this);
        _ChallengeResponseItems = new ArrayList<ChallengeResponseItem>();
        int resID = R.layout.challengeresponse_item;
        cria = new ChallengeResponseItemAdapter(this, resID, _ChallengeResponseItems);
        _myListView01.setAdapter(cria);

        // Open or create the database
        _ChallengeResponseDBAdapter.open();
        
        populateChallengeResponseList();   	
    }

    private void populateChallengeResponseList() {
        // Get all the todo list items from the database.
    	_ChallengeResponseCursor = _ChallengeResponseDBAdapter.getAllToDoItemsCursor();
        this.startManagingCursor(_ChallengeResponseCursor);
          
        // Update the array.
        updateArray();
      }

    private void updateArray() {
		_ChallengeResponseCursor.requery();
		
		_ChallengeResponseItems.clear();
	    
		if (_ChallengeResponseCursor.moveToFirst()) {
			  do { 
			    String challenge = _ChallengeResponseCursor.getString(_ChallengeResponseCursor.getColumnIndex(ChallengeResponseDBAdapter._CHALLENGE));
//			    String response = _ChallengeResponseCursor.getString(_ChallengeResponseCursor.getColumnIndex(ChallengeResponseDBAdapter._RESPONSE));

			    String response = 
			       CryptoTeam6.Decrypt(_ChallengeResponseCursor.getString(_ChallengeResponseCursor.getColumnIndex(ChallengeResponseDBAdapter._RESPONSE)));
			    boolean used = _ChallengeResponseCursor.getInt(_ChallengeResponseCursor.getColumnIndex(ChallengeResponseDBAdapter._USED)) == 0 ? false : true;
			
			    ChallengeResponseItem challengeResponseItem = new ChallengeResponseItem(challenge, response, used);
			    _ChallengeResponseItems.add(0, challengeResponseItem);
			  } while(_ChallengeResponseCursor.moveToNext());
			  
//			  cria.notifyDataSetChanged();//have an issue here when adding new challenge response
	         _H.post(doUpdateGuiNotifyDataSetChanged);
		}
	}

   private Runnable doUpdateGuiNotifyDataSetChanged = new Runnable() {
      public void run() {
         UpdateGuiNotifyDataSetChanged();
      }
   };

   // code to update the gui
   private void UpdateGuiNotifyDataSetChanged() {
      cria.notifyDataSetChanged();
   }
    
    
//      private void restoreUIState() {
//        // Get the activity preferences object.
//        SharedPreferences settings = getPreferences(0);
//
//        // Read the UI state values, specifying default values.
//        String text = settings.getString(TEXT_ENTRY_KEY, "");
//        Boolean adding = settings.getBoolean(ADDING_ITEM_KEY, false);
//
//        // Restore the UI to the previous state.
//        if (adding) {
//          addNewItem();
//          myEditText.setText(text);
//        }
//      }
//      
//      @Override
//      public void onSaveInstanceState(Bundle outState) {
//        outState.putInt(SELECTED_INDEX_KEY, myListView.getSelectedItemPosition());
//
//        super.onSaveInstanceState(outState);
//      }
//
//      @Override
//      public void onRestoreInstanceState(Bundle savedInstanceState) {
//        int pos = -1;
//
//        if (savedInstanceState != null)
//          if (savedInstanceState.containsKey(SELECTED_INDEX_KEY))
//            pos = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);
//
//        myListView.setSelection(pos);
//      }
//      
//      @Override
//      protected void onPause() {
//        super.onPause();
//        
//        // Get the activity preferences object.
//        SharedPreferences uiState = getPreferences(0);
//        // Get the preferences editor.
//        SharedPreferences.Editor editor = uiState.edit();
//
//        // Add the UI state preference values.
//        editor.putString(TEXT_ENTRY_KEY, myEditText.getText().toString());
//        editor.putBoolean(ADDING_ITEM_KEY, addingNew);
//        // Commit the preferences.
//        editor.commit();
//      }
//      
//      @Override
//      public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//
//        // Create and add new menu items.
//        MenuItem itemAdd = menu.add(0, ADD_NEW_TODO, Menu.NONE,
//                                    R.string.add_new);
//        MenuItem itemRem = menu.add(0, REMOVE_TODO, Menu.NONE,
//                                    R.string.remove);
//
//        // Assign icons
//        itemAdd.setIcon(R.drawable.add_new_item);
//        itemRem.setIcon(R.drawable.remove_item);
//
//        // Allocate shortcuts to each of them.
//        itemAdd.setShortcut('0', 'a');
//        itemRem.setShortcut('1', 'r');
//
//        return true;
//      }
//
//      @Override
//      public boolean onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//
//        int idx = myListView.getSelectedItemPosition();
//
//        String removeTitle = getString(addingNew ? 
//                                       R.string.cancel : R.string.remove);
//
//        MenuItem removeItem = menu.findItem(REMOVE_TODO);
//        removeItem.setTitle(removeTitle);
//        removeItem.setVisible(addingNew || idx > -1);
//
//        return true;
//      }
//      
//      @Override
//      public void onCreateContextMenu(ContextMenu menu, 
//                                      View v, 
//                                      ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//
//        menu.setHeaderTitle("Selected To Do Item");
//        menu.add(0, REMOVE_TODO, Menu.NONE, R.string.remove);
//      }
//      
//      @Override
//      public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);
//
//        int index = myListView.getSelectedItemPosition();
//
//        switch (item.getItemId()) {
//          case (REMOVE_TODO): {
//            if (addingNew) {
//              cancelAdd();
//            } 
//            else {
//              removeItem(index);
//            }
//            return true;
//          }
//          case (ADD_NEW_TODO): { 
//            addNewItem();
//            return true; 
//          }
//        }
//
//        return false;
//      }
//      
//      @Override
//      public boolean onContextItemSelected(MenuItem item) {  
//        super.onContextItemSelected(item);
//        switch (item.getItemId()) {
//          case (REMOVE_TODO): {
//            AdapterView.AdapterContextMenuInfo menuInfo;
//            menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
//            int index = menuInfo.position;
//
//            removeItem(index);
//            return true;
//          }
//        }
//        return false;
//      }
//            
//      private void cancelAdd() {
//        addingNew = false;
//        myEditText.setVisibility(View.GONE);
//      }
//
      private void addNewItem() {
//        addingNew = true;
        _myEditText01.setVisibility(View.VISIBLE);
        _myEditText01.requestFocus(); 
      }

      private void removeItem(String challenge) {
        // Items are added to the listview in reverse order, so invert the index.
         _ChallengeResponseDBAdapter.removeChallengeResponse(challenge);
        updateArray();
      }
    
    @Override
    public void onDestroy() {
      super.onDestroy();
        
      // Close the database
      if (_ChallengeResponseDBAdapter != null){
          _ChallengeResponseDBAdapter.close();    	  
      }
    }

}

