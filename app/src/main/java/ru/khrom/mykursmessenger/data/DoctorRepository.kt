package ru.khrom.mykursmessenger.data

object DoctorRepository {
    val doctors = listOf(
        Doctor(
            id = "1",
            name = "Д-р Александр Иванов",
            specialty = "Дерматолог",
            lastMessage = "Здравствуйте! Как успехи?",
            time = "10:30",
            isOnline = true,
            avatarUrl = "https://unsplash.com",
            gender = "Male",
            isFavorite = true
        ),
        Doctor(
            id = "2",
            name = "Д-р Мария Петрова",
            specialty = "Аллерголог",
            lastMessage = "Пришлите результаты анализов.",
            time = "Вчера",
            isOnline = false,
            avatarUrl = "https://unsplash.com",
            gender = "Female",
            isFavorite = false
        ),
        Doctor(
            id = "3",
            name = "Д-р Сергей Смирнов",
            specialty = "Терапевт",
            lastMessage = "Жду вас на повторный прием.",
            time = "2 дня назад",
            isOnline = true,
            avatarUrl = "https://unsplash.com",
            gender = "Male",
            isFavorite = true
        ),
        Doctor(
            id = "4",
            name = "Д-р Елена Козлова",
            specialty = "Педиатр",
            lastMessage = "Рецепт я обновила.",
            time = "05.10",
            isOnline = false,
            avatarUrl = "https://unsplash.com",
            gender = "Female",
            isFavorite = false
        )
    )
}
