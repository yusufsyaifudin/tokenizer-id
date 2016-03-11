/**
 * Membuat kalimat dari kumpulan token.
 * Sumber: http://codereview.stackexchange.com/questions/11116/build-a-sentence-from-tokens-words-in-a-string-array
 * diakses pada 2:49PM August 17, 2015 (70 tahun INDONESIA MERDEKA!!!)
 * jawaban http://codereview.stackexchange.com/revisions/11137/4
 * 
 * dengan penambahan method.
 */
package yusufs.nlp.tokenizerid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

/**
 * @author Yusuf Syaifudin
 *
 */
public class Detokenizer {

	/**
	 * 
	 */
	public Detokenizer() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Mengembalikan ArrayList<String> yang berisi token menjadi string kalimat seperti sebelum ditokenisasi
	 * @param tokens
	 * @return
	 */
	public String detokenize(ArrayList<String> tokens) {
        //Define list of punctuation characters that should NOT have spaces before or after 
        List<String> noSpaceBefore = new LinkedList<String>(Arrays.asList(",", ".",";", ":", ")", "}", "]", "!", "?"));
        List<String> noSpaceAfter = new LinkedList<String>(Arrays.asList("(", "[","{", "\"",""));

        StringBuilder sentence = new StringBuilder();

        tokens.add(0, "");  //Add an empty token at the beginning because loop checks as position-1 and "" is in noSpaceAfter
        for (int i = 1; i < tokens.size(); i++) {
            if (noSpaceBefore.contains(tokens.get(i))
                    || noSpaceAfter.contains(tokens.get(i - 1))) {
                sentence.append(tokens.get(i));
            } else {
                sentence.append(" " + tokens.get(i));
            }

            // Assumption that opening double quotes are always followed by matching closing double quotes
            // This block switches the " to the other set after each occurrence
            // ie The first double quotes should have no space after, then the 2nd double quotes should have no space before
            if ("\"".equals(tokens.get(i - 1))) {
                if (noSpaceAfter.contains("\"")) {
                    noSpaceAfter.remove("\"");
                    noSpaceBefore.add("\"");
                } else {
                    noSpaceAfter.add("\"");
                    noSpaceBefore.remove("\"");
                }
            }
        }
        return sentence.toString();
    }

}
