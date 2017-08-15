package bwz;

import java.util.LinkedList;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.BinaryIn;
import edu.princeton.cs.algs4.BinaryOut;
import java.io.ByteArrayOutputStream; 
import java.io.ByteArrayInputStream;

/**
 * MoveToFront encoding/decoding for unicode
 * @author Sean
 * @version 0.0
 */
public class MoveToFront
{
    public MoveToFront(){}
    
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode()
    {
        LinkedList<Character> alphabet = new LinkedList<>();
        for (int i = 0; i < 65535; i++) alphabet.add((char)i);
        
        String s = BinaryStdIn.readString();
        for (int i = 0; i < s.length(); i++)
        {
            int idx = alphabet.indexOf(s.charAt(i));
            BinaryStdOut.write((char)idx);
            alphabet.addFirst(alphabet.remove(idx));
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode()
    {
        LinkedList<Character> alphabet = new LinkedList<>();
        for (int i = 0; i < 65535; i++) alphabet.add((char)i);
        
        String s = BinaryStdIn.readString();
        for (int i = 0; i < s.length(); i++)
        {
            int idx = Integer.valueOf(s.charAt(i));
            char c = alphabet.remove(idx);
            BinaryStdOut.write(c);
            alphabet.addFirst(c);
        }
        BinaryStdOut.close();
    }
    
    // apply move-to-front encoding and save into a byte array
    public static byte[] encode(byte[] b)
    {
        LinkedList<Character> alphabet = new LinkedList<>();
        
        ByteArrayInputStream inStream = new ByteArrayInputStream(b);
        BinaryIn input = new BinaryIn(inStream);
        String s = input.readString();
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BinaryOut output = new BinaryOut(outStream);
        
        for (int i = 0; i < 65535; i++) alphabet.add((char)i);
        
        for (int i = 0; i < s.length(); i++)
        {
            int idx = alphabet.indexOf(s.charAt(i));
            output.write((char)idx);
            alphabet.addFirst(alphabet.remove(idx));
        }
        output.close();
        
        return outStream.toByteArray();
    }

    // apply move-to-front decoding and save into a byte array
    public static byte[] decode(byte[] b)
    {
        LinkedList<Character> alphabet = new LinkedList<>();
        
        ByteArrayInputStream inStream = new ByteArrayInputStream(b);
        BinaryIn input = new BinaryIn(inStream);
        String s = input.readString();
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BinaryOut output = new BinaryOut(outStream);
        
        for (int i = 0; i < 65535; i++) alphabet.add((char)i);
        
        for (int i = 0; i < s.length(); i++)
        {
            int idx = Integer.valueOf(s.charAt(i));
            char c = alphabet.remove(idx);
            output.write(c);
            alphabet.addFirst(c);
        }
        output.close();
        
        return outStream.toByteArray();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args)
    {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
    }
}
