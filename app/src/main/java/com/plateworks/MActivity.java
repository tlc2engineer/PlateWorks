package com.plateworks;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import com.example.plateworks.R;

import tech.Place;
import tech.Yard;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


/**
 * Основная активность.
 */

public class MActivity extends Activity implements ChangeFragment,LogInOut,
OnSharedPreferenceChangeListener,Simulated {
	// Флаг симуляции
	private boolean sim_on=false;
	// Время запуска
	public  static long startTick=System.currentTimeMillis();
	// Флаг входа пользователя
	private boolean logOn=false;
	// Флаг отсутствия сети
	public final static boolean noNet=false;
	// Поле склада
	private Yard yard;
	// ActionBar
	private ActionBar bar;
	// Фрагмент карты
	private MapFragment m1;
	// начальные номеры колонок и рядов по 2 позициям
	private int col1=19,col2=20,row1=1,row2=1;
	// Последнее врямя действия пользователя с программой
	private long lastTime=0;
	// Тайм аут 10 минут
	private final int timeOut=10;
	// Меню
	private Menu menu;
	// Уставки
	private SharedPreferences settings;
	// Результаты поиска
	private List<PlaceList> slr;
	/**
	 * Получение результатов поиска
 	 */
	public List<PlaceList> getSlr() {
		return slr;
	}

	/**
	 * Обновление времени последнего действия пользователя
	 * @return
	 */
	private boolean updateLastTime(){
	if((Calendar.getInstance().getTimeInMillis()-lastTime)>timeOut*1000*60){
		logOut();
		return false;
	}
	else
	lastTime=Calendar.getInstance().getTimeInMillis();
	return true;
}

	/**
	 * Обработка кнопки назад
	 */
	@Override
public void onBackPressed() {
	updateLastTime();
	super.onBackPressed();
}

	/**
	 * Обработка действия пользователя.
	 */
	@Override
public void onUserInteraction() {
		updateLastTime();
	super.onUserInteraction();
}

	/**
	 * Создание активности
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Стартовое время
		startTick=System.currentTimeMillis();
		// Обработка свойств
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		settings.registerOnSharedPreferenceChangeListener(this);
		Editor edit = settings.edit();
		yard=Yard.getInstance();
		if (!settings.contains("sim_on")){
			sim_on=false;
			edit.putBoolean("sim_on", false);
	 	   	edit.commit();
		}
		else{
			sim_on=settings.getBoolean("sim_on", false);
		}
		// Если симуляция загрузка данных из ранее сохраненного файла.
		if(sim_on) yard.restorePlates(this);
		// Получение сохраненных данных
		if ((savedInstanceState!=null)&&savedInstanceState.containsKey("row1")&&savedInstanceState.containsKey("col1")){
			col1=savedInstanceState.getInt("col1");
			col2=savedInstanceState.getInt("col2");
			row1=savedInstanceState.getInt("row1");
			row2=savedInstanceState.getInt("row2");
			logOn=savedInstanceState.getBoolean("logOn");
			slr=(List<PlaceList>) savedInstanceState.getSerializable("slr");
			lastTime=savedInstanceState.getLong("lastTime");
		}
		// Установка разводки
		setContentView(R.layout.fragment_layout);
		// Если 10 минут прошло пользователю нужно авторизироваться заново
		if(!updateLastTime()){
			return;
		}
		else{
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR); 
		}
		// Получение Action Bar
		bar=this.getActionBar();
		bar.show();
	    // APlaceFragment()
		FragmentManager fm = getFragmentManager();
		if(fm.findFragmentById(R.id.place1Container)==null){
			fm.beginTransaction().add(new APlaceFragment(),"1").commit();
		}
	}

	/**
	 * Сохранение значений
	 * @param outState
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("row1",row1 );
		outState.putInt("col1",col1 );
		outState.putInt("row2",row2 );
		outState.putInt("col2",col2 );
		outState.putBoolean("logOn", logOn);
		outState.putSerializable("slr",(Serializable) slr);
		Calendar cl = Calendar.getInstance();
		outState.putLong("lastTime", cl.getTimeInMillis());
		
	}

	/**
	 * Создание меню пользователя
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu=menu;
		if(logOn)
			getMenuInflater().inflate(R.menu.main, menu);
		else{
			getMenuInflater().inflate(R.menu.nolog, menu);
		}
		return true;
	}

	/**
	 * Обработка меню пользователя
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch(id){
			// Установка фрагмента поиска
		case R.id.searchItem:
			this.getFragmentManager().beginTransaction().replace(R.id.place1Container,new SearchFragment()).addToBackStack("search").commit();
			break;
		// Установка фрагмента карты
		case R.id.mapItem:
			MapFragment.actRow=yard.getCurrentRow();
			MapFragment.actColumn=yard.getCurrentColumn();
			this.getFragmentManager().beginTransaction().replace(R.id.place1Container,new MapFragment()).commit();
			menu.clear();
			getMenuInflater().inflate(R.menu.mapmenu, menu);
			break;
            case R.id.get_all_map:
                yard.getDataToYard(this);
                break;
			// Установка фрагмента свойств
		case R.id.settings:
				this.getFragmentManager().beginTransaction().replace(R.id.place1Container,new SettingsFragment()).addToBackStack("sett").commit();
			break;
		// Выход из аккаунта
		case R.id.out:
			logOut();
			break;
		// Вызов помощи
		case R.id.help:
			this.getFragmentManager().beginTransaction().replace(R.id.place1Container,new HelpFragment()).addToBackStack("help").commit();
			/*
			Builder helpDialog = new AlertDialog.Builder(MActivity.this);
			LayoutInflater ltInflater = getLayoutInflater();
			View view=ltInflater.inflate(R.layout.help_layout, null);
			TextView helpText=(TextView) view.findViewById(R.id.help_text_view);
			AssetManager am = getResources().getAssets();
			StringBuffer sb = null;
	        try {
	       	 InputStreamReader reader = new InputStreamReader(am.open(getResources().getString(R.string.help_file)),"utf-8");
				sb = new StringBuffer();
			        while( true ) {
			            int c = reader.read();
			            if( c < 0 )
			                break;
			                sb.append( (char)c );
			        }
			    
			} catch (IOException e) {
				e.printStackTrace();
			}
	        helpText.setText(Html.fromHtml(new String(sb)));
			helpDialog.setTitle(getResources().getString(R.string.label_help)).setView(view);
			final AlertDialog dialog = helpDialog.create();
			helpDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					dialog.cancel();
					
				}
				
			});	
				dialog.show();
				
			break;
			*/
		}
		
		return true;
	}

	/**
	 * Получение флага симуляции
	 * @return симуляция
	 */
	@Override
	public boolean getSimOn() {
		return sim_on;
	}

	/**
	 * Установка фрагмента карты
	 * @param id 1- первая позиция 2 - вторая
	 */
	@Override
	public void setMap(int id) {
		FragmentManager fm = getFragmentManager();
		if(m1==null){
			m1=new MapFragment();
		}
		m1.setContId(id);
		fm.beginTransaction().replace(R.id.place1Container, m1,"m1").addToBackStack("map").commit();
	}

	/**
	 * Установка фрагмента места
	 * @param id 1- первая позиция 2 - вторая
	 * @param column номер колонки
	 * @param row номер ряда
	 */
	@Override
	public void setPlace(int id,int column,int row) {
		FragmentManager fm = getFragmentManager();
		if(id==1){
			col1=column;
			row1=row;
		}
		else{
			col2=column;
			row2=row;
		}
		this.onBackPressed();
	}

	/**
	 * Получение колонки
	 * @param id 1- первая позиция 2 - вторая
	 * @return
	 */
	@Override
	public int getStartRow(int id) {
		if(id==1) return row1;
		else return row2;
		
	}

	/**
	 * Полчение ряда
	 * @param id 1- первая позиция 2 - вторая
	 * @return
	 */
	@Override
	public int getStartColumn(int id) {
		if(id==1) return col1;
		else return col2;
	}

	/**
	 * Поиск
	 * @param column колонка
	 * @param row ряд
	 */
	@Override
	public void setSearch(int column, int row) {
		col1=column;
		row1=row;
		this.onBackPressed();
		this.onBackPressed();
	}

	/**
	 * Авторизация пользователя
	 */
	@Override
	public void logOn() {
		lastTime=Calendar.getInstance().getTimeInMillis();
		logOn=true;
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction().replace(R.id.place1Container,new APlaceFragment(),"1").commit();
		menu.clear();
		getMenuInflater().inflate(R.menu.main, menu);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		yard=Yard.getInstance();
		bar=this.getActionBar();
		bar.show();
	}

	/**
	 * Выход пользователя из аккаунта
	 */
	@Override
	public void logOut() {
		Log.v("logout","logout");
		logOn=false;
		FragmentManager fm = getFragmentManager();
		if(menu!=null){
			menu.clear();
			getMenuInflater().inflate(R.menu.nolog, menu);
		}
		LoginFragment login=null;
		if (fm.findFragmentById(R.id.place1Container) instanceof LoginFragment)
			 	{login=(LoginFragment)fm.findFragmentById(R.id.place1Container);
			 	}
		else
			{
				login = new LoginFragment();
				fm.beginTransaction().replace(R.id.place1Container,login,"1").commit();
			}
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * Отображение результатов поиска
	 * @param lists список листов
	 */
	@Override
	public void setSearchList(List<PlaceList> lists) {
		SearchListFragment slf=new SearchListFragment();
		this.slr=lists;
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction().replace(R.id.place1Container,slf,"1").addToBackStack("Search list").commit();
		
	}

	/**
	 * Обработка изменения настроек
	 * @param sharedPreferences
	 * @param key
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		if(key.equals("sim_on")){
			sim_on=sharedPreferences.getBoolean("sim_on", false);
            yard.clearAllPlaces();
			
		}
		if(sim_on){
			yard.restorePlates(this);
		}
	}
}

/**
 * Класс отображения результатов поиска. Отображает информацию о листе, а также о том где он находися.
 */
 class PlaceList implements  Serializable{
	// номер, крат, колонка,ряд.
	int id, krat, column, row;

	/**
	 * Статическая функция. Возвращает объект, на вход принимает текстовую информацию.
	 * @param idslab информация о листе
	 * @param columnRow о месте
	 * @return
	 */
	public static PlaceList getIdKrat(String idslab,String columnRow){
		int column = -1;
		int row=-1;
		int searchId=-1;
		int searchKrat=-1;
		try {
				String[] idkrat=idslab.split("-");
			 	searchId = Integer.valueOf(idkrat[0]);
			 	searchKrat = Integer.valueOf(idkrat[1]);
				String[] columnRows = columnRow.trim().split("-");
				column = Integer.valueOf(columnRows[0].trim());
			 	row = Integer.valueOf(columnRows[1]);
            if(column<19 || row<1) return null;
		}
		catch(NumberFormatException exc){
			return null;
		}
		return new PlaceList(searchId,searchKrat,column,row);
	}

	/**
	 * Конструктор обрабатывает информацию в численном виде.
	 * @param id номер листа
	 * @param krat крат
	 * @param column колонка
	 * @param row ряд
	 */
	public PlaceList(int id, int krat, int column, int row) {
		super();
		this.id = id;
		this.krat = krat;
		this.column = column;
		this.row = row;
	}

	/**
	 * Конструктор приниает информацию в текстовом виде.
	 * @param idslab данные листа
	 * @param columnRow данные места
	 */
	private PlaceList(String idslab,String columnRow){
		String[] idkrat=idslab.split("-");
		Integer searchId = Integer.valueOf(idkrat[0]);
		Integer searchKrat = Integer.valueOf(idkrat[1]);
		String[] columnRows=columnRow.trim().split("-");
		int colum=-1;;
		try{
		 column=Integer.valueOf(columnRows[0].trim());
		}
		catch(NumberFormatException ex){
			return ;
		}
		try {
			int row = Integer.valueOf(columnRows[1]);
		}
		catch(NumberFormatException exc){
			return ;
		}
		this.id=searchId;
		this.krat=searchKrat;
	}

	/**
	 * Переопределение функции toString.
	 * @return
	 */
	@Override
	public String toString() {
		return "Место "+column+"-"+row+" Лист "+id+"-"+krat;
	}
	public Place getPlace(int column,int row){
		return Yard.getInstance().getPlace(column, row);
	}

}

/**
 * Интерфейс для связи с Activity. Симуляция.
 */
interface  Simulated{
	boolean getSimOn();
}