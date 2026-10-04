# Spesifikasi 13: Kontrol Bluetooth

## Tujuan
Pengguna mengendalikan Bluetooth dengan suara: menyalakan, mematikan, dan menanyakan statusnya.

## Perilaku
1. Ucapan yang memuat kata bluetooth dikenali sebagai perintah Bluetooth.
2. Ucapan yang memuat nyalakan atau hidupkan menyalakan Bluetooth.
3. Ucapan yang memuat matikan mematikan Bluetooth.
4. Ucapan bluetooth lainnya dijawab dengan status: menyala atau mati.
5. Di Android 12 ke atas, kontrol butuh izin Bluetooth yang diminta saat aplikasi dibuka. Bila belum diberikan, Nazeio memberi tahu cara memberikannya.
6. Di Android 11, kontrol berjalan tanpa izin tambahan.

## Batasan
1. Perangkat tanpa Bluetooth dijawab dengan pesan bahwa Bluetooth tidak tersedia.
2. Status perangkat tersambung seperti headphone belum dibaca.
3. Nazeio tidak bisa menyalakan Bluetooth bila sistem menolak, misalnya saat mode pesawat.

## Di luar cakupan
1. Pencarian dan penyambungan perangkat Bluetooth baru.
2. Kontrol Wi-Fi.

## Kriteria selesai
1. Nyalakan bluetooth menjadikan Bluetooth aktif pada perangkat yang mendukung.
2. Matikan bluetooth menjadikan Bluetooth nonaktif.
3. Tanya status bluetooth dijawab dengan status yang benar.
