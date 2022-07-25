// File: Converter
// Author: Finn Robin Schniebel
// Date: 12/6/2021
// Section: 505
// E-mail: frms@tamu.edu 
// Description: this is a helper class that is desigend to allow for quick and consitant conversions between hexadecimal, binary, and decimal values. 
// All functions can be called without instantiating the object because they are all static.
public final class Converter {

    /**
     * The function converts a hexadecimal number into an integer.
     * @param hex takes in a hexadecimal with or without 0x in front
     * @return the integer version of the hexadecimal
     */
    public static int HexToInt(String hex){
        if(hex.substring(0,2).equals("0x")){
            String h= hex.substring(2);
            return Integer.parseInt(h,16); // hex.substring(2, -1);
        }
        else{
            return Integer.parseInt(hex, 16);
        }
    }
    /**
     * The finction converts an integer into a hexadeciam string
     * @param i integer value to convert to hex
     * @return hexedecimal number in the form of a string excluding the "0x" infront. Does not check for 8 bit length.
     */
    public static String IntToHex(int i){
        return Integer.toHexString(i).toUpperCase();
    }
    /**
     * Converts a hexadecimal number into a binary number in string form.
     * @param hex //takes a string input that represents a hexadecimal number.
     * @return String that is the number writen out in binary; 
     */
    public static String HextoBin(String hex){
        int i = HexToInt(hex);
        return Integer.toBinaryString(i);
    }

    /**
     * Takes in a binary string input and converts it into decimal integer
     * @param bin takes in a string that consists of binary (ex: "010111")
     * @return an integer that holds the value the binary represents.
     */
    public static int BintoDec(String bin){
        return Integer.parseInt(bin,2);
    }

    /**
     * Binary to hexadecimal converted 
     * @param bit takes in a string that is formated to be binary (ex: "010111")
     * @return returns a string that is formated to an 8  bit hexadecimal number and is always 2 hex digits.
     */
    public static String BintoHex(String bit){
        String taghex = Converter.IntToHex(Converter.BintoDec(bit));
        taghex= taghex.toUpperCase();
        if(taghex.length() < 2){
            taghex = "0" + taghex;
        }
        return taghex;
    }

}
