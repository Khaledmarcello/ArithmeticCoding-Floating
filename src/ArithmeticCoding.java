import java.io.*;
import java.util.Scanner;
import java.util.Vector;

public class ArithmeticCoding {

    Vector<Probability> prob = new Vector<Probability>();

    public String ReadFromFile(String Path) throws IOException {
        String content = null;
        File file = new File(Path);
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }

    public void WrtieToFile(String content, String name) {
        BufferedWriter writer = null;
        String fileName = name + ".txt";
        File logFile = new File(fileName);
        try {
            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }

        }
    }

    public Probability SearchProb(Vector<Probability> vec, String content) {
        for (int i = 0; i < vec.size(); i++) {
            if (vec.elementAt(i).text.equals(content))
                return vec.get(i);
        }
        return null;
    }

    public void CalcProbability(String content) {
        for (int i = 0; i < content.length(); i++) {
            boolean found = false;
            for (int j = 0; j < prob.size(); j++) {
                if (prob.get(j).text.charAt(0) == content.charAt(i)) {
                    found = true;
                    prob.elementAt(j).count++;
                    break;
                }
            }
            if (!found) {
                String c = "" + content.charAt(i);
                int count = 1;
                Probability p = new Probability(c, count, 0, 1, 0.0);
                prob.add(p);
            }
        }
        for (int i = 0; i < prob.size(); i++) {
            prob.get(i).probability = prob.get(i).count / (content.length() + 0.0);
        }
    }

    public void SetUpperAndLower(int length) {
        double low = 0;
        for (int i = 0; i < prob.size(); i++) {
            prob.elementAt(i).lower = low;
            prob.elementAt(i).upper = low + (prob.elementAt(i).count / (length + 0.0));
            low = prob.elementAt(i).upper;
        }
    }

    public String Range(double code) {
        for (int i = 0; i < prob.size(); i++) {
            if (code < prob.get(i).upper && code > prob.get(i).lower) {
                return prob.get(i).text;
            }
        }
        return null;
    }

    public void Compress(String path, String name) {
        String text = "";
        try {
            text = ReadFromFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CalcProbability(text);
        SetUpperAndLower(text.length());

        double oldLower = 0, oldUpper = 1;
        double lower = 0, upper = 1;

        for (int i = 0; i < text.length(); i++) {
            Probability search = SearchProb(prob, text.charAt(i) + "");
            int index = prob.indexOf(search);
            lower = oldLower + (oldUpper - oldLower) * prob.elementAt(index).lower;
            upper = oldLower + (oldUpper - oldLower) * prob.elementAt(index).upper;
            oldLower = lower;
            oldUpper = upper;
        }
        double compressedValue = (oldLower + oldUpper) / 2;
        String compressedProbability = text.length() + " ";
        for (int i = 0; i < prob.size(); i++) {
            compressedProbability += prob.elementAt(i).text + " " + prob.elementAt(i).probability + " ";
        }
        WrtieToFile(compressedProbability + " " + compressedValue, name);
    }

    public void Decompress(String path, String name) {
        String text = "";
        double compressed = 0.0;
        String decompressed = "";
        try {
            text = ReadFromFile(path);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner input = new Scanner(text);
        int size = input.nextInt();
        Vector<String> vec = new Vector<String>();
        while (input.hasNext()) {
            String s = input.next();
            vec.add(s);
        }

        for (int i = 0; i < vec.size() - 1; i++) {
            Probability temp = new Probability(vec.get(i++), 0, 0, 0, Double.parseDouble(vec.get(i)));
            temp.count = (int) (Double.parseDouble(vec.get(i)) * size);
            prob.add(temp);
        }
        compressed = Double.parseDouble(vec.get(vec.size() - 1));

        SetUpperAndLower(size);

        double oldLower = 0, oldUpper = 1;
        double lower = 0, upper = 1;
        for (int i = 0; i < size; i++) {
            double code = (compressed - lower) / (upper - lower);
            String currChar = Range(code);
            Probability search = SearchProb(prob, currChar + "");
            int index = prob.indexOf(search);
            lower = oldLower + (oldUpper - oldLower) * prob.elementAt(index).lower;
            upper = oldLower + (oldUpper - oldLower) * prob.elementAt(index).upper;
            oldLower = lower;
            oldUpper = upper;
            decompressed += currChar;
        }


        WrtieToFile(decompressed, name);

    }

}
