import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

/**
  * Use MSD and switch to insertion sort when there are less then 
  * 15 items to sort.
  * cannot handle unicode since count[65535] use too much memory. 
  * Run time on dickens256.txt (no unicode char version) is 15.5s
  * @sean
  * @0.0
  */

public class CircularSuffixArray 
{
    private int len;
    private final int R = 256;
    private int[] index;
    
    // circular suffix array of s
    public CircularSuffixArray(String s)
    {
        this.len = s.length();
        index = new int[len];
        
        // generate original index array
        for (int i = 0; i < len; i++) index[i] = i;
        
        int[] partialSort = new int[len];
        sort(s, partialSort, 0, len - 1, 0);    
    }
    
    // sort index array
    private void sort(String s, int[] partialSort, int lo, int hi, int d)
    {
        // base condition
        if (hi <= lo) return;
        
        // insertion sort when hi - low is small
        if (hi - lo <= 15)
        {
            for (int i = lo; i <= hi; i++)
                for (int j = i; j > lo && isLess(s, index[j], index[j-1], d); j--)
                     exchange(j, j - 1);
            return;
        }
        
        // MSD sort
        int[] count = new int[R+2];
     
        // count the frequency
        for (int i = lo; i <= hi; i++) 
        {
            int idx = charAt(s, index[i], d) + 2;
            count[idx]++;
        }
        
        // compute the cummulates
        for (int r = 0; r < R + 1; r++) count[r+1] += count[r];
        
        // move items
        for (int i = lo; i <= hi; i++)
        {
            int idx = charAt(s, index[i], d) + 1;
            partialSort[count[idx]++] = index[i];
        }
        
        // copy partialSort back to index
        for (int i = lo; i <= hi; i++) index[i] = partialSort[i - lo];
        
        // sort R subarrays recursively
        for (int r = 0; r < R; r++)
        {
            int low = lo + count[r];
            int hgh = lo + count[r+1] - 1;
            sort(s, partialSort, low, hgh, d + 1);
        }
    }
    
    // length of s
    public int length() {return len;}
    
    // returns index of ith sorted suffix
    public int index(int i) {return index[i];}
    
    // custom charAt to return chat in a virtual array at row r and col d
    /* a virtual unsorted circular suffix array is as below:
     * 
     * row/col 0 1 2 3 4 5
     *   0     b a n a n a
     *   1     a n a n a b
     *   2     n a n[a]b a
     *   3     a n a b a n
     *   4     n a b a n a
     *   5     a b a n a n
     *   
     * e.g. for row 2 col 3, char is 'a', charAt("banana", 2, 3) is actually 
     * start from third letter of string and go 3 to the right.
     */ 
    private int charAt(String s, int r, int d)
    {
        return s.charAt((r + d) % len); 
    }
    
    // check if virtual string at row v is less than the one in row w
    // both virtual strings start from d for comparison
    private boolean isLess(String s, int v, int w, int d)
    {
        for (int i = d; i < s.length(); i++)
        {
            if (charAt(s, v, i) > charAt(s, w, i)) return false;
            if (charAt(s, v, i) < charAt(s, w, i)) return true;
        }
        return false;
    }
    
    // exchange
    private void exchange(int i, int j)
    {
        int temp = index[i];
        index[i] = index[j];
        index[j] = temp;
    }
    
    // unit testing of the methods
    public static void main(String[] args)
    {
        Stopwatch timer = new Stopwatch();
        In input = new In(args[0]);
        String s = input.readAll();
        
        int len = s.length();
        CircularSuffixArray test = new CircularSuffixArray(s);
        
        /*
        for (int i = 0; i < len; i++) 
        {
            int idx = test.index(i);
            for (int j = idx, cnt = 0; cnt < len; j = (j+1) % len, cnt++)
                StdOut.print(s.charAt(j));
            StdOut.println(" " + idx);
        }
        */
       StdOut.println("Run time is " + timer.elapsedTime());
    }
}