/******************************************************************************
 *  Compilation:  javac Huffman.java
 *  Execution:    java Huffman - < input.txt   (compress)
 *  Execution:    java Huffman + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   http://algs4.cs.princeton.edu/55compression/abra.txt
 *                http://algs4.cs.princeton.edu/55compression/tinytinyTale.txt
 *                http://algs4.cs.princeton.edu/55compression/medTale.txt
 *                http://algs4.cs.princeton.edu/55compression/tale.txt
 *
 *  Compress or expand a binary input stream using the Huffman algorithm.
 *
 *  % java Huffman - < abra.txt | java BinaryDump 60
 *  010100000100101000100010010000110100001101010100101010000100
 *  000000000000000000000000000110001111100101101000111110010100
 *  120 bits
 *
 *  % java Huffman - < abra.txt | java Huffman +
 *  ABRACADABRA!
 *
 ******************************************************************************/

/**
 *  The {@code Huffman} class provides static methods for compressing
 *  and expanding a binary input using Huffman codes over the 8-bit extended
 *  ASCII alphabet.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/55compress">Section 5.5</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *  
 *  Writing compression result directly into a file, and return expand result as a byte arry
 *  for further post-processing. Modified by Sean Wang.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Sean Wang
 */
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.BinaryIn;
import edu.princeton.cs.algs4.BinaryOut;
import java.io.ByteArrayInputStream; 
import java.io.ByteArrayOutputStream; 

public class Huffman {

    // alphabet size of extended ASCII
    private static final int R = 256;

    // Do not instantiate.
    public Huffman() { }

    // Huffman trie node
    private static class Node implements Comparable<Node> {
        private final char ch;
        private final int freq;
        private final Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch    = ch;
            this.freq  = freq;
            this.left  = left;
            this.right = right;
        }

        // is the node a leaf node?
        private boolean isLeaf() {
            assert ((left == null) && (right == null)) || ((left != null) && (right != null));
            return (left == null) && (right == null);
        }

        // compare, based on frequency
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }
    
    /**
     * Reads a sequence of 8-bit bytes from a byte array; compresses them
     * using Huffman codes with an 8-bit alphabet; and writes the results
     * to a file.
     */
    public static void compress(byte[] b, String file) 
    {
        ByteArrayInputStream inStream = new ByteArrayInputStream(b);
        BinaryIn in = new BinaryIn(inStream);
        
        String s = in.readString();
        char[] input = s.toCharArray();
        
        BinaryOut output = new BinaryOut(file);
        
        // tabulate frequency counts
        int[] freq = new int[R];
        for (int i = 0; i < input.length; i++)
            freq[input[i]]++;

        // build Huffman trie
        Node root = buildTrie(freq);

        // build code table
        String[] st = new String[R];
        buildCode(st, root, "");

        // print trie for decoder
        writeTrie(root, output);
        
        // print number of bytes in original uncompressed message
        output.write(input.length);
        
        // use Huffman code to encode input
        for (int i = 0; i < input.length; i++) {
            String code = st[input[i]];
            for (int j = 0; j < code.length(); j++) {
                if (code.charAt(j) == '0') {
                    output.write(false);
                }
                else if (code.charAt(j) == '1') {
                    output.write(true);
                }
                else throw new IllegalStateException("Illegal state");
            }
        }
        
        // close output stream
        output.close();
    }

    // build the Huffman trie given frequencies
    private static Node buildTrie(int[] freq) {

        // initialze priority queue with singleton trees
        MinPQ<Node> pq = new MinPQ<Node>();
        for (char i = 0; i < R; i++)
            if (freq[i] > 0)
                pq.insert(new Node(i, freq[i], null, null));

        // special case in case there is only one character with a nonzero frequency
        if (pq.size() == 1) {
            if (freq['\0'] == 0) pq.insert(new Node('\0', 0, null, null));
            else                 pq.insert(new Node('\1', 0, null, null));
        }

        // merge two smallest trees
        while (pq.size() > 1) {
            Node left  = pq.delMin();
            Node right = pq.delMin();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.insert(parent);
        }
        return pq.delMin();
    }
    
    // write bitstring-encoded trie to binary output
    private static void writeTrie(Node x, BinaryOut bOut) {
        if (x.isLeaf()) {
            bOut.write(true);
            bOut.write(x.ch, 8);
            return;
        }
        bOut.write(false);
        writeTrie(x.left, bOut);
        writeTrie(x.right, bOut);
    }

    // make a lookup table from symbols and their encodings
    private static void buildCode(String[] st, Node x, String s) {
        if (!x.isLeaf()) {
            buildCode(st, x.left,  s + '0');
            buildCode(st, x.right, s + '1');
        }
        else {
            st[x.ch] = s;
        }
    }

    /**
     * Reads a sequence of bits that represents a Huffman-compressed message from
     * a binary file; expands them; and writes the results to a byte array.
     */
    public static byte[] expand(String inputFile) 
    {
        BinaryIn input = new BinaryIn(inputFile);
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BinaryOut output = new BinaryOut(outStream);
        
        // read in Huffman trie from input stream
        Node root = readTrie(input); 

        // number of bytes to write
        int length = input.readInt();

        // decode using the Huffman trie
        for (int i = 0; i < length; i++) {
            Node x = root;
            while (!x.isLeaf()) {
                boolean bit = input.readBoolean();
                if (bit) x = x.right;
                else     x = x.left;
            }
            output.write(x.ch, 8);
        }
        output.close();
        return outStream.toByteArray();
    }
    
    private static Node readTrie(BinaryIn input) {
        boolean isLeaf = input.readBoolean();
        if (isLeaf) {
            return new Node(input.readChar(), -1, null, null);
        }
        else {
            return new Node('\0', -1, readTrie(input), readTrie(input));
        }
    }
}