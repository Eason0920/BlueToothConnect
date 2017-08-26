package tw.master.eason.bluetoothconnect;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 88;
    private Activity mActivity;
    private final HashMap<String, String> mDeviceHashMap = new HashMap<>();
    private final BluetoothAdapter mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BroadcastReceiver mBroadcastReceiver;

    @BindView(R.id.searchBlueToothButton)
    Button mSearchBlueToothButton;
    @BindView(R.id.connectDeviceTextView)
    TextView mConnectDeviceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        ButterKnife.bind(this);

        enableLocalBlueTooth();
        setBlueToothReceiver();
        showConnectedDevice();
        setSearchBlueToothButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    public void enableLocalBlueTooth() {
        if (mBlueToothAdapter != null) {
            mBlueToothAdapter.enable();

//            if (!mBlueToothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
        }
    }

    private void setBlueToothReceiver() {

        // Create a BroadcastReceiver for ACTION_FOUND
        mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    Toast.makeText(mActivity, "ACTION_FOUND", Toast.LENGTH_LONG).show();

                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Add the name and address to an map adapter to show
                    mDeviceHashMap.put(device.getName(), device.getAddress());
                    showConnectedDevice();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Toast.makeText(mActivity, "ACTION_DISCOVERY_FINISHED", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Register the BroadcastReceiver
        //Don't forget to unregister during onDestroy
        IntentFilter mIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    private void setSearchBlueToothButton() {
        mSearchBlueToothButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mBlueToothAdapter.isDiscovering()) {
                    mBlueToothAdapter.cancelDiscovery();
                }

                mBlueToothAdapter.startDiscovery();
            }
        });
    }

    private void showConnectedDevice() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : mDeviceHashMap.entrySet()) {

            sb.append(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
        }

        mConnectDeviceTextView.setText(sb.toString());
    }
}