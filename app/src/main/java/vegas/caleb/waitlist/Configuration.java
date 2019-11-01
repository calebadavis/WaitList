package vegas.caleb.waitlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Configuration extends AppCompatActivity {

    private EditText
        etUser,
        etPassword,
        etHostname,
        etSharename,
        etFolderPath,
        etCustFileName;

    private String
        user, pass, hostname, shareName, pathToFiles, custFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        etUser = findViewById(R.id.userid);
        etPassword = findViewById(R.id.password);
        etHostname = findViewById(R.id.hostname);
        etSharename = findViewById(R.id.share);
        etFolderPath = findViewById(R.id.path);
        etCustFileName = findViewById(R.id.file);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.PREFS_FILENAME, 0); // 0 - for private mode

        if (pref.contains(Constants.PREFS_USER_KEY)) user = pref.getString(Constants.PREFS_USER_KEY, "");
        if (pref.contains(Constants.PREFS_PASSWORD_KEY)) pass = pref.getString(Constants.PREFS_PASSWORD_KEY, "");
        if (pref.contains(Constants.PREFS_SHARENAME_KEY)) shareName = pref.getString(Constants.PREFS_SHARENAME_KEY, "");
        if (pref.contains(Constants.PREFS_HOSTNAME_KEY)) hostname = pref.getString(Constants.PREFS_HOSTNAME_KEY, "");
        if (pref.contains(Constants.PREFS_FOLDER_PATH_KEY)) pathToFiles = pref.getString(Constants.PREFS_FOLDER_PATH_KEY, "");
        if (pref.contains(Constants.PREFS_CUSTFILE_KEY)) custFileName = pref.getString(Constants.PREFS_CUSTFILE_KEY, "cust_list.csv");

        etUser.setText(user);
        etPassword.setText(pass);
        etHostname.setText(hostname);
        etSharename.setText(shareName);
        etFolderPath.setText(pathToFiles);
        etCustFileName.setText(custFileName);
    }

    public void onApply(View v) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.PREFS_FILENAME, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.PREFS_USER_KEY, etUser.getText().toString());
        editor.putString(Constants.PREFS_PASSWORD_KEY, etPassword.getText().toString());
        editor.putString(Constants.PREFS_HOSTNAME_KEY, etHostname.getText().toString());
        editor.putString(Constants.PREFS_SHARENAME_KEY, etSharename.getText().toString());
        editor.putString(Constants.PREFS_FOLDER_PATH_KEY, etFolderPath.getText().toString());
        editor.putString(Constants.PREFS_CUSTFILE_KEY, etCustFileName.getText().toString());
        editor.apply();
        finish();
    }
}
