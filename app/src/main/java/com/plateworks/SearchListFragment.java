package com.plateworks;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import com.example.plateworks.R;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Фрагмент отображения результатов поиска.
 */
public class SearchListFragment extends Fragment {
	// Результаты поиска
	private List<PlaceList> lists;
	// Места
	private List<PlaceItem> places=new ArrayList<PlaceItem>();
	// Листы
	private ListView list;
	// Кнопки переключения отображения мест листов
	private Button btn_place,btn_all;

	/**
	 * Класс отображения найденного места
	 */
	class PlaceItem{
		final int column;
		final int row;
		public PlaceItem(int column,int row){
			this.row=row;
			this.column=column;
		}
		@Override
		public String toString() {
			return "Место "+column+"-"+row;
		}

		@Override
		public int hashCode() {
			return column*10+row;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof PlaceItem){
				PlaceItem pi=(PlaceItem)obj;
				return pi.column==column && pi.row==row;
			}
			return false;
		}
	}

	/**
	 * Создание view
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Cписок результатов поиска
		lists=((MActivity)this.getActivity()).getSlr();
		// портретная ориентация
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// Получение view
		View v = inflater.inflate(R.layout.search_list, container, false);
		// ListView
		 list=(ListView) v.findViewById(R.id.search_res_list);
		// Сортировка
		 if(lists!=null){
			 Collections.sort(lists, new Comparator<PlaceList>(){

				@Override
				public int compare(PlaceList lhs, PlaceList rhs) {
					if(lhs.column==rhs.column)
						return 0;
					return ((lhs.column)>(rhs.column)) ? 1:-1;
				}
				 
			 });
			 // Установка адаптера
		 list.setAdapter(new ArrayAdapter<PlaceList>(this.getActivity(),android.R.layout.simple_list_item_1,lists));
		 }
		 // Инициализация кнопок
		 btn_place= (Button) v.findViewById(R.id.search_list_place_btn);
         btn_all= (Button) v.findViewById(R.id.button_search_all);
		 btn_place.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				places.clear();
				HashSet<PlaceItem> hs=new HashSet<PlaceItem>();
				for(PlaceList pl: lists){
                    if(pl.column<19 || pl.row<1) continue;
					hs.add(new PlaceItem(pl.column,pl.row));
				}
				places.addAll(hs);
                Collections.sort(places, new Comparator<PlaceItem>() {

                    @Override
                    public int compare(PlaceItem placeItem, PlaceItem t1) {
                        if(placeItem.column>t1.column) return 1;
                            else
                        return -1;

                    }
                });
                list.setAdapter(new ArrayAdapter<PlaceItem>(SearchListFragment.this.getActivity(),android.R.layout.simple_list_item_1, places.toArray(new PlaceItem[1])));
                list.invalidate();
				
			}
			 
		 });
        btn_all.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
				if(places ==null )return;
                list.setAdapter(new ArrayAdapter<PlaceList>(SearchListFragment.this.getActivity(),android.R.layout.simple_list_item_1,lists));
                list.invalidate();
            }
        });
		// Обработка нажатия на элемент в списке
		 list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adView, View arg1, int arg2,
					long arg3) {
				if(list.getAdapter()!=null){
					if(adView.getItemAtPosition(arg2) instanceof PlaceList){
						PlaceList place=	(PlaceList) adView.getItemAtPosition(arg2);
						 ChangeFragment cf=(ChangeFragment) getActivity();
						 cf.setSearch(place.column, place.row);
				}
					if(adView.getItemAtPosition(arg2) instanceof PlaceItem){
						PlaceItem place=	(PlaceItem) adView.getItemAtPosition(arg2);
						ChangeFragment cf=(ChangeFragment) getActivity();
						 cf.setSearch(place.column, place.row);
					}
				}
			} 
		 });
		return v;
	}
	@Override
	public void onPause() {
		super.onPause();
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}
}
