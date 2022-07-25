To compile the program:

  javac cachesimulator.java 
or 
  javac *.java

in the command line while in the project folder.

RUN:
java cachesimulator (input .txt path in quatation marks)

ex: java cachesimulator "input.txt"

Input format:
1. input: "init-ram 0x00 [to a desired hex number up to 0xFF]"
2. Enter enter the values and policies you want to use you want to be using for the perameters stated
	Replacement polcies: 1: random, 2: least recently, 3: least used
	Write Hit policies: 1: write-through, 2: write-back
	Wrtie miss policy: 1 write-allocate 2: no write-allocate
3. when in the Menu use comands such as "cache-read" followed by the hex location you want to access. Information can also be dumped into txt files
	in the file location of the project for testing and verification of correct actions.