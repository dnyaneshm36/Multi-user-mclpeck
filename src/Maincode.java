import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;


public class Maincode {
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    { 
        // Static getInstance method is called with hashing SHA 
        MessageDigest md = MessageDigest.getInstance("SHA-256"); 

        // digest() method called 
        // to calculate message digest of an input 
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8)); 
    }
    
    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation 
        BigInteger number = new BigInteger(1, hash); 

        // Convert message digest into hex value 
        StringBuilder hexString = new StringBuilder(number.toString(16)); 

        // Pad with leading zeros
        while (hexString.length() < 32) 
        { 
            hexString.insert(0, '0'); 
        } 
        return hexString.toString(); 
    }
        //H1: {0,1}*→G1*
        static Element hash1(String str,Pairing pairing)
        {
            String shastring = "";
            try {
            shastring = toHexString(getSHA(str));                                      //generate the sha256 output for string 
            } catch (NoSuchAlgorithmException e) {
            System.out.println(" No such Algorithm exception occurred ");
            e.printStackTrace();
            }
            byte [] shatringbyte = shastring.getBytes();                                // converting it into bytes.
            
            Element g1 = pairing.getG1().newElement().setFromHash(shatringbyte, 0, shatringbyte.length);             // setting g1 element from bytes.
            return g1.duplicate();                                                      // to make deep copy used duplicate.
        }

        //H2: {0,1}*→G1*
        static Element hash2(String str,Pairing pairing)
        {
            String shastring = "";
            try {
              shastring = toHexString(getSHA(str));                                      //generate the sha256 output for string 
            } catch (NoSuchAlgorithmException e) {
              System.out.println(" No such Algorithm exception occurred ");
              e.printStackTrace();
            }
            byte [] shatringbyte = shastring.getBytes();                                // converting it into bytes.
            
            Element g1 = pairing.getG1().newElement().setFromHash(shatringbyte, 0, shatringbyte.length);             // setting g1 element from bytes.
            return g1.duplicate();                                                      // to make deep copy used duplicate.
        }
        // H0  {0, 1}∗ × G1 → Zn∗
        static Element hash0(String str, Pairing pairing,Element g1)
        {
            Element g1str = hash1(str, pairing).duplicate();
            Element mul = g1str.mul(g1);
            System.out.println( "---------- --------\n"+ mul);
            byte mulbyte[] = mul.toBytes();
            Element bytezn = pairing.getZr().newElement().setFromHash(mulbyte, 0, mulbyte.length);
            System.out.println( " filllal reusl t is \n"+ bytezn);
            return bytezn.duplicate();
        }
        // ℎ3 ∶ {0, 1}∗ × G1 × G1 × G1 → Zn* 
        static Element hash3(String str, Pairing pairing,Element g1one, Element g1two, Element g1three)
        {
            Element g1str = hash1(str, pairing).duplicate();
            Element multi = g1str.mul(g1one);
            multi.mul(g1two);
            multi.mul(g1three);
            System.out.println( "---------- --------\n"+ multi);
            byte mulbyte[] = multi.toBytes();
            Element bytezn = pairing.getZr().newElement().setFromHash(mulbyte, 0, mulbyte.length);
            System.out.println( " filllal reusl t is \n"+ bytezn);
            return bytezn.duplicate();
        }
    public static void main(String[] args) throws Exception {

        // int rBits = 160;                                                                      // this code generater params1.txt file
        // int qBits = 512;                                                                      // no need to generated again.
        // PairingParametersGenerator pg = new TypeACurveGenerator(rBits, qBits);
        // PairingParameters params = pg.generate();

        // try {
        //     FileWriter fw = new FileWriter("params1.txt");
        //     String paramsStr = params.toString();
        //     fw.write(paramsStr);
        //     fw.flush();
        //     fw.close();

        // } catch (IOException e) {
        //     System.out.println("the we get problem in writering ");
        //     e.printStackTrace();
        // }


        // //Implamenting the pairing   

        // Pairing pairing = PairingFactory.getPairing("params1.txt"); 
        // //use pbc wrapper
        // PairingFactory.getInstance().setUsePBCWhenPossible(true);


        //Implamenting the pairing   
 
        Pairing pairing = PairingFactory.getPairing("params1.txt"); 
        //use pbc wrapper
        PairingFactory.getInstance().setUsePBCWhenPossible(true);

        Element g = pairing.getG1().newRandomElement();

        Element s = pairing.getZr().newRandomElement();                   // random value of lambda.
        Element gpub = g.duplicate();
        gpub.mulZn(s); 


        
        // Extract-Partial-Private-Key


        int m = 5;
        ArrayList<String > receiverId = new ArrayList<String>();
        String id = "receiver1";
        receiverId.add(id);
         id = "receiver2";
         receiverId.add(id);
         id = "receiver3";
         receiverId.add(id);
         id = "receiver4";
         receiverId.add(id);
         id = "receiver5";
         receiverId.add(id);

        ArrayList<PartialPrivatekey> receiversPartialPrivatekeys = new ArrayList<PartialPrivatekey>();

        String data = "";                                                   // read the value of q from the param1.txt

        try {                 // taking q from param
            File myObj = new File("params1.txt");
            Scanner myReader = new Scanner(myObj);
            data = myReader.nextLine();
            data = myReader.nextLine();
            data = data.substring(2);     
            myReader.close();
            } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        String str = data;
        BigInteger q = new BigInteger(str);

        for( int i = 0 ;i< m ; i++)
        {
            Element Si   = pairing.getZr().newRandomElement();
            Element Ti = g.powZn(Si);
            Element alphai = hash0(receiverId.get(i), pairing, Ti).duplicate();
            Element ai = alphai.duplicate();
            alphai.mul(s);
            alphai.add(Si);
            String formod = alphai.toString();
            BigInteger formodiBigInteger = new BigInteger(formod);
            formodiBigInteger = formodiBigInteger.mod(q);
            Element Di = pairing.getZr().newRandomElement();
            Di.set(formodiBigInteger);
            PartialPrivatekey temp = new PartialPrivatekey(Si,Ti,Di,ai);
            receiversPartialPrivatekeys.add(temp);
        }

        //Set-Secret-Value

        ArrayList<SecreteKey> receiversSecreteKeys = new ArrayList<SecreteKey>();

        for (int i = 0 ;i< m ;i++)
        {
            Element Xi = pairing.getZr().newRandomElement();
            Element Di = receiversPartialPrivatekeys.get(i).getDi();
            SecreteKey temp = new SecreteKey(Xi, Di);
            receiversSecreteKeys.add(temp);

        }

        // Set-Public-Key

        ArrayList<PublicKey> receiversPublicKeys = new ArrayList<PublicKey>();
        for (int i = 0 ;i< m ;i++)
        {
            Element Yi = pairing.getZr().newRandomElement();
            Element Ti = receiversPartialPrivatekeys.get(i).getTi();
            PublicKey temp = new PublicKey(Yi, Ti);
            receiversPublicKeys.add(temp);

        }


        //mCLPECK
        int n = 10;
        ArrayList<String> words = new ArrayList<String>();
        ArrayList<Element> Bjs = new ArrayList<Element>();
        String word= "first";
        words.add(word);
        word = "second";
        words.add(word);
        word = "third";
        words.add(word);
        word = "four";
        words.add(word);
        word = "five";
        words.add(word);
        word = "six";
        words.add(word);
        word = "seven";
        words.add(word);
        word = "eight";
        words.add(word);
        word = "nine";
        words.add(word);
        word = "ten";
        words.add(word);


        Element r = pairing.getZr().newRandomElement(); 
        Element rdash = pairing.getZr().newRandomElement();

        Element A = g.duplicate();
        A.powZn(r);

        for (int i = 0 ;i < m ;i++)
        {
            String ids = receiverId.get(i);
            Element Yj = receiversPublicKeys.get(i).getYi();
            Element Tj = receiversPublicKeys.get(i).getTi();
            
            Element betaj = hash3(ids, pairing, gpub, Yj, Tj);
            Element alphai = receiversPartialPrivatekeys.get(i).getAlphai();

            Element temp = Yj.duplicate();
            temp.powZn(betaj);
            temp.mul(Tj);

            Element tem = gpub.duplicate();
            tem.powZn(alphai);
            temp.mul(tem);

            temp.powZn(rdash);

            Element Bj = temp.duplicate();
            Bjs.add(Bj);
        }

        ArrayList<Element> Cjs = new ArrayList<Element>();

        for ( int  i = 0 ;i< n ;i++)
        {
            
            Element Hi = hash1(words.get(i), pairing);
            Element Fi = hash2(words.get(i),pairing);

            Hi.powZn(r);
            Fi.powZn(rdash);
            Element Ci = Hi.duplicate();
            Ci.mul(Fi);
            Cjs.add(Ci);
        }


        // Trapdoor

        // let j is receiver j = 2 
        // third receiver secrete key is used int the program.
        int j = 2;

        int l = 4;
        ArrayList<String> Searchingwords = new ArrayList<String>();
        word = "abc";
        Searchingwords.add(word);
        word = "first";
        Searchingwords.add(word);
        word = "covid";
        Searchingwords.add(word);
        word = "another";
        Searchingwords.add(word);

        Element t = pairing.getZr().newElement();   
        Element Tj1 = g.duplicate();
        Tj1.powZn(t);

        Element multiplcation_hi =hash1(Searchingwords.get(0), pairing);;
        Element multiplcation_fi = hash2(Searchingwords.get(0),pairing);
        for ( int  i = 1 ;i< l ;i++)
        {
            
            Element Hi = hash1(Searchingwords.get(i), pairing);
            Element Fi = hash2(Searchingwords.get(i),pairing);

            multiplcation_hi.mul(Hi);
            multiplcation_fi.mul(Fi);
        } 
        Element Tj2 = multiplcation_hi.duplicate();
        Tj2.powZn(t);
        String idofj = receiverId.get(j);
        Element Yj = receiversPublicKeys.get(j).getYi();
        Element Tj = receiversPublicKeys.get(j).getTi();
        Element betaj = hash3(idofj, pairing, gpub, Yj, Tj);

        Element Dj = receiversSecreteKeys.get(j).getDi();
        Element Xj = receiversSecreteKeys.get(j).getXi();

        Element Denomenator = betaj.duplicate();
        Denomenator.mul(Xj);
        Denomenator.add(Dj);

        Element pow = t.duplicate();
        pow.div(Denomenator);

        Element Tj3 = multiplcation_fi.duplicate();
        Tj3.powZn(pow);



        // Test()

        Element prod_cj= Cjs.get(0).duplicate();
        
        for ( int i = 1 ; i< Cjs.size();i++)
        {
            prod_cj.mul(Cjs.get(i));
        }

        Element  leftpair = pairing.pairing(Tj1, prod_cj);

        Element  right_first_pair = pairing.pairing(A, Tj2);

        for (int i = 0 ;i< m ;i++)
        {
            Element Bj = Bjs.get(i);
            Element right_second_pair = pairing.pairing(Bj, Tj3);
            Element mulpair = right_first_pair.duplicate();
            mulpair.mul(right_second_pair);

            if(mulpair.isEqual(leftpair))
            {
                System.out.println("True , ---  1  index "+i);
            }
            else
            {
                System.out.println("False , ---  0  index "+i);
            }
        }
    }
}
