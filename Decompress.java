import java.util.*;
import java.io.*;

public class Decompress {
    static double smallestProb = 1.0;
    static HashMap <Character, Double> mp = new HashMap<Character,Double>();

    static Vector<Character> symbol = new Vector<Character>();
    static Vector<Double> lower = new Vector<Double>();
    static Vector<Double> upper = new Vector<Double>();
    static Vector<Integer> binary = new Vector<Integer>();



    public static boolean checkE1(double upper){
        return upper < 0.5;
    }

    public static boolean checkE2(double lower){
        return lower > 0.5;
    }

    public static int K_Value(double prob)
    {
        int i=1;
        while ((1/(double)Math.pow(2,i))>=prob)
            i++;
        return i;
    }

    public static double e1_Scaling(double value )
    {
        double x=value*2;
        return x;
    }

    public static double e2_Scaling(double value )
    {
        double x=(value-0.5)*2;
        return x;
    }
    public static void makeScale(){
        double prev = 0;

        for (Map.Entry<Character,Double> entry : mp.entrySet())
        {
            if(entry.getValue() < smallestProb)
                smallestProb = entry.getValue();

            symbol.add(entry.getKey());
            lower.add(prev);
            prev += entry.getValue() ;
            upper.add(prev);
        }
    }
    public static void setVectors(String Compressed){
        for(int i =0 ; i<Compressed.length() ; i++)
        {
            if(Compressed.charAt(i) == '=')
            {
                char symbol= Compressed.charAt(i-1);
                String prob="";
                for(int j =i+1; j<Compressed.length() && Compressed.charAt(j) != ',' ; j++ )
                {
                    prob += Compressed.charAt(j);
                    i=j;
                }
                mp.put(symbol, Double.parseDouble(prob));
            }
            if(Compressed.charAt(i) == ' ')
            {
                for(int j =i+1; j<Compressed.length(); j++ )
                {
                    binary.add(Compressed.charAt(j) -'0');
                }
                break;
            }
        }
    }
    public static String readFromCompressed(String fileName) {
        String data="";
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                 data= myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data;
    }

    public static int toBinary(int K , int Shift){
        int result = 0;
        int power=0;
        for(int idx = Shift+(K-1); idx >=Shift && idx<binary.size() ; idx--)
        {
            result += binary.get(idx) * Math.pow(2,power);
            power++;
        }

        return result;
    }

    public static int checkForSymbol(double code){
        for(int i =0 ; i<symbol.size() ; i++)
        {
            if(code > lower.get(i) && code < upper.get(i))
                return i;
        }
        return -1;
    }

    public static String Decompress(String compressed , int K){
        int shift = 0;
        boolean shifted = false;
        double code = toBinary(K , shift) / Math.pow(2 , K);
        double nextCode;
        int symbolIndex = checkForSymbol(code);
        String decompressedStream = "" +symbol.get(symbolIndex);
        double prevLower = 0;
        double prevUpper = 0;
        double prevRange = 1;


        double Lower = prevLower + prevRange * lower.get(symbolIndex);
        double Upper = prevLower + prevRange * upper.get(symbolIndex);

        while(checkE2(Lower) || checkE1(Upper))
        {
            if(checkE2(Lower))
            {
                Lower = e2_Scaling(Lower);
                Upper = e2_Scaling(Upper);
                shift++;
                shifted = true;
            }

            if(checkE1(Upper))
            {
                Lower = e1_Scaling(Lower);
                Upper = e1_Scaling(Upper);
                shift++;
                shifted = true;
            }
        }

        prevLower = Lower;
        prevUpper = Upper;
        prevRange = prevUpper - prevLower;

        for (int i = 1 ; i<compressed.length() ; i++)
        {
            if(shifted) {
                code = toBinary(K, shift) / Math.pow(2, K);
                shifted = false;
            }
            nextCode = (code - prevLower) / (prevRange);


            symbolIndex = checkForSymbol(nextCode);
            decompressedStream += symbol.get(symbolIndex);

            Lower = prevLower + prevRange * lower.get(symbolIndex);
            Upper = prevLower + prevRange * upper.get(symbolIndex);

            while(checkE2(Lower) || checkE1(Upper))
            {
                if(checkE2(Lower))
                {
                    Lower = e2_Scaling(Lower);
                    Upper = e2_Scaling(Upper);
                    shift++;
                    shifted = true;
                }

                if(checkE1(Upper))
                {
                    Lower = e1_Scaling(Lower);
                    Upper = e1_Scaling(Upper);
                    shift++;
                    shifted = true;
                }
            }

            prevLower = Lower;
            prevUpper = Upper;
            prevRange = prevUpper - prevLower;

            if(code == 0.5 )
                break;
        }


        return decompressedStream;

    }

    public static void createFile(String fileName){
        try {
            File myObj = new File(fileName);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void writeToFile(String fileName , String decompressedStream){
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(decompressedStream);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String compressed = readFromCompressed("D:\\BinaryArithmetic\\BinaryArithmeticCoding\\src\\Compressed.txt");
        setVectors(compressed);
        makeScale();
        int K = K_Value(smallestProb);
        String decompressedStream =Decompress(compressed , K);
        createFile("D:\\BinaryArithmetic\\BinaryArithmeticCoding\\src\\Decompressed.txt");
        writeToFile("D:\\BinaryArithmetic\\BinaryArithmeticCoding\\src\\Decompressed.txt" , decompressedStream);

    }
}
