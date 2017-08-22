## bZiper! File Compressor/De-compressor Summary:

### Introduction
* This project focus on writing a program to compress/decompress any file. The program is based on Burrows-Wheeler algorithm and Huffman algorithm. 
* This project is developed based on algs4, please check [Instruction](http://coursera.cs.princeton.edu/algs4/assignments/burrows.html) and [Check List](http://coursera.cs.princeton.edu/algs4/checklists/burrows.html) for more details. 
* Command line mode and GUI which support drag-and-drop are developed as an extension of this project. 

<div align=center>
	<img src="http://i.imgur.com/63gY8WA.png" width="50%" />
</div>

<div align=center>
	<img src="http://i.imgur.com/ffoz9Lk.png" />
</div>

* bZiper! support: 
	* Both __drag-and-drop__ and __file chooser__ to input file.
	* __Auto-detect__ file zip/unzip, and preserve (auto-correction) original file extension while saving. 


<div align=center>
	<img src="http://i.imgur.com/EvZS1K1.png" width="40%"  />
</div>

* Text compression result are shown as below: 

<div align=center>
	<img src="http://i.imgur.com/rhrK5Js.png" />
</div>

#### Implementation
1.  _Burrows-Wheeler transform._ Transform input into a sequence where same character occur near each other many times.
2. _Move-to-front encoding._ Based on result of 1, convert it into a text file in which certain characters appear more frequently than others.
3. _Huffman compression._  compress result of 2, encoding frequently occurring characters with short codewords and rare ones with long codewords.

#### Optimization log
* __Circular Suffix Array__ uses MSD and swap to  insertion sort if the input length is below 15, it cannot handle Unicode in this version.
* __Circular Suffix Array__  is updated to 
	* use MSD for first 2 MSD, 
	* then switch to 3-way quick sort for the rest, 
	* use insertion sort if there are less than 15 items. 
	* This version supports Unicode with array length 65535.
