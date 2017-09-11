package com.plateworks;

import android.support.v7.util.DiffUtil;
import android.util.Log;

import java.util.List;

import tech.Heat;
import tech.PlateContainer;
import tech.Pred;

/**
 * Вспомогательный класс для поиска изменений в RecyclerView
 */

public class ContainerDiffCallback extends DiffUtil.Callback {
    // Старый список объектов
    private List<PlateContainer> mOldList;
    // Новый список объектов
    private List<PlateContainer> mNewList;

    /**
     * Конструктор
     * @param oldList старый список
     * @param newList новый список
     */
    public ContainerDiffCallback(List<PlateContainer> oldList, List<PlateContainer> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    /**
     * Размер старого списка
     * @return
     */
    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    /**
     * Размер нового списка
     * @return
     */
    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    /**
     * Определение идентичности объектв на позициях в старом и новом списке
     * @param oldItemPosition старая позиция
     * @param newItemPosition новая позиция
     * @return идентичность
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        PlateContainer old=mOldList.get(oldItemPosition);
        PlateContainer nw=mNewList.get(newItemPosition);
        return old.equals(nw);
    }

    /**
     * Определение идентичности содержания при идентичности объектов
     * @param oldItemPosition позиция в старом списке.
     * @param newItemPosition позиция в новом списке
     * @return идентичность содержания
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        PlateContainer old=mOldList.get(oldItemPosition);
        PlateContainer nw=mNewList.get(newItemPosition);
        // Содержание идентично если объекты те же и флаги не отличаются.
        return old.equals(nw) && old.exp==old.exp_mem && !old.upd;

    }
}
