import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Pairing pairing = PairingFactory.getPairing("params1.txt"); 
        //use pbc wrapper
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        
        Element g1 = pairing.getG1().newRandomElement();
        System.out.println("ramdone lement generated "+ g1);
        String str = "random baits";
        byte [] strbyte = str.getBytes();                                // converting it into bytes.
            
        Element byteg1 = pairing.getG1().newElement().setFromHash(strbyte, 0, strbyte.length);             // setting g1 element from bytes.
        System.out.println("    -------     \n"+byteg1);
        Element mul = byteg1.mul(g1);
        System.out.println( "---------- --------\n"+ mul);
        byte mulbyte[] = mul.toBytes();
        Element bytezn = pairing.getZr().newElement().setFromHash(mulbyte, 0, mulbyte.length);
        System.out.println( " filllal reusl t is \n"+ bytezn);
    }
}
