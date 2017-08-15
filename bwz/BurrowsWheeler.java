package bwz;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut; 
import edu.princeton.cs.algs4.BinaryIn;
import edu.princeton.cs.algs4.BinaryOut;
import java.io.ByteArrayOutputStream; 
import java.io.ByteArrayInputStream;

/**
 * Burrow-Wheeler encode and decode.
 * 
 * @author Sean
 * @version 0.0
 */
public class BurrowsWheeler 
{   
    public BurrowsWheeler(){};
    
    // apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
    public static void encode()
    {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csArray = new CircularSuffixArray(s);
        StringBuilder res = new StringBuilder();
        int len = csArray.length();
        
        for (int i = 0; i < len; i++)
        {
            int idx = csArray.index(i); 
            res.append(s.charAt((idx - 1 + len) % len));
            if (idx == 0) BinaryStdOut.write(i);
        }
        BinaryStdOut.write(res.toString());
        BinaryStdOut.close();
    }
    
    // apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
    public static void decode()
    {
        int first = BinaryStdIn.readInt();
        final int R = 65535;
        String s = BinaryStdIn.readString();
        
        //char[] c = s.toCharArray();
        int len = s.length();
        
        // apply MSD once to obtain sequence of next
        int[] next = new int[len];
        int[] count = new int[R+1];
        
        for (int i = 0; i < len; i++) count[s.charAt(i) + 1]++;
        for (int r = 0; r < R; r++) count[r+1] += count[r];
        for (int i = 0; i < len; i++) next[count[s.charAt(i)]++] = i; 
        
        // construct original string follow next sequence
        int i = 0, idx = first;
        while (i < len)
        {
            idx = next[idx];
            BinaryStdOut.write(s.charAt(idx));
            i++;
        }
        BinaryStdOut.close();
    }
    
    // apply Burrows-Wheeler encoding and output into a String
    public static byte[] encode(String inputFile)
    {
        BinaryIn input = new BinaryIn(inputFile);
        String s = input.readString();
        
        CircularSuffixArray csArray = new CircularSuffixArray(s);
        StringBuilder res = new StringBuilder();
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BinaryOut output = new BinaryOut(outStream);
        
        int len = csArray.length();
        for (int i = 0; i < len; i++)
        {
            int idx = csArray.index(i); 
            res.append(s.charAt((idx - 1 + len) % len));
            if (idx == 0) output.write(i);
        }
        output.write(res.toString());
        output.close();
        
        return outStream.toByteArray();
    }
    
    // apply Burrows-Wheeler decoding and output into a file
    public static void decode(byte[] b, String outputFile)
    {
        ByteArrayInputStream inStream = new ByteArrayInputStream(b);
        BinaryIn input = new BinaryIn(inStream);
        
        final int R = 65535;
        int first = input.readInt();
        String s = input.readString();
        
        BinaryOut output = new BinaryOut(outputFile);
        
        int len = s.length();
        
        // apply MSD once to obtain sequence of next
        int[] next = new int[len];
        int[] count = new int[R+1];
        
        for (int i = 0; i < len; i++) count[s.charAt(i) + 1]++;
        for (int r = 0; r < R; r++) count[r+1] += count[r];
        for (int i = 0; i < len; i++) next[count[s.charAt(i)]++] = i; 
        
        // construct original string follow next sequence
        int i = 0, idx = first;
        while (i < len)
        {
            idx = next[idx];
            output.write(s.charAt(idx));
            i++;
        }
        output.close();
        //return outStream.toString();
    }

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args)
    {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
    }
}
