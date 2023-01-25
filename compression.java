import java.util.*;
import java.io.*;

public class Main {
    static String vec = "";

    static double smallestProb = 1.0;
    static HashMap <Character, Double> mp = new HashMap<Character,Double>();

    static Vector<Character> symbol = new Vector<Character>();
    static Vector<Double> lower = new Vector<Double>();
    static Vector<Double> upper = new Vector<Double>();

    static Vector<Integer> binary = new Vector<Integer>();
    static boolean find(String vec,char a)
    {
        for (int i=0;i<vec.length();i++)
        {
            if (a==vec.charAt(i))
                return true;
        }
        return false;
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

    public static boolean checkE1(double upper){
        return upper < 0.5;
    }

    public static boolean checkE2(double lower){
        return lower > 0.5;
    }

    public static String compress(String originalFile){
        String res ="";
        int index =findChar(0 , originalFile);

        double initialLow = lower.get(index);
        double initalUpper = upper.get(index);



        while(checkE1(initalUpper) || checkE2(initialLow))
        {
            while(checkE1(initalUpper))
            {
                initialLow = e1_Scaling(initialLow);
                initalUpper = e1_Scaling(initalUpper);
                binary.add(0);
            }

            while(checkE2(initialLow))
            {
                initialLow = e2_Scaling(initialLow);
                initalUpper = e2_Scaling(initalUpper);
                binary.add(1);
            }
        }


        double prevLower = initialLow;
        double prevUpper = initalUpper;


        for(int i =1 ; i<originalFile.length() ; i++)
        {
            double prevRange = (prevUpper - prevLower);
            index = findChar(i , originalFile);
            double lowerS = prevLower + (prevRange * lower.get(index));
            double upperS = prevLower + (prevRange * upper.get(index));


            while(checkE1(upperS) || checkE2(lowerS))
            {
                while(checkE1(upperS))
                {
                    lowerS = e1_Scaling(lowerS);
                    upperS = e1_Scaling(upperS);
                    binary.add(0);
                }

                while(checkE2(lowerS))
                {
                    lowerS = e2_Scaling(lowerS);
                    upperS = e2_Scaling(upperS);
                    binary.add(1);
                }
            }

            prevLower = lowerS;
            prevUpper = upperS;
        }


        for(int i =0 ; i< binary.size() ; i++)
        {
            res += binary.get(i);
        }

        int k = K_Value(smallestProb);
        res += 1;

        for(int i =0 ; i<k-1 ; i++)
        {
            res += '0';
        }
        return res;

    }

    public static int findChar(int index , String originalFile){
        for(int i =0 ; i< symbol.size() ; i++)
        {
            if(symbol.get(i) == originalFile.charAt(index))
                return i;
        }
        return -1;
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

    public static void main(String[] args) {
        String originalData = "";

        try {
            File myObj = new File("D:\\BinaryArithmetic\\BinaryArithmeticCoding\\src\\Original File.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                originalData = myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        for (int i=0;i<originalData.length();i++)
        {
            try {
                if (!(find(vec,originalData.charAt(i))))
                {
                    vec+=originalData.charAt(i);
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        for (int i=0;i<vec.length();i++)
        {
            double cnt=0.0;
            for (int j=0;j<originalData.length();j++)
            {
                if (vec.charAt(i)==originalData.charAt(j))
                    cnt++;
            }
            mp.put(vec.charAt(i),(double)(cnt/originalData.length()));
        }

//        mp.put('A',0.8);
//        mp.put('B',0.02);
//        mp.put('C',0.18);

        makeScale();

        String output = compress(originalData);


        // writing the characters and the prob
        // then the compressed binary
        try {
            FileWriter myWriter = new FileWriter("D:\\BinaryArithmetic\\BinaryArithmeticCoding\\src\\Compressed.txt");
            for(Map.Entry<Character , Double> entry : mp.entrySet())
            {
                myWriter.write(entry.getKey() + "=" + entry.getValue()+",");
            }
            myWriter.write(" ");
            myWriter.write(output);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }
}