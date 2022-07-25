import java.util.*;
import java.util.Vector;
import java.io.*;
// File: RAM
// Author: Finn Robin Schniebel
// Date: 12/6/2021
// Section: 505
// E-mail: frms@tamu.edu 
// Description: all items simulating ram are stored and handled here. the "ram" is a vecor of 8 bits addresses.
//The function can get the full ram, find an item in ram, view the contents in ram, and output it into a file after the initram function is called and executed.
public class RAM {
    private Vector<String> ram = new Vector<String>();

    public Vector<String> getRam() {
        return ram;
    }
    public void setRam(Vector<String> ram) {
        this.ram = ram;
    }

    /** takes in a integer and will return the contents of the location in the vector 
     * if not in range it will return nothing.
    */
    public String findRam(int i){
        if(i < ram.size()){
            return ram.get(i);
        }
        return "";
    }

    /*
        This function takes in a start, an end, and a source that are used to put all items into the class vector ram. 
        The file that this ram comes from can be specified with source and needs to be formated with one hex addres per line in the form "F5".
        The start indecates from what line the program will start reading on and the end will indecate on what line the program will stop reading from.
    */
    public void InitRam(int start, int end, String source){
        try{
            Scanner rfile = new Scanner(new FileReader(source));
            int i = 0;
            String item = "";
            while(rfile.hasNextLine() && i <= end){ //adds all items to the ram for use during program
                item = rfile.nextLine();
                if(i >= start){
                    ram.add(item);
                }
                i++;
            }
            while(ram.size() != 256){
                ram.add("00");
            }
            rfile.close();
        }
        catch(IOException e){
            System.out.println("What did you do? file did not open fatal error for ram !");
        }
        
    }

    /**
     * outputs the conentets of the ram into the terminal:
     * It will state the size of the memory and then have 8 blocks of 8 bit ram per line with the starting hex line number infromt of it.
     */
    public void viewRam(){
        String groupedMemSet ="";
        String memsetInd ="";
        System.out.println("memory_size:" + ram.size());
        System.out.println("memory_content:");

        for(int i =0; i < ram.size(); i+=8){ //go through the ram in step of 8
            groupedMemSet = "";
            if(i >= ram.size()){ //edge case 
                break;
            }
            for(int j =0; j < 8; j++){ //in each of the steps put all 8 elements into a string seperated by spaces
                if(j+i < ram.size()){ //edge case for if it is not multiple of 8
                    groupedMemSet += ram.get(i+j);
                    if(j != 7){ //spaces
                        groupedMemSet += " ";
                    }
                }
                
            }
            memsetInd = Converter.IntToHex(i);
            if(memsetInd.length() ==1){
                memsetInd = "0"+ memsetInd;
            }
            System.out.println("0x"+ memsetInd + ":" + groupedMemSet);
        }
    }
    /**
     * Places the contents of memory into a text file called memory-dump.txt.
     * Each line will contain one block or 8 bits.
     */
    public void memorydump(){
        try{
            File cachedumpfile = new File("ram.txt");
            cachedumpfile.createNewFile();
            PrintWriter outf = new PrintWriter("ram.txt");
            for(int i =0; i < ram.size(); i++){
                outf.println(ram.get(i));
            }
            outf.close();
        }
        catch(IOException e){
            System.out.println("error in memoryump. could not create or open file, check folder permissions.");
        }
        
    }
}


