package tech;

import android.util.Log;

import java.util.List;

/**
 * Родительский класс для всех хранилищ листов.
 */
public abstract class  PlateContainer {
    // Список листов
    abstract public List<Plate> getPlates();
    // Выбор для передачи
    private boolean selected=false;
    // Развернут
    public boolean exp=false;
    // Память развернуто
    public boolean exp_mem=false;
    // Флаг обновления
    public boolean upd=false;
    // Выбран ли
    public boolean isSelected() {
	return selected;
}

    /**
     * Установка флага выбора
     * @param selected
     */
    public void setSelected(boolean selected) {
	this.selected = selected;
}
  
}
