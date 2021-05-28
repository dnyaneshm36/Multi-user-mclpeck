import it.unisa.dia.gas.jpbc.Element;

public class PublicKey {
    Element Yi;
    Element Ti;
    public PublicKey() {
    }
    public PublicKey(Element yi, Element ti) {
        Yi = yi;
        Ti = ti;
    }
    public Element getYi() {
        return Yi;
    }
    public void setYi(Element yi) {
        Yi = yi;
    }
    public Element getTi() {
        return Ti;
    }
    public void setTi(Element ti) {
        Ti = ti;
    }
    @Override
    public String toString() {
        return "PublicKey [Ti=" + Ti + ", Yi=" + Yi + "]";
    }
}
