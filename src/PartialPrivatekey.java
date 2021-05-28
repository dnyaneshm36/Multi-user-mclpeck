import it.unisa.dia.gas.jpbc.Element;

public class PartialPrivatekey {
    Element Si;
    Element Ti;
    Element Di;
    Element alphai;
    public PartialPrivatekey() {
    }
    public PartialPrivatekey(Element si, Element ti, Element di, Element alphai) {
        Si = si.duplicate();
        Ti = ti.duplicate();
        Di = di.duplicate();
        this.alphai = alphai.duplicate();
    }
    public Element getSi() {
        return Si;
    }
    public void setSi(Element si) {
        Si = si.duplicate();
    }
    public Element getTi() {
        return Ti;
    }
    public void setTi(Element ti) {
        Ti = ti.duplicate();
    }
    public Element getDi() {
        return Di;
    }
    public void setDi(Element di) {
        Di = di.duplicate();
    }
    public Element getAlphai() {
        return alphai;
    }
    public void setAlphai(Element alphai) {
        this.alphai = alphai.duplicate();
    }
    @Override
    public String toString() {
        return "PartialPrivatekey [Di=" + Di + ", Si=" + Si + ", Ti=" + Ti + ", alphai=" + alphai + "]";
    }
    
}
