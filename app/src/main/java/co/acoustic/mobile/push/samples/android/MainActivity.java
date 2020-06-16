package co.acoustic.mobile.push.samples.android;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

//import co.acoustic.mobile.push.samples.android.R;
import co.acoustic.mobile.push.samples.android.layout.ResourcesHelper;
import co.acoustic.mobile.push.sdk.api.OperationCallback;
import co.acoustic.mobile.push.sdk.api.OperationResult;
import co.acoustic.mobile.push.sdk.api.notification.MceNotificationActionRegistry;
import co.acoustic.mobile.push.sdk.plugin.inapp.InAppManager;
import co.acoustic.mobile.push.sdk.plugin.inapp.InAppStorage;
import co.acoustic.mobile.push.sdk.plugin.inbox.InboxMessagesClient;
import co.acoustic.mobile.push.sdk.plugin.inbox.RichContent;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    protected ResourcesHelper resourcesHelper;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    InboxMessagesClient.loadInboxMessages(getApplicationContext(), new OperationCallback<List<RichContent>>() {
                        @Override
                        public void onSuccess(List<RichContent> richContents, OperationResult operationResult) {
                            Log.d("loadInboxMessages:","success");
                        }

                        @Override
                        public void onFailure(List<RichContent> richContents, OperationResult operationResult) {
                            Log.d("loadInboxMessages:","failed");
                        }
                    });
                    showInAppIfExists("pg1");
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    showInAppIfExists("pg2");
                   // mTextMessage.setText("Dashboard");//title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    showInAppIfExists("all");
                    //mTextMessage.setText(R.string.title_notifications);

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resourcesHelper = new ResourcesHelper(getResources(), getPackageName()); //gm

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener (mOnNavigationItemSelectedListener);

    }
    protected void showInAppIfExists(String pageRule) {
        Log.d("showInApp rule:",pageRule);
        int inAppFragmentId = resourcesHelper.getId("container");

        if (inAppFragmentId > 0) {
            List<String> values = new ArrayList<String>(1);
            values.add(pageRule);
            InAppManager.show(getApplicationContext(), InAppStorage.KeyName.RULE, values, getSupportFragmentManager(), inAppFragmentId);
        }
    }

}
