package com.ccurrin.minecraftservertest;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class MinecraftServer
{

    private String host;
    private int port;

    public MinecraftServer()
    {
        host = "example.dummy";
        port = -1;
    }

    public MinecraftServer(String newHost)
    {
        host = newHost;
        port = 25565;
    }

    public MinecraftServer(String newHost, int newPort)
    {
        host = newHost;
        port = newPort;
    }

    public static MinecraftServer lookup(String address) throws Exception
    {
        String host = address.trim();
        int port;
        if(address.contains(":"))
        {
            String[] parts = address.split(":");
            if(parts.length > 2)
            {
                throw new Exception("Invalid address '" + address + "'");
            }
            host = parts[0];
            port = Integer.parseInt(parts[1]);
        }
        else
        {
            port = 25565;
            String query = "_minecraft._tcp." + host;

            Lookup lookup = new Lookup(query, Type.SRV);//throws TextParseException if DNS name is invalid
            Record[] answers = lookup.run();
            if(lookup.getResult() == Lookup.SUCCESSFUL)
            {
                System.out.println("YES :: length: " + answers.length);
                SRVRecord answer = (SRVRecord) answers[0];
                host = answer.getTarget().toString().replaceFirst("\\.$", "");
                port = answer.getPort();
            }
            else if(lookup.getResult() == Lookup.TYPE_NOT_FOUND)
            {
                System.out.println("No Minecraft SRV found");
            }
            else if(lookup.getResult() == Lookup.HOST_NOT_FOUND)
            {
                System.out.println("No SRV found");
            }
            else
            {
                throw new Exception("Network Error!");
            }
        }

        try
        {
            InetAddress inetaddress = InetAddress.getByName(host);
            System.out.println(inetaddress);
        }
        catch(UnknownHostException | SecurityException e)
        {
            e.printStackTrace();
        }

        return new MinecraftServer(host, port);
    }

    public long ping() throws IOException
    {
        return ping(3, 7000);
    }

    public long ping(int retries, int timeout) throws IOException
    {
        long latency;
        IOException possibleError = null;
        Socket socket = new Socket();

        socket.setSoTimeout(timeout);
        socket.connect(new InetSocketAddress(host, port), timeout);

        while(retries > 0)
        {
            try
            {
                ServerPinger pinger = new ServerPinger(socket, host, port);
                pinger.handshake();
                latency = pinger.testPing();
                socket.close();
                return latency;
            }
            catch(IOException e)
            {
                Log.w("MinecraftServer", "Pinging error! Retries left = " + retries, e);
                possibleError = e;
                retries--;
            }
        }
        socket.close();
        throw possibleError;
    }

    public StatusResponse status() throws IOException
    {
        return status(3, 7000);
    }

    public StatusResponse status(int retries, int timeout) throws IOException
    {
        IOException possibleError = null;
        Socket socket = new Socket();

        socket.setSoTimeout(timeout);
        socket.connect(new InetSocketAddress(host, port), timeout);

        while(retries >= 0)
        {
            try
            {
                ServerPinger pinger = new ServerPinger(socket, host, port);
                pinger.handshake();
                StatusResponse result = pinger.readStatus();
                result.setTime(pinger.testPing());
                return result;
            }
            catch(IOException e)
            {
                Log.w("MinecraftServer", "Status error! Retries left = " + retries, e);
                possibleError = e;
                retries--;
            }
        }
        throw possibleError;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String toString()
    {
        return ("Host: " + host + " Port: " + port);
    }
}
