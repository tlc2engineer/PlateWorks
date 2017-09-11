package tech;

import java.util.List;

/**
 * Интерфейс места
 */
public interface PlaceItem {
    /**
     * Получение колонны
     * @return
     */
    int getColumn();

    /**
     * Получение ряда
     * @return
     */
    int getRow();

    /**
     * Получение листов
     * @return
     */
    List<Plate> getPlates();

}
