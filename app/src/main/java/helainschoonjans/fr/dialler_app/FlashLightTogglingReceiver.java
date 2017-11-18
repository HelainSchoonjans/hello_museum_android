package helainschoonjans.fr.dialler_app;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by hell1 on 12/11/17.
 * Toogling on and off the flashlight on phone state ringing/other
 */
public class FlashLightTogglingReceiver extends PhoneCallReceiver {

    Camera camera;

    protected void onIncomingCallStarted(String number, Date start) {
        turnTorchOn();
    }

    protected void onOutgoingCallStarted(String number, Date start) {

    }

    protected void onIncomingCallEnded(String number, Date start, Date end) {
        turnTorchOff();
    }

    protected void onOutgoingCallEnded(String number, Date start, Date end) {

    }

    protected void onMissedCall(String number, Date start) {

    }

    private void turnTorchOn() {
        try {
//            Log.i("custom", "checking feature");
            if (savedContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
//                Log.i("custom", "opening camera");
                camera = Camera.open();
//                Log.i("custom", "getting params");
                Camera.Parameters p = camera.getParameters();
//                Log.i("custom", "setting flash mode");
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                Log.i("custom", "setting params");
                camera.setParameters(p);
//                Log.i("custom", "starting preview");
                camera.startPreview();
//                Log.i("custom", "finished starting preview");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("customError", e.getMessage());
            Toast.makeText(savedContext, "Exception throws in turning on flashlight.", Toast.LENGTH_SHORT).show();
        }
    }

    private void turnTorchOff() {
        try {
            if (savedContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(savedContext, "Exception throws in turning off flashlight.", Toast.LENGTH_SHORT).show();
        }
    }
}
