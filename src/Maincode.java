import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

        
    }
}
