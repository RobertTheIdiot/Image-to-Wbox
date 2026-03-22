import java.awt.Color;

public class NamedColor extends Color{
    String name;

    public NamedColor(String name, int rgb) {
        super(rgb);
        this.name=name;
    }
    
    public NamedColor() {
        super(0);
        name = null;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "\""+name+"\"";
    }
}
