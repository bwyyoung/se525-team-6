package se525.gt;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

    /**
     * <p>UI control for the sample device admin.  This provides an interface
     * to enable, disable, and perform other operations with it to see
     * their effect.</p>
     *
     * <p>Note that this is implemented as an inner class only keep the sample
     * all together; typically this code would appear in some separate class.
     */
    public class Controller extends Activity {
        static final int RESULT_ENABLE = 1;

        DevicePolicyManager mDPM;
        ActivityManager mAM;
        ComponentName mDeviceAdminSample;

        Button mEnableButton;
        Button mDisableButton;

        // Password quality spinner choices
        // This list must match the list found in samples/ApiDemos/res/values/arrays.xml
        final static int mPasswordQualityValues[] = new int[] {
            DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED,
            DevicePolicyManager.PASSWORD_QUALITY_SOMETHING,
            DevicePolicyManager.PASSWORD_QUALITY_NUMERIC,
            DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC,
            DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC
        };

       Button mForceLockButton;
       Button mWipeAllDataButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
            mAM = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
            mDeviceAdminSample = new ComponentName(Controller.this, DeviceAdminSample.class);
            
            setContentView(R.layout.main); //gt002
            System.out.println("set content view"); //debug
           Log.v("++ Controller ++ ","set content view");
            
            // Watch for button clicks.
            mEnableButton = (Button)findViewById(R.id.enable); //gt002
            mEnableButton.setOnClickListener(mEnableListener);
            mDisableButton = (Button)findViewById(R.id.disable); //gt002
            mDisableButton.setOnClickListener(mDisableListener);
            mForceLockButton = (Button)findViewById(R.id.lock);
            mForceLockButton.setOnClickListener(mForceLockListener);
            mWipeAllDataButton = (Button)findViewById(R.id.wipe);
            mWipeAllDataButton.setOnClickListener(mWipeDataListener);
        }

        void updateButtonStates() {
            boolean active = mDPM.isAdminActive(mDeviceAdminSample);
            if (active) {
                mEnableButton.setEnabled(false);
                mDisableButton.setEnabled(true);
                mForceLockButton.setEnabled(true);
                mWipeAllDataButton.setEnabled(true);
            } else {
                mEnableButton.setEnabled(true);
                mDisableButton.setEnabled(false);
                mForceLockButton.setEnabled(false);
                mWipeAllDataButton.setEnabled(false);
            }
        }

        @Override
        protected void onResume() {
            super.onResume();
            updateButtonStates();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case RESULT_ENABLE:
                    if (resultCode == Activity.RESULT_OK) {
                        Log.i("DeviceAdminSample", "Admin enabled!");
                    } else {
                        Log.i("DeviceAdminSample", "Admin enable FAILED!");
                    }
                    return;
            }

            super.onActivityResult(requestCode, resultCode, data);
        }

        private OnClickListener mEnableListener = new OnClickListener() {
            public void onClick(View v) {
                // Launch the activity to have the user enable our admin.
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                        mDeviceAdminSample);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "Additional text explaining why this needs to be added.");
                startActivityForResult(intent, RESULT_ENABLE);
            }
        };

        private OnClickListener mDisableListener = new OnClickListener() {
            public void onClick(View v) {
                mDPM.removeActiveAdmin(mDeviceAdminSample);
                updateButtonStates();
            }
        };

        private OnClickListener mSetPasswordListener = new OnClickListener() {
            public void onClick(View v) {
                // Launch the activity to have the user set a new password.
                Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                startActivity(intent);
            }
        };

        private OnClickListener mResetPasswordListener = new OnClickListener() {
            public void onClick(View v) {
                if (mAM.isUserAMonkey()) {
                    // Don't trust monkeys to do the right thing!
                    AlertDialog.Builder builder = new AlertDialog.Builder(Controller.this);
                    builder.setMessage("You can't reset my password because you are a monkey!");
                    builder.setPositiveButton("I admit defeat", null);
                    builder.show();
                    return;
                }
/*                boolean active = mDPM.isAdminActive(mDeviceAdminSample);
                if (active) {
                    mDPM.resetPassword(mPassword.getText().toString(),
                            DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                }*/
            }
        };

        private OnClickListener mForceLockListener = new OnClickListener() {
            public void onClick(View v) {
                if (mAM.isUserAMonkey()) {
                    // Don't trust monkeys to do the right thing!
                    AlertDialog.Builder builder = new AlertDialog.Builder(Controller.this);
                    builder.setMessage("You can't lock my screen because you are a monkey!");
                    builder.setPositiveButton("I admit defeat", null);
                    builder.show();
                    return;
                }
                boolean active = mDPM.isAdminActive(mDeviceAdminSample);
                if (active) {
                    mDPM.lockNow();
                }
            }
        };

        private OnClickListener mWipeDataListener = new OnClickListener() {
            public void onClick(final View v) {
                if (mAM.isUserAMonkey()) {
                    // Don't trust monkeys to do the right thing!
                    AlertDialog.Builder builder = new AlertDialog.Builder(Controller.this);
                    builder.setMessage("You can't wipe my data because you are a monkey!");
                    builder.setPositiveButton("I admit defeat", null);
                    builder.show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Controller.this);
                builder.setMessage("This will erase all of your data.  Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Controller.this);
                        if (v == mWipeAllDataButton) {
                            builder.setMessage("This is not a test.  "
                                    + "This WILL erase all of your data, "
                                    + "including external storage!  "
                                    + "Are you really absolutely sure?");
                        } else {
                            builder.setMessage("This is not a test.  "
                                    + "This WILL erase all of your data!  "
                                    + "Are you really absolutely sure?");
                        }
                        builder.setPositiveButton("BOOM!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                boolean active = mDPM.isAdminActive(mDeviceAdminSample);
                                if (active) {
 //                                   mDPM.wipeData(v == mWipeAllDataButton // WIPE_EXTERNAL_STORAGE problem
 //                                           ? DevicePolicyManager.WIPE_EXTERNAL_STORAGE : 0); //gt002
                                  mDPM.wipeData(0); //gt002
                                }
                            }
                        });
                        builder.setNegativeButton("Oops, run away!", null);
                        builder.show();
                    }
                });
                builder.setNegativeButton("No way!", null);
                builder.show();
            }
        };

 /*       private OnClickListener mSetTimeoutListener = new OnClickListener() {

            public void onClick(View v) {
                if (mAM.isUserAMonkey()) {
                    // Don't trust monkeys to do the right thing!
                    AlertDialog.Builder builder = new AlertDialog.Builder(Controller.this);
                    builder.setMessage("You can't lock my screen because you are a monkey!");
                    builder.setPositiveButton("I admit defeat", null);
                    builder.show();
                    return;
                }
                boolean active = mDPM.isAdminActive(mDeviceAdminSample);
                if (active) {
                    long timeMs = 1000L*Long.parseLong(mTimeout.getText().toString());
                    mDPM.setMaximumTimeToLock(mDeviceAdminSample, timeMs);
                }
            }
        };*/
    }