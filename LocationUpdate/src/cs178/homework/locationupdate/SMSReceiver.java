package cs178.homework.locationupdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		/*Location location = (Location) bundle.get("sms_location");
		MainActivity main = new MainActivity();
		main.onLocationChanged(location);*/
		
		/*Intent mapIntent = new Intent(context, MapUpdates.class);
		mapIntent.putExtra("sms_location", location);
		mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(mapIntent);*/
		
		SmsMessage[] sms = null;
		String msg = "";
		
		if(bundle != null)
		{
			Object[] pdus = (Object[]) bundle.get("pdus");
			sms = new SmsMessage[pdus.length];
				
			for(int i = 0; i < sms.length; i++)
			{
				sms[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				msg = sms[i].getMessageBody().toString();
			}
			
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

			Intent mapIntent = new Intent(context, MainActivity.class);
			mapIntent.putExtra("sms_location", msg);
			mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(mapIntent);
		}
	}

}
