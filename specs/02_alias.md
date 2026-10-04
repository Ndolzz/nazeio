# Spesifikasi 02: Alias dan Aksi Pengaturan

## Tujuan

Satu aksi dapat dipanggil dengan banyak sebutan, dan daftar sebutan dapat bertambah tanpa mengubah kode.

## Perilaku

1. Alias disimpan sebagai data di Room, dengan berkas awal yang dibawa aplikasi.
2. Setiap aksi memiliki satu nama baku dan sebanyak mungkin alias.
3. Teks ucapan dinormalisasi lebih dulu, lalu dicocokkan dengan alias, dengan toleransi salah ucap.
4. Jika satu kata cocok dengan dua aksi, Nazeio bertanya balik, bukan menebak.
5. Pengguna dapat menambah alias lewat suara, misalnya "kalau aku bilang gas, artinya buka Spotify".
6. Pengguna dapat melihat, mengubah, dan menghapus alias di layar Alias.
7. Nama tampilan semua aplikasi terpasang menjadi alias otomatis.

## Bentuk Data

```json
{
  "aksi": "panel_bluetooth",
  "alias": ["bluetooth", "bt", "blutut"],
  "pola": ["buka {alias}", "nyalakan {alias}", "matikan {alias}"]
}
```

## Alias Awal

1. Bluetooth: bluetooth, bt, blutut.
2. WiFi: wifi, internet nirkabel.
3. Mode pesawat: pesawat, mode pesawat.
4. Hotspot: hotspot, tethering.
5. Lokasi: lokasi, gps.
6. Screenshot: screenshot, ss, tangkap layar.
7. WhatsApp: whatsapp, wa.

## Aksi Pengaturan

1. Tahap awal membuka halaman pengaturan yang sesuai, pengguna mengetuk sendiri tombolnya.
2. Tahap lanjut memakai AccessibilityService untuk mengetuk tombol dan mengambil screenshot, setelah pengguna memberi izin aksesibilitas.

## Batasan

1. Android 11 tidak mengizinkan aplikasi biasa menyalakan Bluetooth atau mode pesawat secara langsung.
2. Screenshot lewat AccessibilityService memerlukan izin aksesibilitas yang diaktifkan manual.
3. Nama komponen pengaturan dapat berbeda antar pabrikan, sehingga ada halaman cadangan.

## Di luar cakupan

1. Alias yang dipelajari otomatis dari kebiasaan pengguna.
2. Sinkronisasi alias antar perangkat.

## Kriteria selesai

1. Menambah alias baru hanya mengubah data, tanpa mengubah kode.
2. "bt", "bluetooth", dan "blutut" membuka halaman yang sama.
3. Alias buatan pengguna bertahan setelah aplikasi ditutup dan dibuka lagi.
4. Kata ambigu menghasilkan pertanyaan balik.
5. Pencocok alias lolos tes otomatis untuk minimal 30 contoh.
