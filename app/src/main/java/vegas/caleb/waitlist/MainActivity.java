package vegas.caleb.waitlist;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.netbios.NbtAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    private static final String
            hostname = "192.168.0.1",
            user = "cdavis",
            pass = "where4Uhack",
            shareName = "cdavis",
            sharedFolder = "public_html",
            custFileName = "cust_list.csv",
            url = "smb://" + hostname + "/" + shareName + "/" + sharedFolder + "/" + custFileName;




    public static String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Customer> _customers = null;
    MyRecyclerViewAdapter adapter;
    protected SmbFile theFile = null;
    protected int selected = -1;
    protected EditText
            etFirst = null,
            etLast = null,
            etPhone = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (savedInstanceState != null && savedInstanceState.containsKey("Customers")) {
            _customers = savedInstanceState.getParcelableArrayList("Customers");
        } else {
            _customers = new ArrayList<Customer>();
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.clients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, _customers);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        etFirst = findViewById(R.id.fname);
        etLast = findViewById(R.id.lname);
        etPhone = findViewById(R.id.phone);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("Customers", _customers);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        this.selected = position;
    }

    public void onAdd(View v) {
        if ((etFirst.length() < 1) && (etLast.length() < 1)) return;
        Customer c = new Customer(etFirst.getText().toString(), etLast.getText().toString(), etPhone.getText().toString());
        _customers.add(c);
        adapter.notifyDataSetChanged();
        _rewrite();
    }

    public void onRemove(View v) {
        int size = _customers.size();
        if (
                size < 1
                || (selected < 0)
                || (selected > size -1)
        ) return;
        _customers.remove(selected);
        adapter.notifyDataSetChanged();
        _rewrite();
    }

    private void _rewrite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SmbFile f = MainActivity.this.theFile;
                List<Customer> customers = MainActivity.this._customers;
                if (f == null) return;
                try {
                    SmbFileOutputStream out = new SmbFileOutputStream(f);
                    BufferedWriter br = new BufferedWriter(new OutputStreamWriter(out));
                    for (Customer c : customers) {
                        br.write(c.getFName() + "," + c.getLName() + "," + c.getPhone());
                        br.newLine();
                    }
                    br.flush();
                } catch (SmbException se) {
                    se.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }).start();
    }

    public void onFetch(View v) {



        _customers.clear();

        //final NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, user, pass);

        new Thread(new Runnable() {
            public void run() {

                CIFSContext base = SingletonContext.getInstance();
                CIFSContext authed1 = base.withCredentials(new NtlmPasswordAuthenticator(null, user, pass));

                SmbFile sfile = null;
                SmbFileInputStream in = null;
                BufferedReader bfr = null;
                try {

                    sfile = new SmbFile(url, authed1);
                } catch (MalformedURLException mue) {
                    String msg = "Malformed URL exception thrown when trying to access SMB file: '";
                    msg += url;
                    msg += "'. Exception msg: " + mue.getMessage();
                    Log.d(TAG, msg);
                    return;
                }

                MainActivity.this.theFile = sfile;

                try {
                    if (!sfile.exists()) sfile.createNewFile();
                    in = new SmbFileInputStream(sfile);
                    bfr = new BufferedReader(new InputStreamReader(in));
                    String line = null;
                    while ((line = bfr.readLine()) != null) {
                        Log.d(TAG, "Read line: " + line);
                        String[] row = line.split(",");
                        if (row.length > 0) {
                            String
                                    first = row[0],
                                    last = (row.length > 1) ? row[1] : "",
                                    phone = (row.length > 2) ? row[2] : "";
                            _customers.add(new Customer(first, last, phone));
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (SmbException se) {
                    String msg = "Problem performing an operation on a remote file. Exception msg: '";
                    msg += se.getMessage();
                    msg += "'.";
                    Log.d(TAG, msg);
                    se.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
