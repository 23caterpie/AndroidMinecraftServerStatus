package com.ccurrin.minecraftservertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        LocalBroadcastManager.getInstance(this).registerReceiver(lookupReceiver,
                new IntentFilter(LookupService.LOOKUP_DONE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startLookupService(View view)
    {
        // Do something in response to button
        EditText serverNameEditView = (EditText) findViewById(R.id.edit_message);
        String serverName = serverNameEditView.getText().toString();
        Intent intent = new Intent(this, LookupService.class);

        intent.putExtra("url", serverName);
        TextView serverLookupTextView = (TextView) findViewById(R.id.output_message);
        serverLookupTextView.setText("");
        spinner.setVisibility(View.VISIBLE);
        this.startService(intent);
    }

    private BroadcastReceiver lookupReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i("LookupService", "Service Received");
            showServerStatus(intent.getStringExtra("MinecraftServerHost"),
                    intent.getIntExtra("MinecraftServerPort", -404),
                    intent.getLongExtra("MinecraftServerLatency", -404),
                    intent.getIntExtra("MinecraftServerMaxPlayers", -404),
                    intent.getIntExtra("MinecraftServerOnlinePlayers", -404),
                    intent.getStringArrayListExtra("MinecraftServerSamplePlayers"),
                    intent.getStringExtra("MinecraftServerVersionName"),
                    intent.getStringExtra("MinecraftServerVersionProtocol"),
                    intent.getStringExtra("MinecraftServerDescription"));
        }
    };

    public void showServerPingResults(String host, int port, long latency)
    {
        TextView serverLookupTextView = (TextView) findViewById(R.id.output_message);
        serverLookupTextView.setText("Host: " + host + "\nPort: " + port + "\nLatency: " + latency);
    }

    public void showServerStatus(String host, int port, long latency, int maxPlayers, int onlinePlayers,
                                 ArrayList<String> sampleNames, String versionName, String versionProtocol,
                                 String description)
    {
        TextView serverLookupTextView = (TextView) findViewById(R.id.output_message);
        StringBuilder nameAccumulator = new StringBuilder();
        if(sampleNames.size() == 0 && onlinePlayers > 0)
            nameAccumulator.append("\n     This server may have its player list hidden.");
        else
        {
            for(String name : sampleNames)
            {
                nameAccumulator.append("\n     " + name);
            }
            if (sampleNames.size() < onlinePlayers)
                nameAccumulator.append("\n     +" + (onlinePlayers - sampleNames.size()) + " more...");
        }
        spinner.setVisibility(View.GONE);
        serverLookupTextView.setText("Host: " + host + "\nPort: " + port + "\nLatency: " + latency +
                                     "\nPlayers: " + onlinePlayers + " / " + maxPlayers +
                                     nameAccumulator.toString() +
                                     "\nVersion: " + versionName + " : " + versionProtocol +
                                     "\nDescription: " + description);
    }
}
