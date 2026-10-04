# Spesifikasi 07: Pengingat Suara

## Tujuan
Pengguna memasang pengingat dengan suara, misalnya "ingatkan aku lima menit lagi", dan Nazeio menampilkan notifikasi pada waktunya.

## Perilaku
1. Ucapan yang memuat kata ingatkan dikenali sebagai permintaan pengingat.
2. Waktu dibaca dari ucapan: X menit lagi, X jam lagi, pukul H, atau jam H. Angka boleh kata atau bilangan.
3. Bila waktu tidak dikenali, Nazeio bertanya kapan pengingat dipasang dan memberi contoh.
4. Saat waktunya tiba, Nazeio menampilkan notifikasi berprioritas tinggi.
5. Waktu yang sudah lewat digeser ke hari berikutnya.
6. Pengingat dicatat di database Room dan dipasang ulang otomatis setelah HP dinyalakan kembali.
7. Baris pengingat dihapus dari database setelah notifikasinya tampil.
8. Pengingat aktif bisa dilihat dan dihapus dari layar Pengingat sesuai spesifikasi 12. Menghapus dari layar juga membatalkan alarmnya.
9. Alarm dipasang presisi lewat setExactAndAllowWhileIdle. Bila izin alarm presisi belum diberikan di Android 12 ke atas, alarm biasa dipakai sebagai cadangan.

## Batasan
1. Bila izin alarm presisi tidak diberikan di Android 12 ke atas, waktu bisa meleset beberapa menit di mode hemat baterai karena alarm biasa dipakai sebagai cadangan.
2. Pengingat yang jatuh saat HP mati dibiarkan lewat dan dihapus saat pemasangan berikutnya.
3. Teks pengingat khusus belum didukung, notifikasi memakai pesan baku.
4. Notifikasi hanya tampil bila izin notifikasi sudah diberikan.

## Di luar cakupan
1. Pengingat berulang.
2. Pengingat berbasis lokasi.

## Kriteria selesai
1. "Ingatkan aku lima menit lagi" menampilkan notifikasi paling lama enam menit kemudian.
2. Waktu yang tidak dikenali meminta klarifikasi tanpa memasang pengingat.
3. Tes unit untuk pembacaan waktu lulus di CI.
4. Pengingat yang dipasang sebelum HP dimatikan tetap berbunyi setelah HP dinyalakan kembali.
5. Pengingat yang dihapus dari layar tidak berbunyi lagi pada waktunya.
