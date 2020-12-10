Listener task
--------------------------------------

Тестовое задание на написание логики листенера.



Road map
--------------------------------------
Предметное объяснение:
   - Основным классом в данном домене является EppRegistryElement, в реальной жизни это может быть любой предмет, например математика.
    У каждого registryElement могут быть ссылающиеся на него части EppRegistryElementPart. Данная класс подразумевает под собой представление
    предмета в одном из семестров. Например 2 части, значит данный предмет есть в двух семестрах в учебном году.
  - Каждый EppRegistryElement имеет ссылку на EppState показывающий его состояние (Формируется, на согласовании, согласовано и т.д.)
  - От EppRegistryElement наследуется EppRegistryProfModule, его особенность в том, что он может быть связан с несколькими registryElement
  одновременно и быть для них "главным", связывание происходит с помощью объекта типа mainBond
  - Части eppRegistryElement должны быть распределены внутри частей profModule строго в последовательном порядке.
  В каждой части проф модуля может быть только одна часть отдельного взятого registryElement. Связывание частей происходит с помощью
  объекта part2partBond.
  - Схема сущностей лежит в resources. Она поможет разобраться.
  
Суть задачи:
  - Нужно написать логику метода Listener'a - валидатора onEvent();
  - Данный метод принимает 3 коллекции: список профмодулей, для которых будет проводиться валидация, список mainBonds, список part2partBonds.

Требования валидации:
  - Нужно провести валидацию только тех профмодулей, которые имеют состояние "на Согласовании" или "Согласовано"
  - У вложенных элементов реестра состояние должно быть не ниже того, в котором находится модуль. т.е. для "на согласовании"
  состояние вложенок должно быть "на согласовании" или "согласовано". Для состояние модуля "согласовано" для вложенок должно быть только "согласовано"
  - Если у проф модуля есть связь с элементом реестра, то должны быть распределены все части этого элемента реестра внутри проф модуля.
  - В каждой части проф модуля, только 1 часть отдельно взятого элемента реестра
  - Части вложенных элементов должны быть распределены по порядку (поле number)
  - В случае ошибок кидать экспшен типа IllegalStateException, с сообщениями:
  - Если неподходящее состояние вложенных элементов: "Нельзя согласовать/отправить на согласование профмодуль ${module.title}, т.к. вложенные мероприятия имеют неподходящее состояние"
  - Если распределены не все части: Нельзя согласовать/отправить на согласование профмодуль ${module.title}, т.к. распределены не все части вложенных элементов.
  - Если распределено всё, но в неверном порядке: Нельзя согласовать/отправить на согласование профмодуль ${module.title}, т.к. части вложенного мероприятия ${element.title} распределены в неправильном порядке.
  
Тестирование:
  - Я добавил пару библиотек для тестирования, основной является конечно JUnit, в пакете test вы найдете пример тестирования (создание тестовых данных и прочее)
  - Нужно будет написать несколько тестовых наборов данных, в которых вы ожидаете разный результат, и тесты к ним.
  