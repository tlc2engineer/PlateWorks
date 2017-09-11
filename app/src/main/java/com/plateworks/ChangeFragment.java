package com.plateworks;

import java.util.List;


/**
 * Интерфейс для связи фрагментов с Activity
 */
public interface ChangeFragment {
	/**
	 * Запрос карты по заданной позиции
	 * @param id 1- первая позиция 2 - вторая
	 */
	void setMap(int id);

	/**
	 *  Установка места на заданную позицию
	 * @param id 1- первая позиция 2 - вторая
	 * @param column номер колонки
	 * @param row номер ряда
	 */
	void setPlace(int id,int column,int row);

	/**
	 * Получеие стартового ряда для данной позиции
	 * @param id 1- первая позиция 2 - вторая
	 * @return
	 */
	int getStartRow(int id);

	/**
	 * Получение стартовой колонки для заданной позиции
	 * @param id 1- первая позиция 2 - вторая
	 * @return
	 */
	int getStartColumn(int id);

	/**
	 * Функция поиска
	 * @param column колонка
	 * @param row ряд
	 */
	void setSearch(int column,int row);

	/**
	 * Выдача результатов поиска
	 * @param lists список листов
	 */
	void setSearchList(List<PlaceList> lists);

}
/**
 * Интерфейс для связи фрагментов с Activity. Авторизация.
 */
interface LogInOut{
	/**
	 * Авторизация
	 */
	void logOn();

	/**
	 * Выход пользователя.
	 */
	void logOut();
}