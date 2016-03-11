package tokenizerid;

import org.junit.Assert;

import java.util.ArrayList;

import org.junit.Test;

import yusufs.nlp.tokenizerid.Detokenizer;
import yusufs.nlp.tokenizerid.Tokenizer;

public class TokenizerIdTest {
	
	Tokenizer tokenizer = new Tokenizer();
	Detokenizer detokenizer = new Detokenizer();

	@Test
	public void testTokenizerSentences() {
		String text = "Kalimat satu. Kalimat dua. \"Selamat pagi!\" kata X.";
    	ArrayList<String> sentences = tokenizer.extractSentence(text);
    	
    	Assert.assertEquals(sentences.get(0), "Kalimat satu.");
    	Assert.assertEquals(sentences.get(1), "Kalimat dua.");
    	Assert.assertEquals(sentences.get(2), "\"Selamat pagi!\" kata X.");
	}
	
	@Test
	public void testTokenizerToken() {
		String sentence = "\"Selamat pagi!\" kata X.";
    	Boolean withPunct = true; // apakah tanda baca diikut-sertakan atau tidak
    	ArrayList<String> tokens = tokenizer.tokenize(sentence, withPunct);
    	
    	Assert.assertEquals(tokens.get(0), "\"");
    	Assert.assertEquals(tokens.get(1), "Selamat");
    	Assert.assertEquals(tokens.get(2), "pagi");
    	Assert.assertEquals(tokens.get(3), "!");
    	Assert.assertEquals(tokens.get(4), "\"");
    	Assert.assertEquals(tokens.get(5), "kata");
    	Assert.assertEquals(tokens.get(6), "X");
    	Assert.assertEquals(tokens.get(7), ".");
    	
    	String tokensToString = tokenizer.tokenizeToString(sentence, withPunct);
    	Assert.assertEquals(tokensToString.contains("\" Selamat pagi ! \" kata X ."), true);
	}
	
	@Test
	public void testDetokenizer() {
		String sentence = "\"Selamat pagi!\" kata X.";
		Boolean withPunct = true; // apakah tanda baca diikut-sertakan atau tidak
		ArrayList<String> tokens = tokenizer.tokenize(sentence, withPunct);
		
		String detoksentence = detokenizer.detokenize(tokens);
		
		Assert.assertEquals(sentence, detoksentence);
	}

}
