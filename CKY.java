import java.lang.*;

public class CKY
{
    private Grammar g;
    private Lexicon l;
    private String[] words;

    private ParseEvaluator ideal;
    private ParserNode[][][] nodes;
    private int[][] nodeCounts;

    public CKY(Grammar gram, Lexicon lex)
    {
        g = gram;
        l = lex;
    }

    public void setSentence(String sent, String i)
    {
        words = sent.split(" ");
        ideal = new ParseEvaluator(i, g);
        nodes = new ParserNode[words.length+1][words.length+1][g.numRules()];
        nodeCounts = new int[words.length+1][words.length+1];
    }

    public String getParses()
    {
        calcParses();
        return getTrees();
    }

    private void calcParses()
    {
        double p;
        Rule[] lex = l.lex();
        int numW = l.size();

        for(int i = 0; i < words.length; i++)
        {
            for(int j = 0; j < numW; j++)
            {
                Rule r = lex[j];
                if(r.lchild().equals(words[i].toLowerCase()))
                {
                    addNode(i, i+1, -1, r.prob(), r.name(), words[i].toLowerCase());
                }
            }
       
            handleUnaries(i, i+1);
        }

        for(int diff = 2; diff <= words.length; diff++)
        {
            for(int begin = 0; (begin+diff) <= words.length; begin++)
            {
                handleBinaries(begin, begin+diff);
                handleUnaries(begin, begin+diff);
            }

        }
        //DEBUG ONLY
        //printNodes();
    }

    private String getTrees()
    {
        String totalStr = "";
        String currTree = "";
        ParserNode node;

        ParseEvaluator candidate;
        double[] stats;
        double sentProb = 0;
        int numParses = 0;

        for(int i = 0; i < nodeCounts[0][words.length]; i++)
        {
            node = nodes[0][words.length][i];
            if(node.name().equals("S"))
            {
                sentProb += node.prob();
                currTree = treeToStr(0, words.length, i);

                candidate = new ParseEvaluator(currTree, g);
                numParses++;
                totalStr += "\nParse " + numParses + ":\n" + currTree;

                stats = candidate.compareToIdeal(ideal);
                totalStr += "\nProbability of parse tree: " + node.prob() + "\nRecall: " + stats[0] + "\tPrecision: " + stats[1] + "\n\n";
            }
        }

        if(sentProb == 0)
        {
            totalStr = "Sentence rejected\n";
            return totalStr;
        }

        totalStr = "\nSentence accepted\nSentence Probability: " + sentProb + "\n" + totalStr;
        return totalStr;
    }

    private boolean addNode(int idx1, int idx2, int split, int bi, int ci, double prob, String n, String lchild, String rchild, boolean orig)
    {
        boolean found = false;
        ParserNode node;

        for(int i = 0; i < nodeCounts[idx1][idx2]; i++)
        {  
            node = nodes[idx1][idx2][i];
            if(split == node.split() && bi == node.bIdx() && ci == node.cIdx() && node.equals(n, lchild, rchild))
                found = true;
        }

        if(!found)
        {
            nodes[idx1][idx2][nodeCounts[idx1][idx2]] = new ParserNode(split, bi, ci, prob, n, lchild, rchild, orig);
            nodeCounts[idx1][idx2]++;
            return true;
        }
        return false;
    }

    private boolean addNode(int idx1, int idx2, int uIdx, double prob, String n, String lchild)
    {
        boolean found = false;
        ParserNode node;

        for(int i = 0; i < nodeCounts[idx1][idx2]; i++)
        {  
            node = nodes[idx1][idx2][i];
            if(uIdx == node.bIdx() && uIdx != -1 && node.equals(n, lchild))
                found = true;
        }

        if(!found)
        {
            nodes[idx1][idx2][nodeCounts[idx1][idx2]] = new ParserNode(uIdx, prob, n, lchild);
            nodeCounts[idx1][idx2]++;
            return true;
        }
        return false;
    }

    private void handleUnaries(int first, int second)
    {
        if(nodeCounts[first][second] == 0)
            return;

        boolean added = true;
        ParserNode[] currNodes = nodes[first][second];
        Rule[] uRules = g.unaryRules();
        int numUn = g.numURules();
        double p;

        while(added)
        {
            added = false;

            for(int j = 0; j < numUn; j++)
            {
                Rule r = uRules[j];
                p = r.prob();
                
                for(int i = 0; i < nodeCounts[first][second]; i++)
                {
                    if(r.lchild().equals(currNodes[i].name()))
                    {
                        p *= currNodes[i].prob();
                        added = addNode(first, second, i, p, r.name(), currNodes[i].name());
                    }
                }
            }
        }
    }

    private void handleBinaries(int begin, int end)
    {
        ParserNode[] bNodes;
        ParserNode[] cNodes;
        Rule[] bRules = g.binaryRules(); 
        int numBin = g.numBRules();
        double p;

        for(int split = begin+1; split <= end-1; split++)
        {
            if(nodeCounts[begin][split] != 0 && nodeCounts[split][end] != 0)
            {
                bNodes = nodes[begin][split];
                cNodes = nodes[split][end];

                for(int j = 0; j < numBin; j++)
                {
                    Rule r = bRules[j];

                    p = r.prob();
                            
                    for(int b = 0; b < nodeCounts[begin][split]; b++)
                    {
                        for(int c = 0; c < nodeCounts[split][end]; c++)
                        {
                            if(r.lchild().equals(bNodes[b].name()) && r.rchild().equals(cNodes[c].name()))
                            {
                                p *= bNodes[b].prob()*cNodes[c].prob();
                                addNode(begin, end, split, b, c, p, r.name(), r.lchild(), r.rchild(), r.isOriginal());
                            }
                        }
                    }
                }
            }
        }
    }

    private String treeToStr(int begin, int end, int idx)
    {
        String s = "";
        String name = nodes[begin][end][idx].name();
        boolean original = nodes[begin][end][idx].isOriginal();

        int split = nodes[begin][end][idx].split();
        int bi = nodes[begin][end][idx].bIdx();
        int ci = nodes[begin][end][idx].cIdx();

        if(original)
            s += "[" + name + " ";

        // root, lexicon word
        if(bi == -1)
        {
            s += " " + nodes[begin][end][idx].lchild() + "]";
            return s;
        }
        else
        {
            // unary reference
            if(ci == -1)
                s += treeToStr(begin, end, bi);
            // binary reference
            else
            {
                s += treeToStr(begin, split, bi);
                s += " ";
                s += treeToStr(split, end, ci);
            }
        }

        if(original)
            s += "]";

        return s;
    }

    // CKY method used for debugging
    public void printNodes()
    {
        System.out.println("\nCurrent State of CKY Model:");
        for(int i = 0; i < words.length+1; i++)
        {
            // System.out.println("\n\n");

            for(int j = i+1; j < words.length+1; j++)
            {
                // System.out.print("\t\t");
                System.out.println("Box (" + i + "," + j + "):");

                for(int k = 0; k < nodeCounts[i][j]; k++)
                {
                    System.out.print("\t" + nodes[i][j][k].name() + " -> " + nodes[i][j][k].lchild());
                    if(nodes[i][j][k].rchild() != null)
                        System.out.print(" " + nodes[i][j][k].rchild());
                    System.out.println(" " + nodes[i][j][k].prob());
                }
            }
        }
    }

    private class ParserNode
    {
        private Rule r;
        // split determines how to reproduce parse tree at the end
        private int split;
        private int bIdx;
        private int cIdx;

        private ParserNode(int s, int bi, int ci, double p, String n, String lc, String rc, boolean o)
        {
            split = s;
            bIdx = bi;
            cIdx = ci;
            r = new Rule(p, n, lc, rc, o);
        }

        private ParserNode(int ui, double p, String n, String c)
        {
            split = -1;
            bIdx = ui;
            cIdx = -1;
            r = new Rule(p, n, c);
        }

        private double prob()
        {
            return r.prob();
        }

        private String name()
        {
            return r.name();
        }

        private String lchild()
        {
            return r.lchild();
        }

        private String rchild()
        {
            return r.rchild();
        }

        private int split()
        {
            return split;
        }

        private int bIdx()
        {
            return bIdx;
        }

        private int cIdx()
        {
            return cIdx;
        }

        private boolean isOriginal()
        {
            return r.isOriginal();
        }

        private boolean equals(String n, String child)
        {
            return r.equals(n, child);
        }

        private boolean equals(String n, String lc, String rc)
        {
            return r.equals(n, lc, rc);
        }
    }
}