import java.lang.*;

public class Lexicon
{
    private static final int DEFAULT_SIZE = 999;
    private Rule[] words;
    private int size;

    public Lexicon()
    {
        words = new Rule[DEFAULT_SIZE];
        size = 0;
    }

    public void addWord(String w)
    {
        String[] tokens = w.split(" |->");
        
        // System.out.println("\nFor: " + w + " tokens are");
        // for(int i = 0; i < tokens.length; i++)
        //     System.out.print(tokens[i] + "\t");

        words[size] = new Rule(Double.parseDouble(tokens[0]), tokens[1].trim().toUpperCase(), tokens[2].trim().toLowerCase());
        size++;
    }

    public double getProb(String lr)
    {
        String[] tokens = lr.split(" |->");

        for(Rule rule : words)
        {
            if(rule.equals(tokens[0], tokens[1]))
                return rule.prob();
        }

        return -1;
    }

    public Rule[] lex()
    {
        return words;
    }

    public int size()
    {
        return size;
    }
}