package com.ccurrin.minecraftservertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private EditText serverNameEditView;
    private ProgressBar spinner;
    private ImageView connectionBars;
    private LinearLayout connectionLayout;
    private LinearLayout softwareLayout;
    private LinearLayout playersLayout;
    private ArrayList<ImageView> playerAvatars;
    private ArrayList<ProgressBar> playerAvatarProgress;
    private ArrayList<LoadPlayerAvatar> avatarLoaders;
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
        playerAvatars = new ArrayList<>();
        playerAvatarProgress = new ArrayList<>();
        avatarLoaders = new ArrayList<>();
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
        spinner.setVisibility(View.GONE);

        /** START DEBUG TEXTVIEW */
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
        serverLookupTextView.setText(/*"Host: " + host + "\nPort: " + port + "\nLatency: " + latency +
                                     "\nPlayers: " + onlinePlayers + " / " + maxPlayers +
                                     nameAccumulator.toString() +
                                     "\nVersion: " + versionName + " : " + versionProtocol +
                                     "\nDescription: " + description +
                                     "\n");*//*CharSequenceFormatting.formatTextToMinecraftStyle("\u00A7nMinecraft Formatting\n" +
                                                                                              "\n" +
                                                                                              "\u00A7r\u00A700 \u00A711 \u00A722 \u00A733\n" +
                                                                                              "\u00A744 \u00A755 \u00A766 \u00A777\n" +
                                                                                              "\u00A788 \u00A799 \u00A7aa \u00A7bb\n" +
                                                                                              "\u00A7cc \u00A7dd \u00A7ee \u00A7ff\n" +
                                                                                              "\n" +
                                                                                              "\u00A7r\u00A70k \u00A7kMinecraft\n" +
                                                                                              "\u00A7rl \u00A7lMinecraft\n" +
                                                                                              "\u00A7rm \u00A7mMinecraft\n" +
                                                                                              "\u00A7rn \u00A7nMinecraft\n" +
                                                                                              "\u00A7ro \u00A7oMinecraft\n" +
                                                                                              "\u00A7rr \u00A7rMinecraft"));*/
                                     CharSequenceFormatting.formatTextToMinecraftStyle("\u00A73c\u00A74c\u00A75c\u00A76c\u00A77c\n" +
                                                                                       "\u00A732\u00A742\u00A752\u00A762\u00A772\n" +
                                                                                       "\u00A73<\u00A74<\u00A75<\u00A76<\u00A77<\n"));
        /** END DEBUG TEXTVIEW */

        TextView connectionHeaderTextView = (TextView) findViewById(R.id.connection_header);
        TextView connectionSpeedTextView = (TextView) findViewById(R.id.connection_speed);
        TextView descriptionBodyTextView = (TextView) findViewById(R.id.description_body);
        connectionHeaderTextView.setText(url);
        connectionSpeedTextView.setText(latency + "ms");
        descriptionBodyTextView.setText(CharSequenceFormatting.formatTextToMinecraftStyle(description));
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
        playerAvatars.clear();
        playerAvatarProgress.clear();
        for(LoadPlayerAvatar eachLoader : avatarLoaders)
        {
            eachLoader.cancel(true);
        }
        avatarLoaders.clear();
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
                playerAvatars.add(avatar);

                ProgressBar avatarLoading = new ProgressBar(this);
                avatarLoading.setLayoutParams(avatarLayoutParams);
                playerAvatarProgress.add(avatarLoading);

                LoadPlayerAvatar loader = new LoadPlayerAvatar();
                loader.index = playerAvatars.size() - 1;
                loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://cravatar.eu/helmavatar/" + name + ".png");
                avatarLoaders.add(loader);
                playerInfo.addView(avatarLoading);
                playerInfo.addView(avatar);

                TextView playerName = new TextView(this);
                LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                textViewLayoutParams.gravity = Gravity.CENTER;
                playerName.setLayoutParams(textViewLayoutParams);
                playerName.setText(name);
                playerName.setEnabled(true);
                playerInfo.addView(playerName);

                playerGrid.addView(playerInfo);
            }
            if (sampleNames.size() < onlinePlayers)
            {
                TextView morePlayers = new TextView(this);
                GridLayout.LayoutParams cellLayoutParams = new GridLayout.LayoutParams();
                cellLayoutParams.setGravity(Gravity.CENTER);
                morePlayers.setText("...+" + (onlinePlayers - sampleNames.size()) + " more");
                morePlayers.setGravity(Gravity.CENTER);
                morePlayers.setLayoutParams(cellLayoutParams);
                playerGrid.addView(morePlayers);
            }
        }
        playersLayout.setVisibility(View.VISIBLE);
    }

    private void initPlayerGrid()
    {
        playerGrid = (GridLayout) findViewById(R.id.player_grid);
        DisplayMetrics displaymetrics = this.getResources().getDisplayMetrics();
        int totalWidth = displaymetrics.widthPixels;
        float density = displaymetrics.density;
        Log.d("Initializing Grid", "Total Width: " + totalWidth + "Density: " + density);
        float availableWidth = totalWidth - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, displaymetrics);;
        float widthOfColumn = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 74, displaymetrics);
        playerGrid.setColumnCount((int)availableWidth / (int)widthOfColumn);
    }

    private class LoadPlayerAvatar extends AsyncTask<String, String, Bitmap>
    {
        public int index = -1;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            try
            {
                playerAvatarProgress.get(index).setVisibility(View.VISIBLE);
                playerAvatars.get(index).setVisibility(View.GONE);
            }
            catch(IndexOutOfBoundsException e)
            {
                e.printStackTrace();
                this.cancel(true);
            }
        }
        protected Bitmap doInBackground(String... args)
        {
            Bitmap bitmap = null;
            try
            {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap image)
        {
            try
            {
                if(image != null)
                    playerAvatars.get(index).setImageBitmap(image);
                else
                    playerAvatars.get(index).setImageResource(R.drawable.blank_avatar);
                playerAvatarProgress.get(index).setVisibility(View.GONE);
                playerAvatars.get(index).setVisibility(View.VISIBLE);
            }
            catch(IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }
        }
    }
}
