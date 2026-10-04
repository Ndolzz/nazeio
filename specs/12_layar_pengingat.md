# Spesifikasi 12: Layar Pengingat

## Tujuan
Pengguna bisa melihat pengingat yang masih aktif dan menghapusnya langsung dari layar aplikasi, tanpa harus menunggu pengingat berbunyi.

## Perilaku
1. Tab Pengingat di navigasi bawah menampilkan daftar pengingat yang belum lewat, urut dari yang paling dekat waktunya.
2. Setiap baris menampilkan tanggal dan jam pengingat akan berbunyi.
3. Tombol hapus pada satu baris membatalkan alarmnya dan menghapus barisnya dari database.
4. Layar dimuat ulang setelah setiap penghapusan sehingga daftar selalu mutakhir.
5. Daftar kosong menampilkan keterangan bahwa belum ada pengingat.

## Batasan
1. Teks khusus pengingat belum didukung sehingga baris hanya menampilkan waktu.
2. Pengingat tidak bisa ditambah atau diubah dari layar, hanya dipasang lewat suara.

## Di luar cakupan
1. Pengingat berulang.
2. Pengingat berbasis lokasi.

## Kriteria selesai
1. Pengingat yang dipasang lewat suara muncul di layar Pengingat.
2. Pengingat yang dihapus dari layar tidak berbunyi lagi pada waktunya.
3. Pengingat yang sudah lewat tidak pernah tampil di daftar.
