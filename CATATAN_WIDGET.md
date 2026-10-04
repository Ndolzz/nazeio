# Catatan Versi 0.5.0: Widget dan Perbaikan Layanan

Dibuat tanpa kompilasi. Kode ditulis dan diperiksa secara statis (XML valid, referensi resource lengkap, kurung seimbang, logika pencocok diuji ulang di Python). Build pertama di GitHub Actions kemungkinan masih menampilkan beberapa galat kecil. Kirim lognya dan kita perbaiki.

## Fitur baru

1. Tiga widget: 2 x 2, 4 x 1, dan 4 x 2 (spesifikasi 06).
2. Tiga status: Mati, Siaga, Aktif, dengan tombol geser di setiap widget.
3. Neon di tepi widget: Siaga berdenyut pelan, Aktif berputar. Mode Hemat baterai menghentikan putaran.
4. Animasi mikrofon sesuai pilihan di Pengaturan: Gelombang, Kipas, atau Badai.
5. Widget besar menampilkan teks ucapan saat percakapan.
6. Status tunggal bersama (StatusBersama) untuk layanan, Beranda, Pengaturan, dan widget.
7. Kata pemicu dengan pencocokan mirip (PemicuKata) dan tesnya.

## Perbaikan dari audit

1. Tombol Matikan di notifikasi kini berfungsi.
2. Pewaktu 8 detik tidak lagi menumpuk dan dibersihkan saat layanan berhenti.
3. Pendengar dijeda saat Nazeio berbicara, sehingga suaranya tidak terdengar sebagai perintah.
4. Percakapan terus berlanjut sampai diam 8 detik, ucapan "selesai", atau tombol berhenti.
5. Tombol siaga di Beranda dan Pengaturan sinkron dengan layanan.
6. Layanan mengisi alias awal sendiri, tidak bergantung pada aplikasi dibuka dulu.
7. Percobaan ulang pendengar memakai jeda bertahap dan berhenti bila izin ditolak.
8. Pendengar meminta mode offline lebih dulu (tidak dijamin, tergantung paket suara di HP).
9. allowBackup dimatikan.

## Belum dikerjakan

1. Normalisasi yang membuang kata seperti "bukan" dan "buat", serta buangPembuka yang merusak "siapa itu".
2. Kata kerja "nyalakan" dan "matikan" pada alias pengaturan.
3. Kunci Gemini masih di URL.
4. Konfirmasi saat kecocokan aplikasi di bawah 0,9.
5. CI belum menjalankan tes unit, dan Gradle wrapper belum dikomit.
6. AccessibilityService untuk menyalakan Bluetooth dan screenshot.

## Yang perlu diuji di HP

1. Bentuk sudut neon dan tampilan tiap ukuran widget di launcher yang dipakai.
2. Kelancaran animasi dan pemakaian baterai saat Aktif.
3. Apakah izin mikrofon latar belakang tetap berlaku setelah layar mati.
4. Apakah kata "Nazeio" dikenali dengan baik oleh pengenal suara di HP.
