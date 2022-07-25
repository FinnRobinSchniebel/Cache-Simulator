// File: cahcesimulator
// Author: Finn Robin Schniebel
// Date: 12/6/2021
// Section: 505
// E-mail: frms@tamu.edu 
// Description: This is the main cache simulator function that calls all others



import java.util.*;

//import java.io.*;


public class cachesimulator{

    /**
     * main runs the program and acts as the interface for the user handeling all user inputs and menus.
     * @param args takes in the file name of the formated file that will be loaded as ram
     */
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** Welcome to the cache simulator ***");
        System.out.println("initialize the RAM:");
        //sc.nextLine(); //has to start at 0x00
        String input = sc.nextLine();
        StringTokenizer stk = new StringTokenizer(input);
        String command = stk.nextToken();
        String starthex = stk.nextToken();
        String endhex = stk.nextToken();

        if(!command.equals("init-ram")){
            System.out.println(command);
            System.out.println("error command incorrect please restart program with correct input of 'init-ram'");
            sc.close();
            return;
        }

        int start = Converter.HexToInt(starthex);
        if(start != 0){
            System.out.println("Warning: need to start at 0x00 for program to garantee work! Program does support more start points.");
        }
        int end = Converter.HexToInt(endhex);

        RAM Ram = new RAM();
        Ram.InitRam(start, end, args[0]);

        System.out.println("RAM successfully initialized!");



        System.out.println("configure the cache: ");
        System.out.print("cache size: ");
        int C = sc.nextInt();

        System.out.print("data block size: ");
        int B = sc.nextInt();

        System.out.print("associativity: ");
        int E = sc.nextInt();

        System.out.print("replacement policy: ");
        int RP = sc.nextInt();

        System.out.print("write hit policy: ");
        int WH = sc.nextInt();

        System.out.print("write miss policy: ");
        int WM = sc.nextInt();

        int M = Ram.getRam().size();
        
        Cache cache = new Cache(C,B,E, M, Ram, RP, WH, WM);
        
        System.out.println("cache successfully configured!");

        sc.nextLine(); //skip what is still left since that is a thing
        
        String choice = "";
        String firstHex = "";
        String secondHex = "";

        while(!choice.equals("quit")){
            System.out.println("*** Cache simulator menu ***");
            System.out.println("type one command:");
            System.out.println("1. cache-read");
            System.out.println("2. cache-write");
            System.out.println("3. cache-flush");
            System.out.println("4. cache-view");
            System.out.println("5. memory-view");
            System.out.println("6. cache-dump");
            System.out.println("7. memory-dump");
            System.out.println("8. quit");
            System.out.println("****************************");
            
            choice = sc.nextLine();
            
            stk = new StringTokenizer(choice);
            if(stk.hasMoreTokens()){
                choice = stk.nextToken();
            }

            //personal note: use switch nexttime
            if(choice.equals("cache-read")){ //cache read
                if(!stk.hasMoreTokens()){
                    System.out.println("invalid input. Please Try again.");
                    continue;
                }
                firstHex = stk.nextToken();
                cache.read(firstHex);
            }
            else if(choice.equals("cache-write")){ //cache write
                if(!stk.hasMoreTokens()){
                    System.out.println("invalid input. Please Try again.");
                    continue;
                }
                firstHex = stk.nextToken();
                if(!stk.hasMoreTokens()){
                    System.out.println("invalid input. Please Try again.");
                    continue;
                }
                secondHex = stk.nextToken();

                cache.writecache(firstHex, secondHex);
            }
            else if(choice.equals("cache-flush")){
                cache.clear();
            }

            else if(choice.equals("cache-view")){
                cache.viewcache();
            }
            else if(choice.equals("memory-view")){
                Ram.viewRam();
            }
            else if(choice.equals("cache-dump")){
                cache.cachedump();
            }
            else if(choice.equals("memory-dump")){
                Ram.memorydump();
            }
        }
        

        sc.close();
    }
}
