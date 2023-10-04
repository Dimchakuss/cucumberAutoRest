#language: ru

Функционал: Тестирование API demoqa.com v1

  Сценарий: Запрос данных о книге по ID

    * Запрашиваем все ID ISBN книг в каталоге
    * Отправляем подготовленный запрос для получения данных книги с индексом 0
    * Проверяем, что в ответе строковое поле author равно Richard E. Silverman
    * Проверяем, что в ответе строковое поле title равно Git Pocket Guide
    * Проверяем, что в ответе строковое поле publisher равно O'Reilly Media
    * Проверяем, что в ответе числовое поле pages равно 234
    * Проверяем, что в ответе строковое поле description равно This pocket guide is the perfect on-the-job companion to Git, the distributed version control system. It provides a compact, readable introduction to Git for new users, as well as a reference to common commands and procedures for those of you with Git exp
    * Проверяем, что в ответе строковое поле website равно http://chimera.labs.oreilly.com/books/1230000000561/index.html