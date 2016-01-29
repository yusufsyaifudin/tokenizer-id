# Tokenizer for Bahasa Indonesia

Kelas Tokenizer akan memecah sekumpulan teks menjadi kalimat dan sekumpulan kalimat menjadi _token_. Kelas Tokenizer ini merupakan kelas yang saya buat sendiri untuk keperluan skripsi saya.

## Instalasi

Untuk menginstall, tambahkan kode berikut pada berkas `pom.xml`:

```
<repositories>
    <repository>
      <id>yusufsyaifudin</id>
      <name>tokenizer-id</name>
      <url>https://github.com/yusufsyaifudin/tokenizer-id/raw/master/</url>
    </repository>
</repositories> 
```

dan kode berikut pada _dependency_ `pom.xml`

```
<dependencies>
    <dependency>
      <groupId>yusuf.skripsi</groupId>
      <artifactId>tokenizerId</artifactId>
      <version>1.0.0</version>
      <scope>compile</scope>
    </dependency>
</dependencies>
```

## Penggunaan
### Pecah teks menjadi sekumpulan kalimat

```
String text = "Kalimat satu. Kalimat dua. \"Selamat pagi!\" kata X.";
Tokenizer tokenizer = new Tokenizer();
ArrayList<String> sentences = tokenizer.extractSentence(text);
```

sehingga _variable_ `sentences` akan berisi _array_ dengan nilai:
* `Kalimat satu.`
* `Kalimat dua.`
* `"Selamat pagi!" kata X.`

### Pecah kalimat menjadi sekumpulan token

```
String sentence = "\"Selamat pagi!\" kata X.";
Boolean withPunct = true; // apakah tanda baca diikut-sertakan atau tidak
Tokenizer tokenizer = new Tokenizer();
ArrayList<String> tokens = tokenizer.tokenize(sentence, withPunct);
```

sehingga `tokens` akan berisi:
* `"`
* `Selamat`
* `pagi`
* `!`
* `"`
* `kata`
* `X`
* `.`

atau dapat juga tokenisasi menjadi string yaitu:

```
String sentence = "\"Selamat pagi!\" kata X.";
Boolean withPunct = true; // apakah tanda baca diikut-sertakan atau tidak
Tokenizer tokenizer = new Tokenizer();
String tokens = tokenizer.tokenizeToString(sentence, withPunct);
```
sehingga `tokens` menjadi `" Selamat pagi ! " kata X .` dimana setiap token telah dipisah oleh spasi.


## Contoh
Contoh penggunaan bisa dilihat pada gist yang saya buat disini [https://gist.github.com/yusufsyaifudin/4af421ccf269b11205ac](https://gist.github.com/yusufsyaifudin/4af421ccf269b11205ac)

<!-- mvn install:install-file -DgroupId=yusuf.skripsi -DartifactId=tokenizerId -Dversion=1.0.0 -Dpackaging=jar -Dfile=/home/yusuf/workspace/tokenizer-id/target/tokenizerId-1.0.0.jar -DlocalRepositoryPath=/home/yusuf/workspace/tokenizer-id -->
