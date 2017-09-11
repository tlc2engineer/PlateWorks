package com.plateworks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.plateworks.R;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tech.PlaceItem;
import tech.Plate;
import tech.Predicate;
import tech.Yard;

/**
 * Фрагмент поиска листов.
 */
public class SearchFragment extends Fragment {
    // клиент для связи по сети
	private final OkHttpClient client = new OkHttpClient();
    // Поля для ввода значений
    EditText orderT,predT,heatT,gradeT,thickT,idT;
    // начальная и конечная дата поиска отображение
    TextView startDate,endDate;
    // Начальная и конечная даты поиска
    Date start,end;
    // Кнопки вызова диалогов ввода даты
    Button from,until,search;
    // Симуляция
	private boolean sim_on;

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
        // Флаг симуляции
        sim_on = ((Simulated) this.getActivity()).getSimOn();
        // Получение view
		View v = inflater.inflate(R.layout.search_modern, container, false);
        // Инициализация элементов
		from=(Button) v.findViewById(R.id.from_date);
		until=(Button) v.findViewById(R.id.until_date);
        if(sim_on)
        {
            from.setEnabled(false);
            until.setEnabled(false);
        }
        else{
            from.setEnabled(true);
            until.setEnabled(true);
        }
		search=(Button) v.findViewById(R.id.searchBtn);
		startDate=(TextView) v.findViewById(R.id.bgsdate);
		endDate=(TextView) v.findViewById(R.id.endsdate);
		Calendar cal=Calendar.getInstance();
		startDate.setText(""+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.YEAR));
		endDate.setText(""+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.YEAR));
		orderT=(EditText) v.findViewById(R.id.sorder);
		predT=(EditText) v.findViewById(R.id.spred);
		heatT=(EditText) v.findViewById(R.id.sheat);
		gradeT=(EditText) v.findViewById(R.id.sgrade);
		thickT=(EditText) v.findViewById(R.id.sthick);
		idT=(EditText) v.findViewById(R.id.sid);
		start=new Date();
		end=new Date();
		//обработчик кнопки ввода даты от
		from.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DatePickerFragment newFragment = new DatePickerFragment();
				newFragment.setTextView(startDate);
			    newFragment.show(getFragmentManager(), "datePicker");
				
			}
			
		});
        //обработчик кнопки ввода даты до
		until.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DatePickerFragment newFragment = new DatePickerFragment();
				newFragment.setTextView(endDate);
			    newFragment.show(getFragmentManager(), "datePicker");
				
			}
			
		});
        // Обработчик кнопки поиска
		search.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
                // Если нет симуляции
				if(!sim_on) {
                    // Создание и исполнение запроса
					GetSearchResult res = new GetSearchResult();
					res.execute(1);
				}
				// Если симуляция поиск по загруженным данным.
				else{
					Yard yard=Yard.getInstance();
					int order=-1;
					int id=-1;
                    int pred=-1;
                    int thick=-1;
                    // Получение значений
					final String heatText=heatT.getText().toString();
                    final String gradeText=gradeT.getText().toString();
					try {
                        // Получение значений
						final String orderText = orderT.getText().toString();
						if(orderText.length()>0) order = Integer.parseInt(orderText.toString());
						final String idText = idT.getText().toString();
						if(idText.length()>0) id = Integer.parseInt(idText.toString());
                        final String predText=predT.getText().toString();
                        if(predText.length()>0) pred = Integer.parseInt(predText.toString());
                        final String thickText=thickT.getText().toString();
                        if(thickText.length()>0) thick = Integer.parseInt(thickText.toString());
					}
					catch(NumberFormatException ex){
						Toast tst=Toast.makeText(SearchFragment.this.getActivity(),"Неправильно введeно число",Toast.LENGTH_SHORT);
						tst.show();
					}
					final int orderF=order;
					final int idF=id;
                    final int predF=pred;
                    final int thickF=thick;
                    // Поиск в памяти. Создание функции поиска. Замыкание.
					List<PlaceItem> plates=yard.search(new Predicate<Plate>() {


						public boolean test(Plate plate) {
                            boolean ob=(plate.getOrder()==orderF)||(orderF==-1);
                            boolean idb=(plate.hashCode()/10==idF) || (idF==-1);
                            boolean hb=(heatText.length()==0) || (heatText.contains(plate.getHeat()));
                            boolean gb=(gradeText.length()==0) || gradeText.contains(plate.getGrade());
                            boolean pb=(predF==-1) || (predF==plate.getNum());
                            boolean tb=( thickF==-1 || thickF==(int)plate.getThick());
							boolean res= ((ob  && idb && hb && gb && pb && tb));
                            return res;
						}
					});
                    // Создание списка найденных листов.
					if(plates.size()>0){
						List<PlaceList> pls=new ArrayList<PlaceList>();
						for(PlaceItem place : plates){
							int row = place.getRow();
							int column=place.getColumn();
							for(Plate plate: place.getPlates()){
								int krat=plate.hashCode()%10;
								int ID=(plate.hashCode()-krat)/10;
								pls.add(new PlaceList(ID,krat,column,row));
							}
						}
						// Вызов фрагмента отображения списка
						getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						((ChangeFragment)getActivity()).setSearchList(pls);

					}
					else{
						Toast.makeText(getActivity(), "Ничего не найдено", Toast.LENGTH_SHORT).show();
					}

				}
				
			}
			
		});
		return v;
	}

    /**
     * Класс отображения диалога ввода даты.
     */
	public static  class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {
		TextView view=null;
		public void setTextView(TextView view){
			this.view=view;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(getActivity(), this, year, month, day);
		}

	    public void onDateSet(DatePicker view, int year, int month, int day) {
		    if(view!=null)
		        this.view.setText(""+(++month)+"/"+day+"/"+year);
	    }
	}

    /**
     * Класс запроса результатов поиска по сети
     */
	private class GetSearchResult extends AsyncTask<Integer,Integer,Boolean>{
		List<PlaceList> slist=new ArrayList<PlaceList>();
        // Параметры запроса
		String startDateTxt;
		String endtDateTxt;
		String predTTxt;
		String orderTTxt;
		String heatTTxt;
		String gradeTTxt,thickTTxt,idTTxt;

        /**
         * Инициализация параметров запроса.
         */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			startDateTxt=startDate.getText().toString();
			endtDateTxt=endDate.getText().toString();
			predTTxt=predT.getText().toString();
			orderTTxt=orderT.getText().toString();
			heatTTxt=heatT.getText().toString();
			gradeTTxt=gradeT.getText().toString();
			thickTTxt=thickT.getText().toString();
			idTTxt=idT.getText().toString();
		}

        /**
         * Обработка результатов запроса
         * @param result
         */
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
            // Если есть результаты
			if(slist.size()>0){
				((ChangeFragment)getActivity()).setSearchList(slist);
			}
			// иначе
			else{
				Toast.makeText(getActivity(), "Ничего не найдено", Toast.LENGTH_SHORT).show();
			}
		}

        /**
         * Запрос в отдельном потоке
         * @param arg0
         * @return
         */
		@Override
		protected Boolean doInBackground(Integer... arg0) {
            // Конструктор запроса
			HttpUrl.Builder urlBuilder = HttpUrl.parse("http://tlc2.amk.lan/sklad_lo/android/lists_byPlace.php").newBuilder();
			urlBuilder.addQueryParameter("db", "'"+startDateTxt+"'");
			urlBuilder.addQueryParameter("de", "'"+endtDateTxt+"'");
			urlBuilder.addQueryParameter("pred", predTTxt);
			urlBuilder.addQueryParameter("ord", orderTTxt);
			urlBuilder.addQueryParameter("grade", gradeTTxt);
			urlBuilder.addQueryParameter("heat", heatTTxt);
			urlBuilder.addQueryParameter("h", thickTTxt);
			urlBuilder.addQueryParameter("slabid", idTTxt);
			String request = urlBuilder.build().toString();
			try {
                // Создание запроса
				Request rq = new Request.Builder()
						.url(request)
						.build();
                // Получение результатов
				Response response = client.newCall(rq).execute();
                // Обработка результатов
				JSONArray arr=new JSONArray(response.body().string());
				for( int i=0;i<arr.length();i++){
                    // Если ошибка
						try{
						    if(arr.getJSONObject(i)==null)
						        continue;
						}
						catch(org.json.JSONException e){
							continue;
						}
						// Обработка парсинг элемента
						JSONObject el = arr.getJSONObject(i);
						if( el.has("place")) {
							final String place=el.getString("place");
							JSONArray data = el.getJSONArray("data");
							for(int j=0;j<data.length();j++){
								JSONObject obj = data.getJSONObject(i);
								if(obj.has("idslab")){
									PlaceList pl = PlaceList.getIdKrat(obj.getString("idslab"), place);
									if(pl!=null) slist.add(pl);
								}
							}
						}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally{
				
			}
			return true;
			
		}
		
	}

}
