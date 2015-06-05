package com.ccurrin.minecraftservertest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;


public class VarInt
{
    public static int read(DataInputStream in) throws IOException
    {
        int data = 0;
        int shiftOffset = 0;
        int buffer;
        do
        {
            buffer = in.readByte();
            data |= (buffer & 0x7F) << shiftOffset++ * 7;
            if(shiftOffset > 5)
                throw new RuntimeException("VarInt too big");
        } while((buffer & 0x80) == 0x80);
        return data;
    }

    public static void write(DataOutputStream out, int dataToWrite) throws IOException
    {
        while((dataToWrite & 0xFFFFFF80) != 0)
        {
            out.writeByte(dataToWrite & 0x7F | 0x80);
            dataToWrite >>>= 7;
        }
        out.writeByte(dataToWrite);
    }

    public static void writeUTF(DataOutputStream out, String dataToWrite) throws IOException
    {
        VarInt.write(out, dataToWrite.length());
        out.writeBytes(dataToWrite);
    }
}
