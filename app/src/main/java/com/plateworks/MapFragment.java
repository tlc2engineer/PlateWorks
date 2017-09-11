package com.plateworks;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tech.Place;
import tech.Plate;
import tech.Yard;
import util.Simulation;

import com.example.plateworks.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

/**
 * Фрагмент отображающий карту склада.
 */
public class MapFragment extends Fragment {
    // Ширина карты 8000 пикселей.
	public static int allWidth=8000;
    // PlateMap тображает карту
	private PlateMap map;
    // активный столбец и активная колонка
    public static int actRow=1,actColumn=19;
    // позиция 1 или вторая
    int contId;
    // http клиент
    private final OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(5, TimeUnit.SECONDS).build();
    // Установка позиции
	public void setContId(int contId) {
		this.contId = contId;
	}
    // Создание View
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        boolean simulate_on=((Simulated)MapFragment.this.getActivity()).getSimOn();
        // нет симуляции загрузка карты по сети
        if(!simulate_on){
            ListPresentData works = new ListPresentData();
            works.execute("");
        }
        // иначе из памяти
		else{
			Yard yard = Yard.getInstance();
			for(int ncolumn=19;ncolumn<=86;ncolumn++){
				for(int nrow=1; nrow<9;nrow++){
					Place place = yard.getPlace(ncolumn, nrow);
					place.listCount=place.getSize();
				}
			}
		}
		// Обработка элементов разводки
		LinearLayout l1= (LinearLayout) inflater.inflate(R.layout.maplayout, container, false);
		HorizontalScrollView hv=(HorizontalScrollView) l1.getChildAt(1);
		LinearLayout l2=(LinearLayout) hv.getChildAt(0);
		map= new PlateMap(this.getActivity());
		map.setLayoutParams(new LayoutParams(allWidth,LayoutParams.MATCH_PARENT));
		l2.addView(map);
		hv=(HorizontalScrollView) l1.findViewById(R.id.mapScrollView);
		hv.scrollTo(400, 0);
		ImageButton toPlace=(ImageButton) l1.findViewById(R.id.place_btn);
        // Кнопка возврата к основному экрану. Обработка.
		toPlace.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ChangeFragment cf=(ChangeFragment) getActivity();
				cf.setPlace(contId,actColumn,actRow);
				
			}
			
		});
		return l1;
	}

    /**
     * Возврат активной колонны
     * @return
     */
	public int getColumn(){
		return map.getActColumn();
	}

    /**
     * Возврат активного ряда.
     * @return
     */
	public int getRow(){
		return map.getActRow();
	}

    /**
     * Загрузка карты.
     */
	 class ListPresentData extends AsyncTask {
        // Диалог прогресса загрузки
	private ProgressDialog dialogS;
// Установка диалога прогресса загрузки
		 @Override
		 protected void onPreExecute() {
			 super.onPreExecute();
			 dialogS=new ProgressDialog(MapFragment.this.getActivity());
			 dialogS.setMessage("Загрузка карты");
			 dialogS.setProgress(10);
			 dialogS.show();
		 }

        /**
         * Загрузка по сети
         * @param params не используется
         * @return
         */
		 @Override
		protected Object doInBackground(Object... params) {
			//Если есть сеть загрузка по сети
			if(!MActivity.noNet)
			{
				jsonReader();

			}
			// Если нет сети загрузка данных из памяти
			else
			{
				Yard yard=Yard.yard;
				for(int row=1;row<9;row++){
					for(int column=19;column<87;column++){
						Place place=yard.getPlace(column, row);
						int countList=0;
						if(place!=null){
							countList=(int) (Math.random()*10+1);
							place.listCount=countList;
							for(int i=0;i<countList;i++){
								String grade= Simulation.grades[(int) (Math.random()*8)];
								String heat=Simulation.heats[(int) (Math.random()*8)];
								int number=Simulation.orderNums[(int) (Math.random()*8)];
								Plate plate=new Plate(grade, heat, new Plate.Size(20.0, 2500.0, 10000.0),  number, column*row*10+i,
										1, number, 2000,column*row*10+i);
								place.addPlate(plate);
							}
						}
					}
				}
			}
			return true;
		}
		@SuppressLint("NewApi")
		private void jsonReader(){
			Yard yard=Yard.yard;
			try {
                // Создание url
                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://tlc2.amk.lan/sklad_lo/android/place_countlist.php").newBuilder();
                String request = urlBuilder.build().toString();
                Request rq = new Request.Builder()
                        .url(request)
                        .build();
                // Запрос данных
                Response response = client.newCall(rq).execute();
                // Обработка данных в json
                JSONArray arr=new JSONArray(response.body().string().split("</script>")[1]);
				for(int i=0;i<arr.length();i++){
					JSONObject obj =(JSONObject) arr.get(i);
					try{
						int count=obj.getInt("count");
						String placeS=obj.getString("place");
						String[] rowcol=placeS.split("-");
						int column=Integer.valueOf(rowcol[0]);
						int row=Integer.valueOf(rowcol[1]);
                        // Получение места
						Place place= yard.getPlace(column, row);
                        // Передача данных
						place.listCount=count;
					}
					catch(JSONException ex){

					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}

        /**
         * Закрытие  диалога
         * @param o
         */
		 @Override
		 protected void onPostExecute(Object o) {
			 super.onPostExecute(o);
             dialogS.dismiss();
		 }
	 }

}
