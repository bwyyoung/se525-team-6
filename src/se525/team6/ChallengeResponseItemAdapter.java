package se525.team6;

import android.content.Context;
import java.util.*;
import android.view.*;
import android.widget.*;
import se525.team6.R;

public class ChallengeResponseItemAdapter extends
		ArrayAdapter<ChallengeResponseItem> {

	private int _resource;
	
	
	public ChallengeResponseItemAdapter(Context context,
			int textViewResourceId, List<ChallengeResponseItem> objects) {
		super(context, textViewResourceId, objects);
		_resource = textViewResourceId;
	}

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LinearLayout challengeResponseView;

	    ChallengeResponseItem item = getItem(position);

	    String challenge = item.getChallenge();
	    String response = item.getResponse();

	    if (convertView == null) {
	      challengeResponseView = new LinearLayout(getContext());
	      String inflater = Context.LAYOUT_INFLATER_SERVICE;
	      LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
	      vi.inflate(_resource, challengeResponseView, true);
	    } else {
	      challengeResponseView = (LinearLayout) convertView;
	    }

	    TextView challengeView = (TextView)challengeResponseView.findViewById(R.id.TextView01);
	    TextView responseView = (TextView)challengeResponseView.findViewById(R.id.TextView02);
	      
	    challengeView.setText(challenge);
	    responseView.setText(response);

	    return challengeResponseView;
	  }
	
	
}
