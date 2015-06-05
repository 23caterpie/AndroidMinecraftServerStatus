package com.ccurrin.minecraftservertest;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class ServerPinger
{
    private static Random random = new Random();
    private int version;
    private Socket connection;
    private String host;
    private int port;
    private long pingToken;

    public ServerPinger(Socket newSocket, String newHost, int newPort)
    {
        version = 47;
        connection = newSocket;
        host = newHost;
        port = newPort;
        pingToken = random.nextLong();
    }

    public ServerPinger(Socket newSocket, String newHost, int newPort, int newVersion, int newPingToken)
    {
        version = newVersion;
        connection = newSocket;
        host = newHost;
        port = newPort;
        pingToken = newPingToken;
    }

    public void handshake() throws IOException
    {
        DataOutputStream streamToServer = new DataOutputStream(connection.getOutputStream());

        ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();
        DataOutputStream handshakePacket = new DataOutputStream(outputByteArray);

        VarInt.write(handshakePacket, 0);      //packet id for handshake
        VarInt.write(handshakePacket, version);//protocol version
        VarInt.writeUTF(handshakePacket, host);//host length then host string
        handshakePacket.writeShort(port);      //port
        VarInt.write(handshakePacket, 1);      //state (1 for handshake)

        VarInt.write(streamToServer, outputByteArray.size());//prepend size
        streamToServer.write(outputByteArray.toByteArray()); //write handshake packet
        outputByteArray.close();
        handshakePacket.close();
    }

    public long testPing() throws IOException
    {
        DataOutputStream streamToServer = new DataOutputStream(connection.getOutputStream());
        DataInputStream streamFromServer = new DataInputStream(connection.getInputStream());

        ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();
        DataOutputStream testPingPacket = new DataOutputStream(outputByteArray);

        VarInt.write(testPingPacket, 1);    //packet id for test ping
        testPingPacket.writeLong(pingToken);//long token

        VarInt.write(streamToServer, outputByteArray.size());//prepend size
        long beforeTime = System.currentTimeMillis();
        streamToServer.write(outputByteArray.toByteArray()); //write test ping packet

        VarInt.read(streamFromServer);//Packet Size
        long afterTime = System.currentTimeMillis();
        int responseId = VarInt.read(streamFromServer);
        if (responseId == -1)
            throw new IOException("Premature end of stream.");
        else if (responseId != 1)
            throw new IOException("Received invalid 'test ping' response packet.");
        long receivedToken = streamFromServer.readLong();
        if (pingToken != receivedToken)
            throw new IOException("Received mangled ping response packet (expected token " + pingToken +
                    ", received " + receivedToken + ")");
        outputByteArray.close();
        testPingPacket.close();
        return (afterTime - beforeTime);
    }

    public StatusResponse readStatus() throws IOException
    {
        Gson gson = new Gson();

        DataOutputStream streamToServer = new DataOutputStream(connection.getOutputStream());
        DataInputStream streamFromServer = new DataInputStream(connection.getInputStream());

        ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();
        DataOutputStream statusPacket = new DataOutputStream(outputByteArray);

        VarInt.write(statusPacket, 0);
        VarInt.write(streamToServer, outputByteArray.size());
        streamToServer.write(outputByteArray.toByteArray());

        VarInt.read(streamFromServer);//Packet Size
        int responseId = VarInt.read(streamFromServer);
        if (responseId == -1)
            throw new IOException("Premature end of stream.");
        else if (responseId != 0)
            throw new IOException("Received invalid 'read status' response packet.");
        int length = VarInt.read(streamFromServer); //length of json string
        if (length < 0)
            throw new IOException("Premature end of stream.");
        else if (length == 0)
            throw new IOException("Received invalid 'read status' response packet.");
        byte[] jsonInput = new byte[length];
        streamFromServer.readFully(jsonInput);
        String json = new String(jsonInput);

        StatusResponse response = gson.fromJson(json, StatusResponse.class);
        outputByteArray.close();
        statusPacket.close();
        return response;
    }
}
