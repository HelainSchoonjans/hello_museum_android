package helainschoonjans.fr.dialler_app;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

// https://stackoverflow.com/questions/21990947/detect-the-end-of-an-answered-incoming-call-by-user-in-android-not-declined
public abstract class PhoneCallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
    static PhoneCallStartEndDetector listener;
    String outgoingSavedNumber;
    protected Context savedContext;


    @Override
    public void onReceive(Context context, Intent intent) {
        savedContext = context;
        if(listener == null){
            listener = new PhoneCallStartEndDetector();
        }

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            listener.setOutgoingNumber(intent.getExtras().getString("android.intent.extra.PHONE_NUMBER"));
            return;
        }

        //The other intent tells us the phone state changed.  Here we set a listener to deal with it
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
        telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    //Derived classes should override these to respond to specific events of interest
    protected abstract void onIncomingCallStarted(String number, Date start);
    protected abstract void onOutgoingCallStarted(String number, Date start);
    protected abstract void onIncomingCallEnded(String number, Date start, Date end); 
    protected abstract void onOutgoingCallEnded(String number, Date start, Date end);
    protected abstract void onMissedCall(String number, Date start);

    //Deals with actual events
    public class PhoneCallStartEndDetector extends PhoneStateListener {
        int lastState = TelephonyManager.CALL_STATE_IDLE;
        Date callStartTime;
        boolean isIncoming;
        String savedNumber;  //because the passed incoming is only valid in ringing

        public PhoneCallStartEndDetector() {}

        //The outgoing number is only sent via a separate intent, so we need to store it out of band
        public void setOutgoingNumber(String number){
            savedNumber = number;
        }

        //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
        //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if(lastState == state){
                //No change, debounce extras
                return;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    isIncoming = true;
                    callStartTime = new Date();
                    savedNumber = incomingNumber;
                    onIncomingCallStarted(incomingNumber, callStartTime);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing donw on them
                    if(lastState != TelephonyManager.CALL_STATE_RINGING){
                        isIncoming = false;
                        callStartTime = new Date();
                        onOutgoingCallStarted(savedNumber, callStartTime);                      
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                    if(lastState == TelephonyManager.CALL_STATE_RINGING){
                        //Ring but no pickup-  a miss
                        onMissedCall(savedNumber, callStartTime);
                    }
                    else if(isIncoming){
                        onIncomingCallEnded(savedNumber, callStartTime, new Date());                        
                    }
                    else{
                        onOutgoingCallEnded(savedNumber, callStartTime, new Date());                                                
                    }
                    break;
            }
            lastState = state;
        }

    }



}