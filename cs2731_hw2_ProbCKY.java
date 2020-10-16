import java.io.*;
import java.util.Scanner;

public class cs2731_hw2_ProbCKY
{
    public static void main(String [] args) throws IOException
    {
        try {
            File grammarFile = new File(args[0]);
            Scanner fr = new Scanner(grammarFile);

            Grammar g = new Grammar();
            Lexicon l = new Lexicon();

            boolean lexicon = false;

            while(fr.hasNextLine())
            {
                String data = fr.nextLine();

                if(data.trim().equals("Grammar") || data.trim().equals(""))
                    continue;
                else if(data.trim().equals("Lexicon"))
                {
                    lexicon = true;
                    continue;
                }

                if(lexicon)
                    l.addWord(data);
                else
                    g.addRule(data);
            }

            CKY cky = new CKY(g, l);
            String testSent = args[1];
            String idealParse = args[2];
            cky.setSentence(testSent, idealParse);

            System.out.println(cky.getParses());

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}