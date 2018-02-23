package hotelokura.jalhotels.oneharmony.activity.map;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by barista5 on 2013/09/24.
 */
public class Element
{

    private String name;
    private ArrayList<Element> children = new ArrayList<Element>();

    public Element(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public boolean isParent()
    {
        return children.size() > 0;
    }

    public List<Element> getChildren()
    {
        return children;
    }

    public void addChild(Element element)
    {
        children.add(element);
    }
}
