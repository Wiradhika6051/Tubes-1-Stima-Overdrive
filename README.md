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
1. Modifikasi kode pada `Bot.java` di folder `starter-bots/java/src/main/java/za/co/entelect/challenge`
2. Buka sidetab Maven di sebelah kanan, buka folder `lifecycle` > `install`. Tunggu hingga kompilasi selesai.

### Mempertandingkan Bot
Untuk mempertandingkan bot, digunakan program yang terdapat pada [2020-Overdrive](https://github.com/EntelectChallenge/2020-Overdrive), atau langsung di tree [main](https://github.com/Wiradhika6051/Tubes-1-Stima-Overdrive/tree/main) dari repositori submisi kami.

Langsung dari tree `main`:
1. Ubah lokasi bot `player-a` dan `player-b` pada `game-runner-config.json`
1. Jalankan `run.bat`

## Author
Dibuat oleh:
1. Jeremy S.O.N. Simbolon (13520042)
2. Fawwaz Anugrah Wiradhika Dharmasatya (13520086)
3. Alifia Rahmah (13520122)