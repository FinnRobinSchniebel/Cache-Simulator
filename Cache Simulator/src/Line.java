import java.util.*;

// File: Line
// Author: Finn Robin Schniebel
// Date: 12/6/2021
// Section: 505
// E-mail: frms@tamu.edu 
// Description: This class is a object that is called by cache and is used to initilize, store, and edit the individual lines in the cache. 
// They are stored in a vecor in cache.
public class Line {
    private int Valid = 0;
    private int Dirty = 0;
    private String Tag = "";
    private Vector<String> Blocks = new Vector<String>();//contains the blocks byte accessible 
    
    private int Usage = 0; //counts how often it is used
    private int realLoc=0; //saves the real location of the element for least used 

    Line(int B){    
        for(int i =0; i < B; i++){
            Blocks.add(new String("")); //create cold cache line
            
        }

    }
    /**
     * basic setters and getters for the varias parts of the line.
     * @param blocks
     */
    public void setBlocks(Vector<String> blocks) {
        Blocks = blocks;
    }
    public void setDirty(int dirty) {
        Dirty = dirty;
    }
    public void setTag(String tag) {
        Tag = tag;
    }
    public Vector<String> getBlocks() {
        return Blocks;
    }
    public int getDirty() {
        return Dirty;
    }
    public String getTag() {
        return Tag;
    }
    public int getValid() {
        return Valid;
    }
    public void setValid(int valid) {
        Valid = valid;
    }
    public int getUsage() {
        return Usage;
    }
    public void setUsage(int usage) {
        Usage = usage;
    }
    public void setRealLoc(int realLoc) {
        this.realLoc = realLoc;
    }
    public int getRealLoc() {
        return realLoc;
    }

    /**
     * adds one to the usage counter when called (used for replacement policy)
     */
    public void incermentUsage(){
        Usage++;
    }
    /**
     * Lets the program replace a block of memory using:
     * @param i the integer index in the vector 
     * @param item the string hexadecimal 8 bit that will be writen in.
     */
    public void replaceBlock(int i , String item){
        Blocks.set(i, item);
    }

    /**
     * replace all parts of the line. de-active: not used and commented out.
     * @param valid valid bit int
     * @param dirty dirty bit int
     * @param tag hex line tag 
     * @param Blocks blocks in line
     * @param usage 
     */
    /*public void replaceAll(int valid, int dirty, String tag, Vector<String> Blocks, int usage){
        Valid = valid;
        Dirty = dirty;
        Tag =tag;
        this.Blocks = Blocks;
        Usage = usage; 
    }*/

    /**
     * resets the cache line to its cold state.
     */
    public void reset(){
        Valid = 0;
        Dirty = 0;
        Tag ="";
        for(int i =0; i < Blocks.size(); i++){
            Blocks.set(i, "");
        }
        Usage = 0; 
    }
    /**
     * converts the lines content into a string in the form: valid bit dirty-bit tag blocks
     */
    public String toString(){
        String tagreturn = Tag;
        if(Tag.equals("")){
            tagreturn = "00";
        }
        String blockString = "";
        for(int i = 0; i < Blocks.size(); i++){
            if(Blocks.get(i).equals("")){
                blockString += "00";
            }
            else{
                blockString += Blocks.get(i);
            }
            if(i != Blocks.size() -1){
                blockString += " ";
            }
        }
        return Valid+ " "+Dirty+" "+tagreturn+" " + blockString;
    }
    /**
     * converts the blocks of the line into a single string for output in a text file.
     * @return strings of blocks
     */
    public String fileout(){
        String blockString = "";
        for(int i = 0; i < Blocks.size(); i++){
            if(Blocks.get(i).equals("")){
                blockString += "00";
            }
            else{
                blockString += Blocks.get(i);
            }
            if(i != Blocks.size() -1){
                blockString += " ";
            }
        }
        return blockString;
    }
}
