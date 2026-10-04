# Spesifikasi 07: Pengingat Suara

## Tujuan
Pengguna memasang pengingat dengan suara, misalnya "ingatkan aku lima menit lagi", dan Nazeio menampilkan notifikasi pada waktunya.

## Perilaku
1. Ucapan yang memuat kata ingatkan dikenali sebagai permintaan pengingat.
2. Waktu dibaca dari ucapan: X menit lagi, X jam lagi, pukul H, atau jam H. Angka boleh kata atau bilangan.
3. Bila waktu tidak dikenali, Nazeio bertanya kapan pengingat dipasang dan memberi contoh.
4. Saat waktunya tiba, Nazeio menampilkan notifikasi berprioritas tinggi.
5. Waktu yang sudah lewat digeser ke hari berikutnya.

## Batasan
1. Pengingat hilang bila HP dimatikan ulang. Penyimpanan permanen menyusul.
2. Memakai AlarmManager tanpa izin alarm presisi sehingga bisa melesat beberapa menit di mode hemat baterai.
3. Teks pengingat khusus belum didukung, notifikasi memakai pesan baku.
4. Notifikasi hanya tampil bila izin notifikasi sudah diberikan.

## Di luar cakupan
1. Pengingat berulang.
2. Pengingat berbasis lokasi.

## Kriteria selesai
1. "Ingatkan aku lima menit lagi" menampilkan notifikasi paling lama enam menit kemudian.
2. Waktu yang tidak dikenali meminta klarifikasi tanpa memasang pengingat.
3. Tes unit untuk pembacaan waktu lulus di CI.
