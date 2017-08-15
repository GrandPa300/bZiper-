import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut; 

/**
 * Burrow-Wheeler encode and decode.
 * 
 * @author Sean
 * @version 0.0
 */
public class BurrowsWheeler 
{   
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

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args)
    {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
    }
}