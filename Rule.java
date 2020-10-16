public class Rule
{
    private double prob;
    private String name;
    private String lchild;
    private String rchild;
    private boolean original;

    public Rule(double p, String n, String child, boolean o)
    {
        prob = p;
        name = n;
        lchild = child;
        rchild = null;
        original = o;
    }

    public Rule(double p, String n, String lc, String rc, boolean o)
    {
        prob = p;
        name = n;
        lchild = lc;
        rchild = rc;
        original = o;
    }

    public Rule(double p, String n, String child)
    {
        prob = p;
        name = n;
        lchild = child;
        rchild = null;
        original = true;
    }

    public Rule(double p, String n, String lc, String rc)
    {
        prob = p;
        name = n;
        lchild = lc;
        rchild = rc;
        original = true;
    }

    public boolean equals(String n, String child)
    {
        return name.equals(n) && lchild.equals(child) && rchild == null;
    }

    public boolean equals(String n, String lc, String rc)
    {
        return name.equals(n) && lchild.equals(lc) && rchild.equals(rc);
    }

    public boolean equals(Rule r)
    {
        return r.name.equals(name) && r.lchild.equals(lchild) && r.rchild.equals(rchild);
    }

    public double prob()
    {
        return prob;
    }

    public String name()
    {
        return name;
    }

    public String lchild()
    {
        return lchild;
    }

    public String rchild()
    {
        return rchild;
    }

    public boolean isOriginal()
    {
        return original;
    }
}