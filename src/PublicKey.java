import it.unisa.dia.gas.jpbc.Element;

public class PublicKey {
    Element Yi;
    Element Di;
    public PublicKey() {
    }
    public PublicKey(Element yi, Element di) {
        Yi = yi.duplicate();
        Di = di.duplicate();
    }
    public Element getYi() {
        return Yi;
    }
    public void setYi(Element yi) {
        Yi = yi.duplicate();
    }
    public Element getDi() {
        return Di;
    }
    public void setDi(Element di) {
        Di = di.duplicate();
    }
    @Override
    public String toString() {
        return "PublicKey [Di=" + Di + ", Yi=" + Yi + "]";
    }
    
}
