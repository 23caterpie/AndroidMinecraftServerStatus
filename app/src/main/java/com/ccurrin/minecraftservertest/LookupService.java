package com.ccurrin.minecraftservertest;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class LookupService extends IntentService
{
    public static final String LOOKUP_DONE = "com.ccurrin.LOOKUP_DONE";

    public LookupService()
    {
        super(LookupService.class.getName());
    }

    public LookupService(String name)
    {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.i("LookupService", "Service Started");

        String passedURL = intent.getStringExtra("url");

        MinecraftServer server = doLookup(passedURL);

        Log.i("LookupService", "Service Stopped");

        Intent i = new Intent(LOOKUP_DONE);

        i.putExtra("MinecraftServerHost", server.getHost());
        i.putExtra("MinecraftServerPort", server.getPort());
        try
        {
            //i.putExtra("MinecraftServerLatency", server.ping());
            StatusResponse status = server.status();
            i.putExtra("MinecraftServerLatency", status.getTime());
            i.putExtra("MinecraftServerMaxPlayers", status.getPlayers().getMax());
            i.putExtra("MinecraftServerOnlinePlayers", status.getPlayers().getOnline());
            i.putStringArrayListExtra("MinecraftServerSamplePlayers", status.getPlayers().getSampleNames());
            i.putExtra("MinecraftServerVersionName", status.getVersion().getName());
            i.putExtra("MinecraftServerVersionProtocol", status.getVersion().getProtocol());
            i.putExtra("MinecraftServerDescription", status.getDescription());
        }
        catch(IOException e)
        {
            i.putExtra("MinecraftServerLatency", (long)-1);
            i.putExtra("MinecraftServerMaxPlayers", -1);
            i.putExtra("MinecraftServerOnlinePlayers", -1);
            ArrayList<String> dummySampleNames = new ArrayList<>();
            dummySampleNames.add("dummy");
            i.putStringArrayListExtra("MinecraftServerSamplePlayers", dummySampleNames);
            i.putExtra("MinecraftServerVersionName", "dummyVersionName");
            i.putExtra("MinecraftServerVersionProtocol", "dummyVersionProtocol");
            i.putExtra("MinecraftServerDescription", "DummyDescription");
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    protected MinecraftServer doLookup(String theURL)
    {
        MinecraftServer lookupResults = new MinecraftServer();
        try
        {
            lookupResults = MinecraftServer.lookup(theURL);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return lookupResults;
        }
    }
}
