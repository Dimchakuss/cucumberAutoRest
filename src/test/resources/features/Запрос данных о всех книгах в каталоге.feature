#language: ru

Функционал: Тестирование API demoqa.com v1

  Сценарий: Запрос данных о всех книгах в каталоге

    * Запрашиваем данные всех книг в каталоге

    * Проверяем, что в ответе строковое поле books[0].isbn равно 9781449325862
    * Проверяем, что в ответе строковое поле books[0].title равно Git Pocket Guide
    * Проверяем, что в ответе числовое поле books[0].pages равно 234

    * Проверяем, что в ответе строковое поле books[1].isbn равно 9781449331818
    * Проверяем, что в ответе строковое поле books[1].title равно Learning JavaScript Design Patterns
    * Проверяем, что в ответе числовое поле books[1].pages равно 254

    * Проверяем, что в ответе строковое поле books[2].isbn равно 9781449337711
    * Проверяем, что в ответе строковое поле books[2].title равно Designing Evolvable Web APIs with ASP.NET
    * Проверяем, что в ответе числовое поле books[2].pages равно 238