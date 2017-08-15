package bwz;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

/**
 * To validate if input txt file contains non 256 ASCII characters
 * 
 * @author Sean
 * @version 0.1
 */
public class Validation
{
    private int R;

    /**
     * Constructor for objects of class ASCIIValidate
     */
    public Validation(int R)
    {
        this.R = R - 1;
    }
    
    public int IllegalCharAt(String s)
    {
        for (int i = 0; i < s.length(); i++)
            if ((int)s.charAt(i) > R) return i;
        return -1;    
    }
    
    public String replaceUnicodeChar(char c, String s)
    {
        int idx = IllegalCharAt(s);
        if (idx != -1)
        {
            if (idx == 0)  return c + s.substring(idx+1);
            if (idx == s.length()) return s.substring(0, idx - 1) + c;
            return s.substring(0, idx-1) + c + s.substring(idx+1);
        }
        return s;
    }
    
    public static void main(String[] args)
    {
        Validation v = new Validation(256);
        In input = new In(args[0]);
        
        /*
        // print out illegal lines
        int lineNum = 1;
        while (input.hasNextLine())
        {
            String s = input.readLine();
            if (v.IllegalCharAt(s) != -1) StdOut.printf("%d %s \n", lineNum, s);
            lineNum++;
        }
        */
       
        
        while (input.hasNextLine())
        {
            String s = input.readLine();
            String r = v.replaceUnicodeChar(' ', s);
            StdOut.println(r);
        }
        
    }
}
