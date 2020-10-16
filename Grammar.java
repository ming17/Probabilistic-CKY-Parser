import java.lang.*;

public class Grammar
{
    private static final int DEFAULT_SIZE = 999;
    private Rule[] binaryRules;
    private Rule[] unaryRules;

    private int numBRules;
    private int numURules;

    private String[] nonterms;
    private int numNonTerms;
    private int extraRules;

    public Grammar()
    {
        binaryRules = new Rule[DEFAULT_SIZE];
        unaryRules = new Rule[DEFAULT_SIZE];
        nonterms = new String[DEFAULT_SIZE];

        numBRules = 0;
        numURules = 0;

        extraRules = 0;
        numNonTerms = 0;
    }

    public void addRule(String rule)
    {
        double prob;
        String name;
        boolean found = false;

        String[] tokens = rule.split(" |->");

        for(int i = 1; i < tokens.length; i++)
        {
            found = false;
            for(int j = 0; j < numNonTerms; j++)
            {
                if(tokens[i].trim().toUpperCase().equals(nonterms[j]))
                    found = true;
            }
            if(!found)
            {
                //System.out.println("Adding " + tokens[i].trim().toUpperCase() + " to nonterms");
                nonterms[numNonTerms] = tokens[i].trim().toUpperCase();
                numNonTerms++;
            }
        }
        prob = Double.parseDouble(tokens[0]);
        name = tokens[1].trim().toUpperCase();

        // indicates that there are too many non-terminals and binarization must occur
        if(tokens.length > 4)
        {
            // binarization
            extraRules++;
            String newName = "@" + extraRules;
            String newerName = "@" + (extraRules+1);
            binaryRules[numBRules] = new Rule(prob, name, tokens[2].trim().toUpperCase(), newName, true);
            numBRules++;

            for(int idx = 3; idx < tokens.length - 1; idx++)
            {
                if(idx == tokens.length - 2)
                    binaryRules[numBRules] = new Rule(1, newName, tokens[idx].trim().toUpperCase(), tokens[idx+1].trim().toUpperCase(), false);
                else   
                {
                    binaryRules[numBRules] = new Rule(1, newName, tokens[idx].trim().toUpperCase(), newerName, false);
                    extraRules++;
                    newName = "@" + extraRules;
                    newerName = "@" + (extraRules+1);
                }

                numBRules++;
            }
        }
        else
        {
            if(tokens.length == 3)
            {
                unaryRules[numURules] = new Rule(prob, name, tokens[2].trim().toUpperCase(), true);
                numURules++;
            }
            else if(tokens.length == 4)
            {
                binaryRules[numBRules] = new Rule(prob, name, tokens[2].trim().toUpperCase(), tokens[3].trim().toUpperCase(), true);
                numBRules++;
            }
            else
                System.err.println("ERROR: Only " + tokens.length + " tokens detected in " + rule);
        }
    }

    public String[] nonterms()
    {
        return nonterms;
    }

    public int numNonTerms()
    {
        return numNonTerms;
    }

    public Rule[] binaryRules()
    {
        return binaryRules;
    }

    public int numBRules()
    {
        return numBRules;
    }

    public Rule[] unaryRules()
    {
        return unaryRules;
    }

    public int numURules()
    {
        return numURules;
    }

    public double getProb(String gr)
    {
        String[] tokens = gr.split(" |->");

        if(tokens.length == 2)
        {
            for (Rule rule : unaryRules)
                if(rule.equals(tokens[0], tokens[1]))
                    return rule.prob();
        }
        else if(tokens.length == 3)
        {
            for (Rule rule : binaryRules)
                if(rule.equals(tokens[0], tokens[1], tokens[2]))
                    return rule.prob();
        }
        else
        {
            System.err.println("ERROR: gr " + gr + " has " + tokens.length + " detected tokens");
            return -1;
        }

        return -1;
    }

    public int numRules()
    {
        return numBRules + numURules;
    }
}