# Spesifikasi 08: Telepon Suara

## Tujuan
Pengguna menelepon dengan suara, misalnya "telepon budi" atau "telepon 08123456789", dan Nazeio membuka dialer dengan nomor sudah terisi.

## Perilaku
1. Ucapan yang dimulai kata telepon atau panggil dikenali sebagai permintaan menelepon.
2. Bila tujuan berupa bilangan, dialer langsung dibuka dengan nomor itu.
3. Bila tujuan berupa nama, Nazeio mencari kontak yang namanya memuat teks itu lalu memilih yang paling mirip dengan pencocokan jaro winkler.
4. Satu kontak paling mirip berarti dialer dibuka dengan nomor kontak tersebut.
5. Lebih dari satu kontak dengan skor berdekatan berarti Nazeio menyebut hingga tiga nama teratas dan tidak menelepon.
6. Bila izin kontak belum diberikan, Nazeio menyuruh pengguna membuka aplikasi dan memberi izin.
7. Nazeio tidak pernah menelepon langsung. Pengguna selalu menekan tombol panggilan sendiri.

## Batasan
1. Membutuhkan izin kontak yang diminta saat aplikasi pertama kali dibuka.
2. Penyaring pertama memakai kandungan nama sehingga ucapan yang sangat berbeda dari nama kontak tidak ditemukan.
3. Nomor dengan kode negara ditulis apa adanya, tanpa pemformatan.

## Di luar cakupan
1. Menelepon langsung tanpa tombol panggilan.
2. Mengirim pesan singkat.
3. Riwayat panggilan.

## Kriteria selesai
1. "Telepon" diikuti bilangan membuka dialer dengan nomor terisi.
2. "Telepon budi" membuka dialer bila ada tepat satu kontak yang paling mirip.
3. Kontak dengan skor berdekatan memunculkan klarifikasi, bukan dialer.
4. Tanpa izin kontak, Nazeio menjelaskan cara memberi izin.
5. Tes unit untuk pemilihan kontak lulus di CI.
