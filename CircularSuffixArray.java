import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

/**
  * Use a combination of MSD, 3-way quicksort and insertion sort.
  * MSD sort for first 2 MSD
  * 3-way quick sort for more than 15 items.
  * insertion sort for less than 15 items. (not applied after testing)
  * 
  * Since MSD is only applied twice, this version supports unicode txt file. 
  * Run time on dickens.txt is 8.26s
  * @sean
  * @1.0
  */

public class CircularSuffixArray 
{
    private int len;
    // work for unicode
    private final int R = 65535; 
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
    
    // sort index array using a combination of       
    //       MSD: sort all the rows 2 times based on first 2 MSD
    // quicksort: to sort more than 15 rows
    // insertion: to sort less than 15 rows (disabled after test)
    private void sort(String s, int[] partialSort, int lo, int hi, int d)
    {
        // base condition
        if (hi <= lo) return;
        
        // switch to 3-way quick sort after 1st char
        if (d > 1) 
        {
            quickSort(s, lo, hi, d);
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
    
    // 3-way radix quicksort
    private void quickSort(String s, int lo, int hi, int d)
    {
        // base condition
        if (hi <= lo) return;
        
        /*
        // switch to insertion sort
        if (hi - lo <= 15) 
        {
            insertSort(s, lo, hi, d);
            return;
        }
        */
        

        int left = lo, rght = hi;        
        // set first char as partition item
        int v = charAt(s, index[lo], d); 
        
        int i = lo + 1;
        while (i <= rght)
        {
            int t = charAt(s, index[i], d);
            if (t < v) exchange(left++, i++);
            if (t > v) exchange(i, rght--);
            else i++;
        }
        
        // recursion on each part
        quickSort(s, lo, left - 1, d); // sort left part
        quickSort(s, left, rght, d+1); // sort middle for next char
        quickSort(s, rght + 1, hi, d); // sort right part
    }
    
    // insertion sort
    private void insertSort(String s, int lo, int hi, int d)
    {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && isLess(s, index[j], index[j-1], d); j--)
                 exchange(j, j - 1);
        return;
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
    private int charAt(String s, int r, int d) { return s.charAt((r + d) % len); }
    
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
    
    // exchange element in index array
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
        
        for (int i = 0; i < len; i++) 
        {
            int idx = test.index(i);
            for (int j = idx, cnt = 0; cnt < len; j = (j+1) % len, cnt++)
                StdOut.print(s.charAt(j));
            StdOut.println(" " + idx);
        }
        
       StdOut.println("Run time is " + timer.elapsedTime());
    }
}