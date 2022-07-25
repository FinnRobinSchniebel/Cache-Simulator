import java.io.*;
import java.util.*;

// File: cache
// Author: Finn Robin Schniebel
// Date: 12/6/2021
// Section: 505
// E-mail: frms@tamu.edu 
// Description: This class handals all cache related functinos and stores the cache.



public class Cache {
    private Vector<Vector<Line>> CacheSets = new Vector<Vector<Line>>(); //stores all cache sets
    private Vector<Vector<Line>> LastSets = new Vector<Vector<Line>>(); //keeps track as a MPQ of what was used last. Edit: Yes I can make it more efficent but it takes to long to change to complete on time
    private RAM r = null;

    private int E = 0;
    private int B = 0;
    private int C= 0;

    private int m = 0;
    private int s =0;
    private int b =0;
    private int t = 0;


    private int RP = 0;
    private int WH = 0;
    private int WM = 0;
    

    int numCacheHits = 0;
    int numCacheMiss = 0;
    /**
     * This function constructs the cache and initilizes all needed variables.
     * To create a Cache you will neet to give:
     * @param C int, is the Cache Size in Bytes
     * @param B int, is the Block size in bytes
     * @param E int, is the Number of lines per Set
     * @param M int, the number of unique addresses possible
     * @param r takes in a RAM object for linking the cache to ram
     * @param rp is the user selected replacment policy (1: random, 2: least recently, 3: least used)
     * @param wh is an integer that represents the write hit policy entered by the user (1: write-through, 2: write-back)
     * @param wm is an intger that represents the write miss policy entered by the user (1 write-allocate 2: no write-allocate)
     */
    Cache(int C, int B, int E, int M, RAM r, int rp, int wh, int wm){
        int S = C/(B*E); //find value of S
        if(S == 0){
            System.out.println("Fatal error, resulting cache does not work. No space for cache bits!");
            System.exit(0);
        }

        s = (int)Math.ceil(Math.log(S)/Math.log(2)); //get log 2 of S to get s
        b = (int)Math.ceil(Math.log(B)/Math.log(2)); //get log2 of B to get b
        m = (int)Math.ceil(Math.log(M)/Math.log(2)); //get log2 of B to get b
        t = m-(b+s);

        this.r =r;
        this.E =E;
        this.B =B;
        this.C = C;

        RP = rp;
        WH = wh;
        WM = wm;


        for(int i = 0; i< S; i++){ //create the number of needed sets
            Vector<Line> newset = new Vector<Line>();
            Vector<Line> lastloc = new Vector<Line>();
            for(int j = 0; j < E; j++){//create the number of needed lines per set
                Line newline = new Line(B); //creates new line in set here
                newline.setRealLoc(j);
                newset.add(newline);
                lastloc.add(0, newline);
            }
            CacheSets.add(newset);
            LastSets.add(lastloc);
        }
    }

    /**
     * This function will access the Cache to find the location specified. It will output what if found in the consol. 
     * If it does not find anything (miss) it will load the address from RAM into a location specified by the replacment policy input at the start of the program.
     * Output format:
     * Line 1: set:<set number in decimal>
     * Line 2: tag:<tag number in hex>
     * Line 3: hit:<cache hit? yes|no>
     * Line 4: eviction_line: <line to evict in decimal. If cache hit, -1>
     * Line 5: ram_address:<ram address to read in hexadecimal. If cache hit, -1>
     * Line 6: data:<data in hexadecimal>
     * 
     * @param hex string, takes in a hexadecimal number as the address
     */
    public void read(String hex){
        String Address = Converter.HextoBin(hex);
        if(m < Address.length()){  
            System.out.println("Unexpected error, Address is longer then needed address bits");
            return;
        }
        while(m> Address.length()){ //makes sure the address is the correct length for sepperating bits
            Address= "0" + Address; 
        }
        String tagbits = Address.substring(0, t);
        String taghex = Converter.BintoHex(tagbits);

        String setindex = Address.substring(t, s+t);
        String blockoffset = Address.substring(t+s, m);
        //System.out.println("T/S/B: " + tagbits + " "+ setindex+ " " + blockoffset);

        int setnumber = 0;
        if(!setindex.equals("")){
            setnumber = Converter.BintoDec(setindex);
        }
        
        //line 1
        System.out.println("set:" + setnumber);
        //line 2
        System.out.println("tag:" + taghex);

        Vector<Line> setloc = CacheSets.get(setnumber);

        Vector<Line> lastloc = LastSets.get(setnumber);

        Line found = null;
        for(int i =0; i < setloc.size(); i++){
            found = setloc.get(i);
            if(found.getTag().equals(taghex) && found.getValid()==1){ //if tag matches and the location is valid its a hit
                numCacheHits++;
                //line 3-6
                System.out.println("Hit:yes");
                System.out.println("evicted_line:-1");
                System.out.println("ram_address:-1");
                System.out.println("Data:0x" + found.getBlocks().get(Converter.BintoDec(blockoffset)));
                found.incermentUsage(); //item was used so incerment it
                if(RP ==2 && lastloc.indexOf(found) !=0 ){ //if it is a least recent used and the line is not first in the MPQ
                    //move the item to the front of queue
                    //System.out.println("Where in queue: "+lastloc.indexOf(found));
                    Line move = lastloc.remove(lastloc.indexOf(found));
                    lastloc.add(0, move);
                }
                return; //done
            }
        }
        //if nothing was found:
        numCacheMiss++;
        String find = "";

        System.out.println("Hit:no");
        System.out.println("ram_address:" + hex);
        int open = -1;
        
        for(int i =0; i < setloc.size(); i++){
            if(setloc.get(i).getBlocks().get(0).equals("")){ //if you find an empty block
                open = i;
                break;
            }
        }
         
        if(open != -1){ //fill empty first 
            //System.out.println(blockoffset);
            find = replace(Converter.HexToInt(hex), setloc.get(open), taghex, Converter.BintoDec(blockoffset), setnumber);
            if(RP == 2){
                Line move = lastloc.remove(E-1);
                lastloc.add(0, move);
            }
            System.out.println("evicted_line:" + open); //give what line was replaced
        }
        //check replacment policy if full
        else if(RP == 1){ //if random is on always random select a line to replace
            find = RandomReplace(Converter.HexToInt(hex), setloc, taghex, Converter.BintoDec(blockoffset), setnumber);
        }
        else if(RP == 2){
            find = LeastRecent(Converter.HexToInt(hex), setloc, taghex, Converter.BintoDec(blockoffset), lastloc, setnumber);
        }
        else if(RP ==3){
            find = LeastFrequent(Converter.HexToInt(hex), setloc, taghex, Converter.BintoDec(blockoffset), setnumber);
        }
        System.out.println("data:" + find);
        //System.out.println("Last loc list: "+lastloc);
    }   

    /**
     * Helper function private: RandomReplace will determin what line should be replaced in the set
     * @param Address int location in memory 
     * @param set set vector of Lines that is the selected set in which replacement occurse
     * @param tag string, tag of the new item from memory
     * @param blockOffset int, the location of the block that is being read in cache
     * @param setnum int, is the index of the set being changed
     * @return a string of the block that was requested
     */
    private String RandomReplace(int Address, Vector<Line> set, String tag, int blockOffset, int setnum){
        Random ran = new Random();
        int repline = ran.nextInt(E); //random number between 0 and size of lines in set
        System.out.println("evicted_line:" + repline); //give what line was replaced


        String data = replace(Address, set.get(repline), tag, blockOffset, setnum); //does the actual replacment
        return data;
    }
    /**
     * Helper function private: LeastRecent will determin what line should be replaced in the set
     * @param Address int location in memory 
     * @param set set vector of Lines that is the selected set in which replacement occurse
     * @param tag string, tag of the new item from memory
     * @param blockOffset int, the location of the block that is being read in cache
     * @param setnum int, is the index of the set being changed
     * @return a string of the block that was requested
     */
    private String LeastRecent(int Address, Vector<Line> set, String tag, int blockOffset, Vector<Line> least, int setnum){
        //find item that needs replacement and move it to front of MPQ
        int repline =E-1; //get the last element in list
        Line move = least.remove(repline);
        least.add(0, move); //set to front of MPQ

        int realLoc = move.getRealLoc();
        System.out.println("evicted_line:" + realLoc); //give what line was replaced

        String data = replace(Address, set.get(realLoc), tag, blockOffset, setnum); //does the actual replacment
        
        return data;

    } 

    /**
     * Helper function private: LeastFrequent will determin what line should be replaced in the set
     * @param Address int location in memory 
     * @param set set vector of Lines that is the selected set in which replacement occurse
     * @param tag string, tag of the new item from memory
     * @param blockOffset int, the location of the block that is being read in cache
     * @param setnum int, is the index of the set being changed
     * @return a string of the block that was requested
     */
    private String LeastFrequent(int Address, Vector<Line> set, String tag, int blockOffset, int setnum){
        int repline =0; //get the last element in list
        for(int i =0; i< set.size();i++){
            if(set.get(i).getUsage() < set.get(repline).getUsage()){
                repline =i;
            }
        }

        System.out.println("evicted_line:" + repline); //give what line was replaced
        String data = replace(Address, set.get(repline), tag, blockOffset, setnum); //does the actual replacment        
        return data;
    }


    /**
     * Helper class: replace will replace the line selected with the contents of the address selected and its sorounding content 
     * @param Address is the address of the item that will take the current ones place
     * @param line is the line that will be replaced
     * @param tag the string tag of the new item being added
     * @param blockOffset the block ofset of the item that is being requested
     * @param setnum is the location in cache of the set that is changed
     * @return a string of the block that was requested
     */
    private String replace(int Address, Line line, String tag, int blockOffset, int setnum){

        //dirty write start
        if(!line.getTag().equals("") && line.getDirty() == 1){ //if the line is not cold and the dirty bit is set
            String blockbits ="";
            for(int f=0; f<b; f++){ //add all the block bits as 0's to fill end of address
                blockbits += "0";   }
            String setdex = Integer.toBinaryString(setnum);
            if(s == 0){//in case there is only 1 set
                setdex = "";
            }
            else if(setdex.length() != 2){ //getting the spacing right so 1 = 0x01
                setdex = "0"+ setdex;   }
            String stringAddress = Converter.HextoBin(line.getTag()) +""+ setdex + blockbits; // tag bits + set bits + start of block
                //System.out.println(Converter.HextoBin(CacheSets.get(i).get(j).getTag()) +" "+ Integer.toBinaryString(i) +" "+ blockbits);
            dirtToRam(Converter.BintoDec(stringAddress), line); //write dirty bit to Ram
        }
        //dirty write end

        String item = "";
        String requested = "none";
        for(int i = 0; i < B; i++){
            item = r.findRam(Address- blockOffset +i); //get item from ram to match layout of cache line
            line.replaceBlock(i, item);
            if(i == blockOffset){
                requested = item; //save the item for return if it is the one you are looking for
            }
            
        }
        //set the first bits
        line.setDirty(0);
        line.setTag(tag);
        line.setValid(1);
        line.setUsage(1);
        

        return requested;
    }   

    /**
     * moves the dirty line of ram back into the memory to not loss the changes.
     * @param Address int, location in memory of the original line that is being replaced.
     * @param line the line object that needs to be placed back in ram.
     */
    private void dirtToRam(int Address, Line line){
        if(line.getDirty() == 1 && line.getValid() == 1){
            //System.out.println("Debug:" + Address);
            while(Address%B != 0){
                Address -=1;
            }
            for(int i =0; i < B; i++){
                //System.out.println("debug: "+ i + ", address: "+ Address+i);
                r.getRam().set(Address+i, line.getBlocks().get(i));
            }
        }
        return;

    }



    /**
     * This funcion will find the block requested in ram and write the new block in its place. It may also write this dot RAM if the write hit policy is set to 1
     * If the block is not in ram it will use the write miss policy to either load it or write directly in memory.
     * Te=he function will also write any dirty lines back into memory if they are being replaced 
     * @param loc is the hexadecimal string input of the location in cache/Ram that is being replaced.
     * @param write is the 8 bit hex string taking the place of the origina block.
     */
    public void writecache(String loc, String write){
        String writein = write.substring(2); //fix the format of the hex
        Boolean hit = false; 

        String Address = Converter.HextoBin(loc);
        if(m < Address.length()){  
            System.out.println("Unexpected error, Address is longer then needed address bits");
            hit = false;
            return;
        }
        while(m> Address.length()){ //makes sure the address is the correct length for sepperating bits
            Address= "0" + Address; 
        }
        String tagbits = Address.substring(0, t);
        String taghex = Converter.BintoHex(tagbits);
        String setindex = Address.substring(t, s+t);
        String blockoffset = Address.substring(t+s, m);

        int setnumber = 0;
        if(!setindex.equals("")){
            setnumber = Converter.BintoDec(setindex);
        }

        //line 1-2
        System.out.println("set:" + setnumber);
        System.out.println("tag:" + taghex);

        Vector<Line> setloc = CacheSets.get(setnumber);
        Vector<Line> lastloc = LastSets.get(setnumber);

        Line found = null;
        for(int i =0; i < setloc.size(); i++){
            found = setloc.get(i);
            if(found.getTag().equals(taghex) && found.getValid()==1){ //if tag matches and the location is valid its a hit
                numCacheHits++;
                //line 3-6
                
                found.incermentUsage(); //item was used so incerment it

                if(RP ==2 && lastloc.indexOf(found) !=0 ){ //if it is a least recent used and the line is not first in the MPQ
                    //move the item to the front of queue
                    Line move = lastloc.remove(lastloc.indexOf(found));
                    lastloc.add(0, move);
                }
                hit = true; //if it exist set hit to true and break
                break;
            }
        }
        

        if(hit == true){//if it is a hit
            System.out.println("hit:yes");
            System.out.println("evicted_line:-1");
            System.out.println("ram_address:-1");
            System.out.println("data:" + write);
            System.out.println("dirty_bit:" + found.getDirty());
            

            found.getBlocks().set(Converter.BintoDec(blockoffset),writein); //replace the String in the location in cache
            if(WH == 1){ //write into RAM as well
                int Ramloc = Converter.HexToInt(loc);
                r.getRam().set(Ramloc, writein);
            }
            else if(WH == 2){ //write only into cache and set a dirty bit
                found.setDirty(1); 
            }
        }

        else{
            numCacheMiss++;
            if(WM == 1){ //if the write hit is 1 then load to ram and write here


                //load memory location to cache
                System.out.println("hit:no");
                int open = -1;
                //checks for empty Lines to write in.
                
                for(int i =0; i < setloc.size(); i++){
                    if(setloc.get(i).getBlocks().get(0).equals("")){ //if you find an empty block
                        open = i;
                        break;
                    }
                }
                
                //if an empty line exists
                if(open != -1){ //fill empty first 
                    //System.out.println(blockoffset);
                    replace(Converter.HexToInt(loc), setloc.get(open), taghex, Converter.BintoDec(blockoffset), setnumber);
                    if(RP == 2){
                        Line move = lastloc.remove(E-1);
                        lastloc.add(0, move);
                    }
                    System.out.println("evicted_line:" + open); //give what line was replaced
                }
                //check replacment policy if full
                else if(RP == 1){ 
                    RandomReplace(Converter.HexToInt(loc), setloc, taghex, Converter.BintoDec(blockoffset), setnumber);
                }
                else if(RP == 2){
                    LeastRecent(Converter.HexToInt(loc), setloc, taghex, Converter.BintoDec(blockoffset), lastloc,setnumber);
                }
                else if(RP ==3){
                    LeastFrequent(Converter.HexToInt(loc), setloc, taghex, Converter.BintoDec(blockoffset),setnumber);
                }
                
                //find the written in item
                for(int i =0; i < setloc.size(); i++){
                    found = setloc.get(i);
                    if(found.getTag().equals(taghex) && found.getValid()==1){ //if tag matches and the location is valid its a hit
                        found.getBlocks().set(Converter.BintoDec(blockoffset),writein); //replace the String in the location in cache
                        break;
                    }
                }


                System.out.println("ram_address:" + loc);
                System.out.println("data:" + write);
                System.out.println("dirty_bit:" + found.getDirty());
                if(WH == 1){ //write into RAM as well
                    int Ramloc = Converter.HexToInt(loc);
                    r.getRam().set(Ramloc, writein);
                }
                else if(WH == 2){ //write only into cache and set a dirty bit
                    found.setDirty(1); 
                }

            }
            else if(WM == 2){ //if the write hit is 2 then only write to Memory
                System.out.println("hit:no");
                System.out.println("evicted_line:-1");
                System.out.println("ram_address:-1");
                System.out.println("data:" + write);
                System.out.println("dirty_bit:0");
                int Ramloc = Converter.HexToInt(loc);
                r.getRam().set(Ramloc, writein);
            }
        }

        
    }

    /**
     * clears the cache and sets it back to cold cache state. It will also reset the least recently used and least-frequently used. 
     * All dirty bit lines will be writen back to memory.
     */
    public void clear(){
        for(int i=0; i <LastSets.size(); i++){//remove all items from each mpq for reset
            LastSets.get(i).clear();
        }
        for(int i =0; i < CacheSets.size(); i++){//for each set
            for(int j=0; j < CacheSets.get(i).size(); j++){ //for each line
                String blockbits="";
                if(CacheSets.get(i).get(j).getDirty() ==1){ //before evicting check if dirty bit is present;
                    for(int f=0; f<b; f++){ //add all the block bits as 0's to fill end of address
                    blockbits += "0";
                    }
                    String setdex = Integer.toBinaryString(i);
                    if(s == 0){//in case there is only 1 set
                        setdex = "";
                    }
                    else if(setdex.length() != 2){ //getting the spacing right so 1 = 0x01
                        setdex = "0"+ setdex;
                    }
                    String stringAddress = Converter.HextoBin(CacheSets.get(i).get(j).getTag()) +""+ setdex + blockbits; // tag bits + set bits + start of block
                    //System.out.println("debug: " + Converter.HextoBin(CacheSets.get(i).get(j).getTag()) +"; "+ setdex +"; "+ blockbits);
                    dirtToRam(Converter.BintoDec(stringAddress), CacheSets.get(i).get(j)); //write dirty bit to Ram
                }
                LastSets.get(i).add(CacheSets.get(i).get(E-1-j)); //places all the items in reverse order into the MPQ again so line 0 is last
                CacheSets.get(i).get(j).reset(); //clear the cache line
            }
        }
        System.out.println("cache_cleared");
    }


    /**
     * Prints the contents in cache line by line and displays the caches stats that where entered at the start.
     */
    public void viewcache(){
        System.out.println("cache_size:" + C);
        System.out.println("data_block_size:" + B);
        System.out.println("associativity:" + E);
        if(RP == 1){
            System.out.println("replacement_policy:random_replacment");  
        }
        if(RP == 2){
            System.out.println("replacement_policy:least_recently_used");  
        }
        if(RP == 3){
            System.out.println("replacement_policy:least_frequently_used");  
        }


        if(WH == 1){
            System.out.println("write_hit_policy:write_through");  
        }
        if(WH == 2){
            System.out.println("write_hit_policy:write_back");  
        }

        if(WM == 1){
            System.out.println("write_miss_policy:write_allocate");  
        }
        if(WM == 2){
            System.out.println("write_miss_policy:no_write_allocate");  
        }
        System.out.println("number_of_cache_hits:" + numCacheHits);
        System.out.println("number_of_cache_misses:" + numCacheMiss);
        System.out.println("cache_content:");
        for(int i = 0; i < CacheSets.size(); i++){
            for(int j=0; j< CacheSets.get(i).size(); j++){
                System.out.println(CacheSets.get(i).get(j));
            }
        }
    }

    /**
     * Places all cache lines blocks content into a folder called cache-dump.txt 
     */
    public void cachedump(){
        try{
            File cachedumpfile = new File("cache.txt");
            cachedumpfile.createNewFile();
            PrintWriter outf = new PrintWriter("cache.txt");
            for(int i = 0; i < CacheSets.size(); i++){
                for(int j=0; j< CacheSets.get(i).size(); j++){
                    outf.println(CacheSets.get(i).get(j).fileout());
                }
            }
            outf.close();
        }
        catch(IOException e){
            System.out.println("error in cachedump. could not create or open file, check folder permissions.");
        }
                
    }
    
}
