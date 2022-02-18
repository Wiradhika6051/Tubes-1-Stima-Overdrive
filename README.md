# Tubes-1-Stima-Overdrive

Implementasi algoritma *greedy* dalam pembuatan Bot Overdrive dari Entelect Challenge 2020 untuk memenuhi Tugas Besar 1 IF2211 Strategi Algoritma Semester 2 2021/2022.

## Penjelasan Algoritma

Algoritma *greedy* yang kami pilih adalah algoritma *greedy offensive*, yang memprioritaskan pengambilan power-up yang bersifat intrusif terhadap lawan pada fungsi seleksinya. 

Algoritma ini akan memilih jalur (lane) yang memiliki prospek power-up intrusif lebih tinggi ketika melakukan perpindahan jalur. Algoritma ini juga menekankan prospek adanya power-up intrusif ketika menjalankan collision avoidance logic serta menekankan penggunaan power-up intrusif di waktu yang tepat bila memungkinkan. 

Fungsi objektif dari strategi ini bertujuan untuk mendisrupsi lawan sebanyak-banyaknya sehingga lawan tidak dapat menuju FINISH secara efektif.

## Requirement Dasar

1. [Java](https://www.oracle.com/java/technologies/downloads/#java8)
2. [IntelliJ IDEA](https://www.jetbrains.com/idea/)
3. [NodeJS](https://www.oracle.com/java/technologies/downloads/#java8)

## Command

### Compile Bot
Modifikasi menggunakan IntelliJ IDEA:
1. Jika tidak ada, aktifkan tool Maven dengan klik View > Tool Windows > Maven pada toolbar.
2. Modifikasi kode pada `Bot.java` di folder `starter-bots/java/src/main/java/za/co/entelect/challenge`.
3. Jika tidak otomatis terdaftar, klik kanan pada `pom.xml` > Add as a Maven Project.
2. Buka tool Maven di sebelah kanan, buka folder `lifecycle` > `install`. Tunggu hingga kompilasi selesai.

### Mempertandingkan Bot
Untuk mempertandingkan bot, dapat digunakan program yang terdapat pada [2020-Overdrive](https://github.com/EntelectChallenge/2020-Overdrive), atau langsung di tree [main](https://github.com/Wiradhika6051/Tubes-1-Stima-Overdrive/tree/main) dari repositori submisi kami.

Langsung dari tree `main`:
1. Clone repository ini
2. Inisasi bot sebagai `player-a` atau `player-b` pada `game-runner-config.json`, dengan menaruh filepath dari `bot.json` (Jika repository di-clone di root folder, dapat diisi dengan `./Tubes1_にげろう/bin`)
3. Jalankan `run.bat`

Jika sudah terbentuk match-log, dapat divisualisasikan dengan mengunggah match-log dalam bentuk zip pada https://entelect-replay.raezor.co.za/.

## Known Bugs
Terdapat bug ketika nama file executable pada folder `bin` adalah `にげろう.jar`. Bot tidak dapat berjalan dengan baik ketika digunakan filename げろう, sehingga digunakan nama `nigerou.jar`, dengan nickname Bot tetap にげろう.

[Bacaan](https://stackoverflow.com/questions/14998507/how-to-run-an-executable-jar-when-its-path-name-contains-unicode-characters)

## Author
Dibuat oleh: Kelompok 26 - にげろう
1. Jeremy S.O.N. Simbolon (13520042)
2. Fawwaz Anugrah Wiradhika Dharmasatya (13520086)
3. Alifia Rahmah (13520122)