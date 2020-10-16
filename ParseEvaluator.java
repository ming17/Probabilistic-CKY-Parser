import java.lang.*;

public class ParseEvaluator
{
    private static final int DEFAULT_SIZE = 999;
    private int numConst;
    private Constituent[] consts;
    private Grammar gram;

    public ParseEvaluator(String sent, Grammar g)
    {
        consts = new Constituent[DEFAULT_SIZE];
        numConst = 0;

        gram = g;
        readSent(sent);
    }

    public void readSent(String str)
    {
        String[] nonterms = gram.nonterms();
        int numNonTerms = gram.numNonTerms();
        String[] words = str.split("\\[");

        int start = 0;
        boolean found;

        int numEnd;
        String s;

        for(String w : words)
        {
            s = w.trim().toUpperCase();

            if(!s.equals(""))
            {
                found = false;
                for(int n = 0; n < numNonTerms; n++)
                    if(s.equals(nonterms[n]))
                        found = true;

                if(found)
                {
                    consts[numConst] = new Constituent(s, start);
                    numConst++;
                }
                else
                {
                    start++;

                    numEnd = -1;
                    for(int c = s.length()-1; c >= 0; c--)
                        if(s.charAt(c) == ']')
                            numEnd++;
                    
                    for(int i = 1; i <= numEnd; i++)
                        consts[numConst-i].setEnd(start);
                }
            }
        }
    }

    // returns recall and precision statistics as double array where this is ideal and parameter is candidate
    public double[] compareToIdeal(ParseEvaluator ideal)
    {
        double[] stats = new double[2];
        int numCorrect = 0;

        Constituent candC;
        Constituent idealC;

        for(int c = 0; c < numConst; c++)
        {
            candC = consts[c];
            for(int i = 0; i < ideal.numConst; i++)
            {
                idealC = ideal.consts[i];
                if(idealC.equals(candC))
                    numCorrect++;
            }
        }
        
        // recall
        stats[0] = numCorrect / (double) ideal.numConst;
        // precision
        stats[1] = numCorrect / (double) numConst;

        return stats;
    }

    private class Constituent
    {
        private String label;
        private int start;
        private int end;

        private Constituent(String l, int s)
        {
            label = l;
            start = s;
            end = -1;
        }

        private Constituent(String l, int s, int e)
        {
            label = l;
            start = s;
            end = e;
        }

        private void setEnd(int e)
        {
            if(end == -1)
                end = e;
        }

        private boolean equals(int s, int e)
        {
            return start == s && end == e;
        }

        private boolean equals(Constituent c)
        {
            return label.equals(c.label) && (start == c.start) && (end == c.end);
        }
    }
}