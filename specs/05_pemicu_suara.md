# Spesifikasi 05: Pemicu Suara dan Percakapan Berkelanjutan

## Tujuan

Pengguna cukup mengucapkan "Nazeio" kapan saja. Nazeio merespons, lalu terus merespons setiap ucapan berikutnya sampai percakapan selesai. Fitur ini dapat dinyalakan dan dimatikan dari aplikasi dan dari widget.

## Perilaku

1. Tombol geser Mode siaga ada di layar Beranda, di widget besar, dan di widget panjang.
2. Saat siaga menyala, layanan latar belakang mendengarkan hanya kata pemicu "Nazeio". Pemrosesan dilakukan di perangkat, tidak ada audio dikirim keluar.
3. Saat kata pemicu terdengar, terdengar bunyi singkat, status berubah menjadi Aktif berwarna ungu, dan Nazeio menjawab "Ya?".
4. Dalam mode percakapan, setiap ucapan diproses tanpa perlu mengulang kata pemicu.
5. Saat Nazeio berbicara, pendengaran dijeda sebentar agar suara Nazeio tidak terdengar sebagai perintah.
6. Percakapan berakhir bila pengguna diam 8 detik, berkata "selesai" atau "terima kasih Nazeio", atau menekan tombol berhenti.
7. Setelah berakhir, Nazeio kembali ke status Siaga.
8. Saat tombol dimatikan, layanan berhenti dan mikrofon dilepas. Status menjadi Mati.
9. Selama siaga, notifikasi tetap tampil dengan tombol Matikan.

## Status

1. Mati: abu abu, mikrofon tidak dipakai.
2. Siaga: biru, hanya menunggu kata pemicu.
3. Aktif: ungu berdenyut, percakapan berlangsung.

## Batasan

1. Izin yang dibutuhkan: RECORD_AUDIO dan layanan latar depan dengan notifikasi permanen.
2. Siaga terus menerus memakai baterai. Pemakaian harus diukur dan ditampilkan di pengaturan.
3. Kandidat mesin kata pemicu: sherpa onnx keyword spotting, atau Porcupine dengan kata kustom. Keduanya belum diuji pada ARMv7, jadi keputusan diambil setelah uji di HP target.
4. Kata "Nazeio" bukan kata umum, sehingga mesin perlu dilatih atau dikonfigurasi khusus.
5. Pabrikan HP tertentu menghentikan layanan latar belakang. Pengguna mungkin perlu mematikan penghematan baterai untuk Nazeio.

## Di luar cakupan

1. Lebih dari satu kata pemicu.
2. Pengenalan suara pembicara tertentu.
3. Memulai siaga otomatis saat HP menyala, kecuali diaktifkan di pengaturan nanti.

## Kriteria selesai

1. Kata pemicu terdeteksi dalam 1 detik pada jarak 1 meter di ruangan tenang.
2. Aktivasi yang salah kurang dari 2 kali per jam saat ada televisi atau percakapan di sekitar.
3. Tombol Mati benar benar melepas mikrofon.
4. Percakapan berakhir otomatis setelah 8 detik tanpa suara.
5. Suara Nazeio sendiri tidak memicu perintah.
6. Tidak ada audio yang keluar dari perangkat selama siaga.
7. Pemakaian baterai saat siaga tercatat selama uji 8 jam.
