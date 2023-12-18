# CRUD для ведения расходов 

## Запуск кода 

Для того чтобы запустить сам проект -  sbt, compile и run. Исполняемый файл - Application.scala

Для того чтобы запустить тесты - sbt, compile и test. Исполняемый файл - ApplicationSpec.scala

src/main/migrations должен быть помечен как ресурсы, там содержится конфиг для работы с сетью и база данных, src/main/scala это основной код и src/test/scala это тесты

## Функции

1.post /expenses/add - добавляет элемент в базу данных по свободному id. Так как айди с каждым добавлением увеличивается а какие-то элементы в базе с бОльшим значением могли быть(начальные элементы), то если при увеличении айди элемент занят он выведет ошибку. После успешного добавления возвращает айди, потом его узнать можно будет только подбором.

2.get /expenses/{id} - ищет элемент по айди.

3.put /expenses/{id} - изменяет элемент по айди. Не создает новые в случае не нахождения.

4.delete /expenses{id} - удаляет элемент по айди. После удаления индексы не меняются.

5.get /hello - из json текста для проверки достает поле имени и выводит его на экран(использовался в качестве проверки парсинга).

6.get /expenses/list/all - возвращает все записи содержащиеся в базе данных.

7-10. get /expenses/filter - возвращает все записи содержащие одинаковую локацию/счет/категорию/имя

11-14. get /expenses/total - возвращает суммарные траты всех записей содержащие одинаковую локацию/счет/категорию/имя