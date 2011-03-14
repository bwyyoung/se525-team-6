package se525.team6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Primary extends Activity {
   private Button _myButton01;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //adg added button to launch the challenge response adding screen
        _myButton01 = (Button)findViewById(R.id.Button01);
        
        // capture onclick of button to do processing
        _myButton01.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {          
              //launch the challenge response activity;
              Intent intent = new Intent(Primary.this, SmsActivity.class);
              startActivity(intent);
           }
        });
    }
}