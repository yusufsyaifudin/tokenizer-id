package yusufs.skripsi;
//#Tokenizer
//Merupakan kode untuk memecah teks kedalam sekumpulan kalimat dan/atau kata.
//
//Ditulis mulai Sabtu, 2 Mei 2015 8:50 PM
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
*
* @author Yusuf Syaifudin
*/
public class Tokenizer {
	
	// ## extractSentence()
	// Memecah teks menjadi array kalimat-kalimat yang menyusun teks 
	// sesuai urutannya saat menyusun teks tersebut.
	// Menggunakan regular expression.
	/**
	 * Memecah teks menjadi array kalimat-kalimat yang menyusun teks 
	 * sesuai urutannya saat menyusun teks tersebut. 
	 * Menggunakan regular expression.
	 * 
	 * @param text teks yang akan dipecah kalimatnya
	 * @return ArrayList array string berisi kalimat-kalimat yang menyusun teks.
	 */
	public ArrayList<String> extractSentence(String text) {
		ArrayList<String> result = new ArrayList<>();
		// Pattern regular expression untuk mengekstrak kalimat.
		// ```(?!\Z)[\n\r]*((?:[^."]*"[^"]*")+[^."]*(?:\.|\Z))|([^.!?\s][^.!?]*(?:[.!?](?!['"]?\s|$)[^.!?]*)*[.!?]?['"]?(?=\s|$))```
		// Sumber: http://stackoverflow.com/questions/5553410/regular-expression-match-a-sentence 
		// dan http://stackoverflow.com/questions/19979272/regex-to-match-all-sentences-with-quotes-in-them diakses pada 2 Mei 2015 9:18 PM
		String pattern = "(?!\\Z)[\\n\\r]*((?:[^.\"]*\"[^\"]*\")+[^.\"]*(?:\\.|\\Z))|"
				+ "([^.!?\\s]" // First char is non-punct, non-ws
				+ "[^.!?]*" // Greedily consume up to punctuation.
				+ "(?:" // Group for unrolling the loop.
				+ "[.!?]" // (special) inner punctuation ok if
				+ "(?!['\"]?\\s|$)" // not followed by ws or EOS.
				+ "[^.!?]*" // Greedily consume up to punctuation.
				+ ")*" // Zero or more (special normal*)
				+ "[.!?]?" // Optional ending punctuation.
				+ "['\"]?" // Optional closing quote.
				+ "(?=\\s|$))";
		// Jalankan regex.
		Pattern re = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.COMMENTS);
		
		// Replace dulu boundary.
		MappedContent mc = replaceBoundary(text);
		String txt = mc.getContent();
		LinkedHashMap<String, String> map = mc.getMap();

		// Pertama ekstrak dulu kalimat yang hanya single quote.
		String[] multiSentences = sentenceQuoteInOneLineEnders(txt);
		
		for(String ms : multiSentences) {
			Matcher reMatcher = re.matcher(ms);
			
			// Lakukan perulangan untuk menemukan pattern yang sesuai.
			// Simpan kedalam array of string.
			// Fungsi trim() digunakan untuk menghapus leading and trailing space
			// http://docs.oracle.com/javase/7/docs/api/java/lang/String.html#trim%28%29
			while (reMatcher.find()) {
				if(!reMatcher.group().trim().equals("")) {
					result.add(reMatcher.group().trim());
				}
				
			}
		}
		
		ArrayList<String> finalResult = new ArrayList<>();
		// Setelah semuanya didapatkan, kemudian unreplace boundary dengan teks asli.
		for(String r : result) {
			String unMap = unreplaceBoundary(r, map);
			finalResult.add(unMap);
		}
		return finalResult;
	}
	
	/**
	 * Untuk memisah kalimat dalam paragraf yang hanya berisi kutipan dalam satu paragraf.
	 * Terkadang, ada kasus khusus dimana paragraf hanya berisi satu kutipan yang diakhiri dengan titik, contoh: 
	 * `"It is better to lead from behind and to put others in front, especially when you celebrate victory when nice things occur. You take the front line when there is danger. Then people will appreciate your leadership."`
	 * Di situ titik berakhir sebelum tanda " (kutip) dan sesudah tanda kutip tidak ada keterangan lagi, 
	 * namun langsung memulai paragraf baru (yang tentu saja memakai huruf kapital di awal kalimat).
	 * 
	 * Untuk itu, ini akan memisahkan kalimat-kalimat berdasarkan kasus khusus itu dahulu sebelum di ekstrak dengan cara yang umum.
	 * 
	 * Sumber: http://stackoverflow.com/questions/8465335/a-regex-for-extracting-sentence-from-a-paragraph-in-python
	 * Direct link: http://stackoverflow.com/a/8465617 oleh http://stackoverflow.com/users/459543/riccardo-murri
	 * Diakses pada: 15 Mei 2015 8:33PM
	 * 
	 * 
	 * @param text
	 * @return
	 */
	private String[] sentenceQuoteInOneLineEnders(String text) {
		String[] splitted = text.split("(?<=[.!?]['\"])\\s*(?=[A-Z])");
		return splitted;
	}
	
	
	/**
	 * Karena berita terkadang berasal dari hasil grabbing website, terkadang
	 * ada titik yang lengket dengan kalimat selanjutnya. Misal:
	 * `Kalimat 1 blablabla.Kalimat 2 blablabla.`
	 * Seharusnya titik terpisah, tetapi untuk `.com` dan lainnya harusnya tetap tergabung.
	 * Untuk itu perlu ditambah pengecualian dulu untuk yang .com dan semacamnya
	 * @param text
	 * @return
	 */
	private String addSpaceAfterDot(String text) {
		// Regex  untuk daftar pengecualian.
		// `(\.\.\.|\.COM)`
		// `[+-]?(?=\d*[.eE])(?=\.?\d)\d*\.?\d*(?:[eE][+-]?\d+)?` regex untuk floating point number
		// Sumber: `http://stackoverflow.com/questions/13252101/regular-expressions-match-floating-point-number-but-not-integer`
		String regex = "(\\.\\.\\." // elipsis ...
				+ "|\\.COM" // .COM versi kapital
				+ "|\\.com" // .com versi lowercase
				+ "|\\.NET"
				+ "|\\.net"
				+ "|\\.CO"
				+ "|\\.co"
				+ "|\\.ID"
				+ "|\\.id"
				+ "|\\.ORG"
				+ "|\\.org"
				+ "|\\.GO"
				+ "|\\.go"
				+ "|\\.AC"
				+ "|\\.ac"
				+ "|\\.TV"
				+ "|\\.tv"
				+ "|\\.ME"
				+ "|\\.me"
				+ "|\\.MY"
				+ "|\\.my"
				+ "|\\.Kom" // untuk S.Kom
				+ "|\\.Sos" // untuk S.Sos
				+ "|\\.Pd" // untuk S.Pd
				+ ")"
				+ "|("
				+ "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?"
				+ ")";
		
		// Jalankan regex.
		Pattern re = Pattern.compile(regex, Pattern.MULTILINE | Pattern.COMMENTS);
		Matcher reMatcher = re.matcher(text);
		
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		// Untuk setiap pattern yang ditemukan, simpan dulu.
		while(reMatcher.find()) {
			String key = RandomStringGenerator.generateRandomString(10, RandomStringGenerator.Mode.ALPHA);
			String match = reMatcher.group().trim();
			// Simpan key ke HashMap untuk melakukan replace ke teks asli.
			map.put(key, match);
		}
		
		// Sekarang lakukan replacing
		for(Entry<String, String> mapEntry : map.entrySet()) {
			String newChar = mapEntry.getKey();
			String oldChar = mapEntry.getValue();
			text = text.replaceFirst(oldChar, newChar);
		}
		
		// Sekarang variable `text` sudah berisi konten yang sudah direplace.
		// Lakukan proses penambahan spasi untuk semua titik (`.`) yang tidak masuk daftar pengecualian.
		text = text.replaceAll("\\.", ". ");
		
		MappedContent mappedContent = new MappedContent(text, map);
		return unreplaceBoundary(mappedContent.getContent(), map);
	}
	
	// Mereplace semua yang berupa angka dan berpotensi "merusak" kalimat.
	// Contoh nomor surat: 1/POJK.7/2013 ketika berada dalam suatu kalimat, akan membuat kalimat berhenti di titik tersebut.
	// Untuk itu, fungsi ini digunakan untuk "mengamankan" dulu nomor surat tersebut pada `1/POJK.7`
	// Fungsi ini support untuk boundary seperti: `600.00`, `1/A/20.23`, `A.12/12`, atau `A.A/12`.
	/**
	 * Mereplace semua yang berupa angka dan berpotensi "merusak" kalimat.
	 * Contoh nomor surat: 1/POJK.7/2013 ketika berada dalam suatu kalimat, akan membuat kalimat berhenti di titik tersebut.
	 * Untuk itu, fungsi ini digunakan untuk "mengamankan" dulu nomor surat tersebut pada `1/POJK.7`
	 * Fungsi ini support untuk boundary seperti: `600.00`, `1/A/20.23`, `A.12/12`, atau `A.A/12`.
	 * @param textToReplace
	 * @return
	 */
	private MappedContent replaceBoundary(String textToReplace) {
		// Pastikan setiap titik terpisah kecuali yang masuk daftar pengecualian.
		textToReplace = addSpaceAfterDot(textToReplace);
		
		// Regex untuk nomor seperti nomor surat.
		// Regex: `(\b((\d|\w)[^\s]*[.]\w+)\b)`
		String regexReplacer = "(\\b((\\d|\\w)[^\\s]*[.]\\w+)\\b)";

		// Regex untuk boundary seperti nama jalan atau nomor rumah, gelar didepan nama.
		// `(No\.| Lt\.| Jl\.)\s+(\d+|\w+)`
		String regexBoundaryFront = "(No\\." // Contoh: No. 60
				+ "| Lt\\." // Contoh: Lt. 5
				+ "| Jl\\." // Contoh: Jl. Rasuna (pada konteks: Jl. Rasuna said)
				+ "| Dr\\." // Dr. Wahidin
				+ "| DR\\."
				+ "| Ir\\." // Ir. Soekarno
				+ "| Mr\\." // Mr. Yusuf
				+ "| Mrs\\."
				+ "| Ms\\."
				+ "| Jr\\."
				+ "| Sr\\."
				+ "| Prof\\."
				+ "| H\\." // H. Lulung
				+ "| Hj\\."
				+ "| W\\." // Maksum W. Kusumah
				+ "| Tn\\."
				+ "| Ny\\."
				+ "| M\\." // M. Najib
				+ "| H\\.M\\." // H.M. Prasetyo
				+ ")\\s+" // Semua karakter whitespace \r \n \t \f
				+ "(\\w+)"; // Untuk semua angka ataupun huruf setelah whitespace.

		// Regex untuk gelar di belakang.
		// Tidak perlu menulis titik di paling belakang.
		String regexBoundaryEnd = "(S\\.Kom" // Untuk S.Kom
				+ "| S\\.Pd"
				+ "| S\\.Sos" // HM Husni Maderi S.Sos
				+ "| \\.\\.\\.)";
		
		// Gabungan kedua regex.
		String regex = regexReplacer + "|" + regexBoundaryEnd + "|" + regexBoundaryFront;
		
		// Jalankan regex.
		Pattern re = Pattern.compile(regex, Pattern.MULTILINE | Pattern.COMMENTS);
		Matcher reMatcher = re.matcher(textToReplace);
		
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		// Untuk setiap pattern yang ditemukan, simpan dulu.
		while(reMatcher.find()) {
			String key = RandomStringGenerator.generateRandomString(10, RandomStringGenerator.Mode.ALPHA);
			String match = reMatcher.group().trim();
			// Simpan key ke HashMap untuk melakukan replace ke teks asli.
			map.put(key, match);
		}
		
		// Sekarang lakukan replacing
		for(Entry<String, String> mapEntry : map.entrySet()) {
			String newChar = mapEntry.getKey();
			String oldChar = mapEntry.getValue();
			textToReplace = textToReplace.replaceFirst(oldChar, newChar);
		}
		
		return new MappedContent(textToReplace, map);
	}
	
	/**
	 * Me-unreplace kata yang sudah di mapping untuk menghasilkan text asli yang sudah di mapping.
	 * @param mappedContent
	 * @param map
	 * @return
	 */
	private String unreplaceBoundary(String mappedContent, LinkedHashMap<String, String> map) {
		String unMapped = mappedContent;
		for(Entry<String, String> entry : map.entrySet()) {
			unMapped = unMapped.replaceFirst(entry.getKey(), entry.getValue());
		}
		return unMapped;
	}
	
	/**
	 * Memecah kalimat kedalam sekumpulan token yang menyusun kalimat 
	 * sesuai dengan urutannya saat menyusun kalimat.
	 * Tanda baca akan dipecah menjadi sebuah token tersendiri 
	 * untuk menghindari tagging yang salah
	 * 
	 * @param sentence
	 * @param withPunctuation apakah tanda baca diikutkan atau tidak.
	 * @return ArrayList string token penyusun kalimat.
	 */
	public ArrayList<String> tokenize(String sentence, Boolean withPunctuation) {
		if(withPunctuation == true) {
			return this.tokenize(sentence);
		} else {
			// Pakai regex yang hanya mendapatkan nilai untuk alpha-numeric (tanda baca tidak disertakan dan diganti spasi), 
			// kemudian dipecah berdasarkan spasi.
			String[] words = sentence.replaceAll("[^a-zA-Z0-9 ]", " ").split("\\s+");
			// Simpan token kedalam array list
			ArrayList<String> token = new ArrayList<>();
			for(String word : words) {
				token.add(word.trim());
			}
			
			// Hapus nilai array yang null atau ""
			token.removeAll(Arrays.asList(null, ""));
			return token;
		}
		
	}
	
	// Memecah kalimat kedalam sekumpulan token yang menyusun kalimat 
	// sesuai dengan urutannya saat menyusun kalimat.
	// Tanda baca akan dipecah menjadi sebuah token tersendiri 
	// untuk menghindari tagging yang salah
	/**
	 * Memecah kalimat kedalam sekumpulan token yang menyusun kalimat 
	 * sesuai dengan urutannya saat menyusun kalimat.
	 * Tanda baca akan dipecah menjadi sebuah token tersendiri 
	 * untuk menghindari tagging yang salah
	 * @param sentence
	 * @return ArrayList string token penyusun kalimat.
	 */
	public ArrayList<String> tokenize(String sentence) {
		// Variable untuk menyimpan hasil tokenisasi final
		ArrayList<String> wordToken = new ArrayList<>();
		
		
		// Pecah berdasarkan spasi.
		// Misal: "Apa kabar?" kata Budi.
		// Akan dipecah menjadi `"Apa`, `kabar?"`, `kata` dan `Budi.`
		String[] words;
		words = sentence.split("\\s");
		// Untuk setiap hasil pecah (kata), cari tanda baca (punctuation) di depan dan belakang.
		// Untuk setiap kata, pastikan setiap tanda baca di awal dan di akhir dijadikan token sendiri.
		for(String word : words) {
			ArrayList<String> wordParsedPunctAtBeginList;
			
			// Pertama cek diawal kata.
			// Misal untuk kata `"Apa` maka akan dipecah menjadi `"` dan `Apa`, 
			// sedangkan `kabar?"` masih tetap `kabar?"`.
			wordParsedPunctAtBeginList = this.parsePunctuationInStartOfWord(word);
			for(int i=0; i<wordParsedPunctAtBeginList.size(); i++) {
				String wordParsedPunctAtBegin = wordParsedPunctAtBeginList.get(i).toString();
				
				// Pecah tanda baca di akhir kalimat, kini `kabar?` menjadi `kabar` dan `?`.
				ArrayList<String> wordParsedAllPunctList = this.parsePunctuationInEndOfWord(wordParsedPunctAtBegin);
				for(int j=0; j<wordParsedAllPunctList.size(); j++) {
					// Dapatkan semua kata yang telah terurai setiap tanda baca di awal dan akhir kalimat, 
					// kemudian simpan di variable hasil (wordToken).
					String wordParsedAllPunct = wordParsedAllPunctList.get(j).toString().trim();
					wordToken.add(wordParsedAllPunct);
				}
				
			}
		}
		
		return wordToken;
	}
	
	
	/**
	 * Tokenisasi ke dalam string lagi.
	 * @param sentence
	 * @param withPunctuation
	 * @return
	 */
	public String tokenizeToString(String sentence, Boolean withPunctuation) {
		String output = "";
		ArrayList<String> tokens = tokenize(sentence, withPunctuation);
		for(String token : tokens) {
			output += token + " ";
		}
		
		return output;
	}
	
	// Memisahkan dan mengelompokkan tanda baca dibelakang kata (word)
	// sebagai token tersendiri. Tanda baca yang sama dan muncul berurutan akan dikelompokkan 
	// kedalam token sendiri. Contoh: `Kamu...!!!` akan menjadi `Kamu`, `...`, dan `!!!`.
	/**
	 * Memisahkan dan mengelompokkan tanda baca dibelakang kata (word)
	 * sebagai token tersendiri. Tanda baca yang sama dan muncul berurutan akan dikelompokkan 
	 * kedalam token sendiri.
	 * 
	 * Ini juga akan berarti bahwa ketika ada kata dengan tanda baca `...` akan dijadikan token sendiri:
	 * `Apa...` akan menjadi `Apa` dan `...`. Ingat bahwa `...` merupakan tanda baca yang legal dalam bahasa Indonesia.
	 * 
	 * Thanks to Achmad Jeihan Pahlevi (5 Mei 2015 11:17PM) yang telah mau bantu untuk pengelompokkannya.
	 * 
	 * @param word
	 * @return array list berupa string dari group group yang ada
	 */
	private ArrayList<String> parsePunctuationInEndOfWord(String word) {
		// Variabel result untuk hasilnya.
		ArrayList<String> result = new ArrayList<>();
		
		// Pattern regex untuk mendapatkan group antara kata dengan tanda baca dipaling akhir kata.
		// Misal: `Halo...!!!`
		String pattern = "(.*)(\\p{Punct})$";
		
		// Dengan menjalankan regex, akan terdapat dua group hasil operasi regex.
		// group1: `Halo...!!` dan group2: `!`
		String group1 = word.replaceFirst(pattern, "$1");
		String group2 = word.replaceFirst(pattern, "$2");
		
		// Membuat variabel sementara untuk menyimpan hasil regex.
		StringBuilder temp = new StringBuilder();
		
		// Diulangi selama group1 dan group2 masih bernilai beda.
		// Jika tanda baca diakhir kata telah ditemukan semua, maka group1 akan sama dengan group2.
		while(group1 != group2) { 
			// Masukkan ke variabel yang menyusun string.
			temp.append(group2);
			
			// Ganti variabel word dengan group1 (`Halo...!`) dan lalukan operasi regex lagi.
			// Operasi ini akan diulangi hingga variable group1 dan group2 sama (while loop akan bernilai false).
			word = group1;
			group1 = word.replaceFirst(pattern, "$1");
			group2 = word.replaceFirst(pattern, "$2");
		}
		
		// Simpan dahulu group1 yang merupakan kata (word) yang sudah dihilangkan tanda baca diakhirnya.
		result.add(group1.trim());
		
		// Reverse string. Dimana hasil regex akan menghasilkan kebalikannya yaitu menjadi: `!!!...` 
		// maka perlu dibalik menjadi sesuai urutannya dalam kata `...!!!`
		String punct = temp.reverse().toString();
		
		
		// Sekarang lalukan Grouping. Buat variabel sementara dahulu.
		String punctGroup = "";
		

		// Lakukan perulangan dari nol sampai kurang dari panjang string (punctuation).
		for(int i=0; i<punct.length(); i++) {
			
			// Dapatkan karakter pertama.
			char c = punct.charAt(i);
			
			// Jika iterasi baru dimulai, append saja ke variable sementara.
			if(i == 0) {
				punctGroup += c;
			} else {
				
				// Dapatkan karakter paling akhir dari variable punctGroup yang berarti variable sementara untuk menyimpan group.
				char cPunct = punctGroup.charAt(punctGroup.length() - 1);
				
				// Jika sama, append. Dan jika beda maka tambahkan ke hasil (variable result) 
				// lalu kosongkan variable punctGroup untuk digunakan lagi.
				// Kemudian append karakter yang beda tersebut.
				if(c == cPunct) {
					punctGroup += c;
				} else {
					result.add(punctGroup.trim());
					punctGroup = "";
					punctGroup += c;
				}
			}

		}
		
		// Pada akhirnya tambahkan ke hasil akhir untuk memastikan bahwa append string yang terakhir sudah masuk ArrayList.
		result.add(punctGroup.trim());
		// Hapus semua nilai dalam array list yang null atau "" (kosong). Lalu kembailkan nilainya.
		result.removeAll(Arrays.asList(null, ""));
		return result;
	}
	
	
	// Memisahkan dan mengelompokkan tanda baca didepan kata (word)
	// sebagai token tersendiri. Tanda baca yang sama dan muncul berurutan akan dikelompokkan 
	// kedalam token sendiri. Contoh: `"Dia` akan menjadi `"` dan `Dia`. 
	// Dalam dunia nyata, ini akan sering terjadi pada kalimat kutipan langsung 
	// (apalagi jika pemecahan kalimat hanya berdasarkan spasi, maka tanda baca akan selalu menempel pada kata.
	/**
	 * Memisahkan dan mengelompokkan tanda baca didepan kata (word)
	 * sebagai token tersendiri. Tanda baca yang sama dan muncul berurutan akan dikelompokkan 
	 * kedalam token sendiri. Contoh: `"Dia` akan menjadi `"` dan `Dia`. 
	 * Dalam dunia nyata, ini akan sering terjadi pada kalimat kutipan langsung 
	 * (apalagi jika pemecahan kalimat hanya berdasarkan spasi, maka tanda baca akan selalu menempel pada kata.
	 * 
	 * @param word
	 * @return
	 */
	private ArrayList<String> parsePunctuationInStartOfWord(String word) {
		// Variabel result untuk hasilnya.
		ArrayList<String> result = new ArrayList<>();
		
		// Pattern regex untuk mendapatkan group antara kata dengan tanda baca dipaling depan kata.
		// Misal: `"Dia`
		String pattern = "^(\\p{Punct})(.*)$";
		
		// Dengan menjalankan regex, akan terdapat dua group hasil operasi regex.
		// group1: `"` dan group2: `Dia`
		String group1 = word.replaceFirst(pattern, "$1");
		String group2 = word.replaceFirst(pattern, "$2");
		
		// Membuat variabel sementara untuk menyimpan hasil regex.
		StringBuilder temp = new StringBuilder();
		
		// Diulangi selama group1 dan group2 masih bernilai beda.
		// Jika tanda baca diakhir kata telah ditemukan semua, maka group1 akan sama dengan group2.
		while(group1 != group2) { 
			// Masukkan ke variabel yang menyusun string.
			temp.append(group1);
			// Ganti variabel word dengan group2 (`Dia`) dan lalukan operasi regex lagi.
			// Operasi ini akan diulangi hingga variable group1 dan group2 sama (while loop akan bernilai false).
			// Dalam kasus `"Dia` hanya akan terdapat satu kali iterasi, pertama group1: `"` dan group2: `Dia`, 
			// kemudian di iterasi kedua group1: `Dia` dan group2: `Dia` (iterasi selesai).
			word = group2;
			group1 = word.replaceFirst(pattern, "$1");
			group2 = word.replaceFirst(pattern, "$2");
		}
		
		// Simpan ke variable punct, tidak perlu di reverse karena urutannya telah sesuai.
		String punct = temp.toString();
		
		// Lakukan grouping terhadap karakter yang sama.
		String punctGroup = "";
		
		
		// Lakukan perulangan dari nol sampai kurang dari panjang string (punctuation).
		for(int i=0; i<punct.length(); i++) {
			
			// Dapatkan karakter pertama.
			char c = punct.charAt(i);
			
			// Jika iterasi baru dimulai, append saja ke variable sementara.
			if(i == 0) {
				punctGroup += c;
			} else {
				
				// Dapatkan karakter paling akhir dari variable punctGroup yang berarti variable sementara untuk menyimpan group.
				char cPunct = punctGroup.charAt(punctGroup.length() - 1);
				
				// Jika sama, append. Dan jika beda maka tambahkan ke hasil (variable result) 
				// lalu kosongkan variable punctGroup untuk digunakan lagi.
				// Kemudian append karakter yang beda tersebut.
				if(c == cPunct) {
					punctGroup += c;
				} else {
					result.add(punctGroup.trim());
					punctGroup = "";
					punctGroup += c;
				}
			}

		}
		
		// Pada akhirnya tambahkan ke hasil akhir untuk memastikan 
		// bahwa append string yang terakhir sudah masuk ArrayList.
		result.add(punctGroup.trim());
		
		// Tambahkan kata yang sudah tidak ada tanda bacanya, yaitu group2: `Dia` (meski group1 juga sudah bernilai `Dia`).
		result.add(group2);
		
		// Hapus semua nilai dalam array list yang null atau "" (kosong). Lalu kembailkan nilainya.
		result.removeAll(Arrays.asList(null, ""));
		return result;
	}
	
	/**
	 * Class untuk mapped content.
	 * @author Yusuf Syaifudin
	 *
	 */
	private class MappedContent {
		private String content;
		private LinkedHashMap<String, String> map;
		
		public MappedContent(String content, LinkedHashMap<String, String> map) {
			setContent(content);
			setMap(map);
		}
		
		/**
		 * @return the content
		 */
		public String getContent() {
			return content;
		}
		
		/**
		 * @param content the content to set
		 */
		private void setContent(String content) {
			this.content = content;
		}

		/**
		 * @return the map
		 */
		public LinkedHashMap<String, String> getMap() {
			return map;
		}

		/**
		 * @param map the map to set
		 */
		private void setMap(LinkedHashMap<String, String> map) {
			this.map = map;
		}
		
		@Override
		public String toString() {
			return getContent();
		}
	}
}

