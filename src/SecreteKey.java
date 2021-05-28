import it.unisa.dia.gas.jpbc.Element;

public class SecreteKey {
    Element Xi;
    Element Di;
    public SecreteKey() {
    }
    public SecreteKey(Element xi, Element di) {
        Xi = xi.duplicate();
        Di = di.duplicate();
    }
    public Element getXi() {
        return Xi;
    }
    public void setXi(Element xi) {
        Xi = xi.duplicate();
    }
    public Element getDi() {
        return Di;
    }
    public void setDi(Element di) {
        Di = di.duplicate();
    }
    @Override
    public String toString() {
        return "SecreteKey [Di=" + Di + ", Xi=" + Xi + "]";
    }
    
}
