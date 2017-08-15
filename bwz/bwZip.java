package bwz;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Write a description of class bzip here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class bwZip
{
    private BurrowsWheeler processB;
    private MoveToFront processM;
    private Huffman processH;
    
    public bwZip()
    {
       processB = new BurrowsWheeler();
       processM = new MoveToFront();
       processH = new Huffman();
    }
    
    /**
     * Reads from a file; compresses them
     * using Huffman codes with an 8-bit alphabet; and writes the results
     * to default filename filename.bz.
     */
    public void compress(String inputFile)
    {
        String outputFile = inputFile + ".bwz";
        
        byte[] bmEncode = processM.encode(processB.encode(inputFile));
        processH.compress(bmEncode, outputFile);
    }
    
    /**
     * Reads from a file; compresses them
     * using Huffman codes with an 8-bit alphabet; and writes the results
     * to user specified filename.
     */
    public void compress(String inputFile, String outputFile)
    {   
        byte[] bmEncode = processM.encode(processB.encode(inputFile));
        processH.compress(bmEncode, outputFile);
    }
    
    /**
     * Reads a sequence of bits that represents a Huffman-compressed message from
     * a binary file; expands them; and writes the results to default output file.
     */
    public void decompress(String inputFile)
    {
        String outputFile = inputFile.substring(0, inputFile.length() - 4);
        
        byte[] hmDecode = processM.decode(processH.expand(inputFile));
        processB.decode(hmDecode, outputFile);
    }
    
     /**
     * Reads a sequence of bits that represents a Huffman-compressed message from
     * a binary file; expands them; and writes the results to a user specified file.
     */
    public void decompress(String inputFile, String outputFile)
    {   
        byte[] hmDecode = processM.decode(processH.expand(inputFile));
        processB.decode(hmDecode, outputFile);
    }
    
    // if args[0] is '-d', apply default decompress for args[1]
    // if args[0] is '-dn' or '-nd', apply decompress for args[1] and save to args[2]
    // if args[0] is a file, apply default compress for args[0]
    // if args[0] is '-n', apply compress for args[1] and save to args[2]
    public static void main(String[] args)
    {
        bwZip bwz = new bwZip();
        Stopwatch timer = new Stopwatch();
        
        if (args[0].equals("--help"))
        {
            StdOut.println("Usage: bwZip [OPTION]... FILE1 [FILE2]");
            StdOut.println("Burrows-Wheeler Data Compression/Cecompression");
            StdOut.println("    -n --new          write result into FILE2");
            StdOut.println("    -d --decompress   upcompress FILE");
        }
        
        else if (args[0].equals("-d")) 
        {
            bwz.decompress(args[1]);
            StdOut.println("Decompress time is " + timer.elapsedTime());
        }
        
        else if (args[0].equals("-n")) 
        {
            bwz.compress(args[1], args[2]);
            StdOut.println("Compress time is " + timer.elapsedTime());
        }
        
        else if (args[0].equals("-dn") ||
                 args[0].equals("-nd")) 
        {
            bwz.decompress(args[1], args[2]);
            StdOut.println("Decompress time is " + timer.elapsedTime());
        }
        
        else 
        {
            bwz.compress(args[0]);
            StdOut.println("Compress time is " + timer.elapsedTime());
        }
    }
}
