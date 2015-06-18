package com.ccurrin.minecraftservertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private EditText serverNameEditView;
    private ProgressBar spinner;
    private ImageView connectionBars;
    private LinearLayout connectionLayout;
    private LinearLayout softwareLayout;
    private LinearLayout playersLayout;
    private GridLayout playerGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverNameEditView = (EditText) findViewById(R.id.edit_message);
        serverNameEditView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND)
                {
                    startLookupService(v);
                    handled = true;
                }
                return false;
            }
        });

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        connectionLayout = (LinearLayout) findViewById(R.id.connection_layout);
        connectionLayout.setVisibility(View.GONE);
        softwareLayout = (LinearLayout) findViewById(R.id.software_layout);
        softwareLayout.setVisibility(View.GONE);
        playersLayout = (LinearLayout) findViewById(R.id.players_layout);
        playersLayout.setVisibility(View.GONE);

        initPlayerGrid();

        LocalBroadcastManager.getInstance(this).registerReceiver(lookupReceiver,
                new IntentFilter(LookupService.LOOKUP_DONE));

        connectionBars = (ImageView) findViewById(R.id.connection_speed_icon);
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
        String serverName = serverNameEditView.getText().toString();
        Intent intent = new Intent(this, LookupService.class);

        intent.putExtra("url", serverName);
        TextView serverLookupTextView = (TextView) findViewById(R.id.output_message);
        serverLookupTextView.setText("");
        connectionLayout.setVisibility(View.GONE);
        softwareLayout.setVisibility(View.GONE);
        playersLayout.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
        this.startService(intent);
    }

    private BroadcastReceiver lookupReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i("LookupService", "Service Received");
            showServerStatus(
                    intent.getStringExtra("OriginalURL"),
                    intent.getStringExtra("MinecraftServerHost"),
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

    public void showServerStatus(String url, String host, int port, long latency, int maxPlayers, int onlinePlayers,
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
                nameAccumulator.append("\n     ");
                nameAccumulator.append(name);
            }
            if (sampleNames.size() < onlinePlayers)
            {
                nameAccumulator.append("\n     +");
                nameAccumulator.append(onlinePlayers - sampleNames.size());
                nameAccumulator.append(" more...");
            }
        }
        spinner.setVisibility(View.GONE);
        serverLookupTextView.setText("Host: " + host + "\nPort: " + port + "\nLatency: " + latency +
                                     "\nPlayers: " + onlinePlayers + " / " + maxPlayers +
                                     nameAccumulator.toString() +
                                     "\nVersion: " + versionName + " : " + versionProtocol +
                                     "\nDescription: " + description);

        TextView connectionHeaderTextView = (TextView) findViewById(R.id.connection_header);
        TextView connectionSpeedTextView = (TextView) findViewById(R.id.connection_speed);
        TextView descriptionBodyTextView = (TextView) findViewById(R.id.description_body);
        connectionHeaderTextView.setText(url);
        connectionSpeedTextView.setText(latency + "ms");
        descriptionBodyTextView.setText(description);
        if(latency < 0)
        {
            connectionBars.setImageResource(R.drawable.connection_speed_0);
        }
        else if(latency < 250)
        {
            connectionBars.setImageResource(R.drawable.connection_speed_full);
        }
        else if(latency < 400)
        {
            connectionBars.setImageResource(R.drawable.connection_speed_4);
        }
        else if(latency < 550)
        {
            connectionBars.setImageResource(R.drawable.connection_speed_3);
        }
        else if(latency < 700)
        {
            connectionBars.setImageResource(R.drawable.connection_speed_2);
        }
        else
        {
            connectionBars.setImageResource(R.drawable.connection_speed_1);
        }
        connectionLayout.setVisibility(View.VISIBLE);

        TextView softwareHeaderTextView = (TextView) findViewById(R.id.software_header);
        TextView softwareBodyTextView = (TextView) findViewById(R.id.software_body);
        softwareHeaderTextView.setText("Server Software");
        softwareBodyTextView.setText(versionName);
        softwareLayout.setVisibility(View.VISIBLE);

        TextView playersHeaderTextView = (TextView) findViewById(R.id.players_header);
        playersHeaderTextView.setText("Online Players: " + onlinePlayers + "/" + maxPlayers);
        playerGrid.removeAllViews();
        if(playersLayout.getChildCount() > 2)
            playersLayout.removeViews(2, playersLayout.getChildCount() - 2);
        if(onlinePlayers == 0)
        {
            TextView noPlayers = new TextView(this);
            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            noPlayers.setLayoutParams(textViewLayoutParams);
            noPlayers.setText("There are no players online right now.");
            noPlayers.setBackgroundResource(R.drawable.body_box);
            playersLayout.addView(noPlayers);
        }
        else if(sampleNames.size() == 0)
        {
            TextView hiddenPlayers = new TextView(this);
            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            hiddenPlayers.setLayoutParams(textViewLayoutParams);
            hiddenPlayers.setText("This server may have its player list hidden.");
            hiddenPlayers.setBackgroundResource(R.drawable.body_box);
            playersLayout.addView(hiddenPlayers);
        }
        else
        {
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 5, getResources().getDisplayMetrics());
            int avatarWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 64, getResources().getDisplayMetrics());
            for(String name : sampleNames)
            {
                LinearLayout playerInfo = new LinearLayout(this);
                playerInfo.setOrientation(LinearLayout.VERTICAL);
                GridLayout.LayoutParams cellLayoutParams = new GridLayout.LayoutParams();

                ImageView avatar = new ImageView(this);
                LinearLayout.LayoutParams avatarLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                avatarLayoutParams.setMargins(margin, margin, margin, 0);
                avatarLayoutParams.width = avatarLayoutParams.height = avatarWidth;
                avatar.setLayoutParams(avatarLayoutParams);
                avatar.setImageResource(R.drawable.blank_avatar);
                playerInfo.addView(avatar);

                TextView playerName = new TextView(this);
                //LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //playerName.setLayoutParams(textViewLayoutParams);
                playerName.setText(name);
                playerName.setEnabled(true);
                playerInfo.addView(playerName);

                playerGrid.addView(playerInfo);
            }
            if (sampleNames.size() < onlinePlayers)
            {
                TextView morePlayers = new TextView(this);
                morePlayers.setText("...+" + (onlinePlayers - sampleNames.size()) + " more");
                playerGrid.addView(morePlayers);
            }
        }
        playersLayout.setVisibility(View.VISIBLE);
    }

    private void initPlayerGrid()
    {
        //FIX THIS CRAP
        playerGrid = (GridLayout) findViewById(R.id.player_grid);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int totalWidth = displaymetrics.widthPixels;
        float density = displaymetrics.density;
        float availableWidth = totalWidth - (14 / density);
        float widthOfColumn = 74 / density;
        playerGrid.setColumnCount((int)availableWidth / (int)widthOfColumn);
    }
}
