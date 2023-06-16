package com.agronify.android.model.local.dummy

object DummyTips {
    fun generateDummyTips(): List<Tips> {
        return listOf(
            Tips(
                "clear",
                listOf(
                    Tip(
                        "Rekomendasi",
                        "Gulma dapat dengan mudah dihilangkan di langit cerah."
                    ),
                    Tip(
                        "Rekomendasi",
                        "Pangkas tanaman selama cuaca cerah untuk meningkatkan sirkulasi udara."
                    ),
                    Tip(
                        "Rekomendasi",
                        "Terapkan pupuk atau kompos ke tanah selama cuaca cerah."
                    ),
                    Tip(
                        "Rekomendasi",
                        "Panen buah dan sayuran. Langit cerah bagus untuk memanen buah."
                    ),
                    Tip(
                        "Hindari",
                        "Hindari penggunaan pestisida selama cuaca langit cerah."
                    ),
                    Tip(
                        "Hindari",
                        "Hindari mentransplantasikan bibit yang rapuh selama cuaca cerah."
                    ),
                    Tip(
                        "Hindari",
                        "Tidak disarankan untuk menambah mulsa selama cuaca langit cerah."
                    )
                )
            ),
            Tips(
                "partly_cloudy",
                listOf(
                    Tip(
                        "Rekomendasi",
                        "Siram tanaman Anda karena penyerapan optimal saat cerah berawan."
                    ),
                    Tip(
                        "Rekomendasi",
                        "Tanam atau pindah tanaman baru saat cuaca berawan."
                    )
                )
            ),
            Tips(
                "overcast",
                listOf(
                    Tip(
                        "Hindari",
                        "Hindari memanen buah dan sayur dalam kondisi mendung."
                    ),
                    Tip(
                        "Hindari",
                        "Hindari mengaplikasikan pestisida atau herbisida."
                    ),
                    Tip(
                        "Hindari",
                        "Cuaca mendung tidak kondusif untuk mengeringkan tanaman."
                    )
                )
            ),
            Tips(
                "drizzle",
                listOf(
                    Tip(
                        "Rekomendasi",
                        "Lakukan penyemprotan foliar saat gerimis."
                    ),
                    Tip(
                        "Rekomendasi",
                        "Panen sayuran hijau dan herbal saat gerimis."
                    ),
                    Tip(
                        "Rekomendasi",
                        "Lakukan pemindahan bibit saat gerimis."
                    ),
                    Tip(
                        "Rekomendasi",
                        "Gerimis membantu melembabkan tanah Anda."
                    ),
                    Tip(
                        "Hindari",
                        "Hindari pengolahan atau penanaman yang berlebihan selama gerimis."
                    )
                )
            ),
            Tips(
                "rain",
                listOf(
                    Tip(
                        "Rekomendasi",
                        "Pancuran hujan dapat membantu mendistribusikan pupuk."
                    ),
                    Tip(
                        "Rekomendasi",
                        "Menerapkan mulsa saat hujan dapat membantu mengurangi erosi tanah."
                    ),
                    Tip(
                        "Hindari",
                        "Sebaiknya hindari penggunaan pestisida atau pupuk selama hujan."
                    ),
                    Tip(
                        "Hindari",
                        "Hindari menyiram tanaman selama periode ini."
                    ),
                    Tip(
                        "Hindari",
                        "Hindari penyemprotan pestisida atau herbisida saat hujan."
                    )
                )
            ),
            Tips(
                "thunderstorm",
                listOf(
                    Tip(
                        "Rekomendasi",
                        "Selalu cek perkiraan cuaca atau berita terkait cuaca di lokasi anda."
                    ),
                    Tip(
                        "Rekomendasi",
                        "Hindari kegiatan pertanian apa pun sampai badai berlalu."
                    )
                )
            ),
            Tips(
                "cloudy",
                listOf(
                    Tip(
                        "Rekomendasi",
                        "Cuaca berawan baik bagi tanaman di rumah kaca."
                    ),
                    Tip(
                        "Hindari",
                        "Hindari irigasi berlebihan saat cuaca berawan."
                    ),
                    Tip(
                        "Hindari",
                        "Tunda aktivitas pemangkasan selama cuaca berawan."
                    )
                )
            )
        )
    }
}