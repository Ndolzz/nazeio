# Spesifikasi 03: Tanya Jawab dan Bantuan Koding

## Tujuan

Ucapan yang bukan perintah ringan dijawab oleh model bahasa melalui API, lalu dibacakan.

## Perilaku

1. Jika ucapan tidak cocok dengan aksi atau alias apa pun, ucapan diteruskan ke model bahasa.
2. Jawaban dibacakan dengan suara Piper.
3. Jawaban panjang diringkas saat dibacakan, teks lengkap tampil di layar.
4. Jawaban berisi kode hanya ditampilkan di layar, tidak dibacakan baris demi baris.
5. Percakapan disimpan sebagai riwayat dan dapat dihapus pengguna.
6. Pengguna dapat menghentikan suara kapan saja.
7. Jika internet tidak tersedia, Nazeio menjawab bahwa fitur ini butuh internet.

## Batasan

1. Penyedia API dapat dipilih di pengaturan, misalnya Gemini atau Claude.
2. Kunci API disimpan di Android Keystore.
3. Ada batas pemakaian harian yang dapat diatur, dan Nazeio memberi tahu bila hampir tercapai.
4. Instruksi sistem memuat identitas Nazeio, gaya jawaban singkat, dan bahasa Indonesia.

## Di luar cakupan

1. Menjalankan model besar langsung di HP.
2. Aksi pada layanan luar. Itu ada di spesifikasi 04.

## Kriteria selesai

1. Pertanyaan umum dijawab dan dibacakan dalam waktu wajar pada koneksi biasa.
2. Kunci API tidak muncul di log maupun berkas yang mudah dibaca.
3. Tanpa internet, pengguna mendapat pesan yang jelas, bukan layar kosong.
4. Batas harian benar benar menghentikan permintaan baru.
5. Riwayat dapat dihapus dan benar benar hilang.
