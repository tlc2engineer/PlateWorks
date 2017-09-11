
package tech;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Класс данных поля склада
 */
public class Yard implements Serializable,Iterable<Place>{
    // http client
    private final OkHttpClient client = new OkHttpClient();
    // Имя файла данных по складу
    private final String plates_file_name="plates.dat";
    // границы
    private final static int min_row=1,max_row=8,min_column=19,max_column=86;
    // карта мест
    private final   Map<Position,Place> places=new HashMap<Position,Place>();
    // синглтон
    public static final Yard yard=new Yard();
    // текущая колонна и ряд
    private int currentPlaceRow=min_row;
    private int currentPlaceColumn=min_column;
    // Получение склада
    public static Yard getInstance(){
        if(yard==null) return new Yard();
        else
            return yard;
    }

    /**
     * Конструктор
     */
    private Yard(){
     addPlaces();
    }

    /**
     * Получение места
     * @param column колонна
     * @param row ряд
     * @return место
     */
    public Place getPlace(int column,int row){
    	Position p=new Position(column,row);
        Place place= places.get(p);
        if(place==null) place=Place.emptyPlace;
        return place;
    }

    /**
     * Добавление мест
     */
    private void addPlaces(){
         int row=1;
         for(int column=19;column<=86;column++){
            places.put(new Position(column,row),new Place(row,column));
        }
        row=2;
        for(int column=50;column<=86;column++){
            places.put(new Position(column,row),new Place(row,column));
        }
        row=3;
          for(int column=19;column<=69;column++){
        	  places.put(new Position(column,row),new Place(row,column));
        }
          places.put(new Position(75,row),new Place(row,75));
          places.put(new Position(81,row),new Place(row,81));
          places.put(new Position(85,row),new Place(row,85));
          places.put(new Position(86,row),new Place(row,86));
        row=4;
        places.put(new Position(19,row),new Place(row,19));
          for(int column=26;column<=31;column++){
        	  places.put(new Position(column,row),new Place(row,column));
        }
          places.put(new Position(36,row),new Place(row,36));
          places.put(new Position(50,row),new Place(row,50));
           for(int column=54;column<=61;column++){
        	   places.put(new Position(column,row),new Place(row,column));
        }
           places.put(new Position(75,row),new Place(row,75));
           places.put(new Position(81,row),new Place(row,81));
           places.put(new Position(86,row),new Place(row,86));
           row=5;
           places.put(new Position(50,row),new Place(row,50));
               for(int column=55;column<=61;column++){
            	   places.put(new Position(column,row),new Place(row,column));
               }
            row=6;
            places.put(new Position(44,row),new Place(row,44));
            places.put(new Position(46,row),new Place(row,46));
            places.put(new Position(48,row),new Place(row,48));
            places.put(new Position(50,row),new Place(row,50));
             for(int column=55;column<=61;column++){
            	 places.put(new Position(column,row),new Place(row,column));
               }
             row=7;
             places.put(new Position(50,row),new Place(row,50));
              row=8;
              places.put(new Position(19,row),new Place(row,19));
              places.put(new Position(44,row),new Place(row,44));
              places.put(new Position(46,row),new Place(row,46));
              places.put(new Position(48,row),new Place(row,48));
              places.put(new Position(50,row),new Place(row,50));
              places.put(new Position(80,row),new Place(row,80));
    }

    /**
     * Итератор по местам
     * @return
     */
    @Override
    public Iterator<Place> iterator() {
        return new Iterator<Place>() {
            int row=0;
            int column=19;
            int code=190;
            @Override
            public boolean hasNext() {
                return code<864;
            }
            @Override
            public Place next() {
                code+=1;
                int row=code%10;
                int column=(code-row)/10;
                while(yard.getPlace(column,row).equals(Place.emptyPlace)){
                    if(code==864) throw new IllegalStateException("864");
                        code+=1;
                        row=code%10;
                        column=(code-row)/10;
                        if(row>8) {
                            code = (column + 1) * 10 + 1;
                            row=code%10;
                            column=(code-row)/10;
                        }
                        if(code>864)
                            throw new IllegalStateException("Shaise");
                }
                return yard.getPlace(column,row);
            }
        };
    }

    /**
     * Класс позиции
     */
    private static class Position implements Serializable{
        // колонна ряд
    	int column,row;

        /**
         * Конструктор
         * @param column колонна
         * @param row ряд
         */
		public Position(int column,int row){
			this.column=column;
			this.row=row;
		}
		@Override
		public boolean equals(Object o) {
			if(o instanceof Position){
				Position p=(Position) o;
				return (column==p.column && row==p.row);
			}
			return false;
		}
		@Override
		public int hashCode() {
			return column+(row-1)*(max_column);
		}
		
    	
    }

    /**
     * Текущая колонна
     * @return
     */
    public int getCurrentColumn(){
    	return currentPlaceColumn;
    }

    /**
     * Текущий ряд
     * @return
     */
    public int getCurrentRow(){
    	return currentPlaceRow;
    }

    /**
     * Загрузка данных по сети
     * @param context
     */
    public void getDataToYard(Context context){
        GetDataToPlace gdtp=new GetDataToPlace(context);
        gdtp.execute(1,1);
    }

    /**
     * Класс загрузки карты всех листов по сети
     */
    private class GetDataToPlace extends AsyncTask<Integer,Integer,Boolean>{
        // Контекст
        private final Context context;
        // Прогресс диалог
        private final ProgressDialog dialogS;

        /**
         * Коннструктор
         * @param context контекст
         */
        public GetDataToPlace(Context context){
            this.context=context;
            dialogS=new ProgressDialog(context);
        }

        /**
         * Загрузка данных в отдельном потоке
         * @param integers
         * @return
         */
        @Override
        protected Boolean doInBackground(Integer... integers) {
            try {
                // Формирование запроса на карту количества листао
                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://tlc2.amk.lan/sklad_lo/android/place_countlist.php").newBuilder();
                String request = urlBuilder.build().toString();
                Request rq = new Request.Builder()
                        .url(request)
                        .build();
                //Запрос
                Response response = client.newCall(rq).execute();
                // Разложение данных
                JSONArray arr=new JSONArray(response.body().string().split("</script>")[1]);
                for(int i=0;i<arr.length();i++){
                    JSONObject obj =(JSONObject) arr.get(i);
                    try{
                        int count=obj.getInt("count");
                        String placeS=obj.getString("place");
                        String[] rowcol=placeS.split("-");
                        int column=Integer.valueOf(rowcol[0]);
                        int row=Integer.valueOf(rowcol[1]);
                        Place place= yard.getPlace(column, row);
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
            // Запрос данных по листам для каждого места
            for( int nrow=1;nrow<9;nrow++) {
                dialogS.setProgress(100*(int)((nrow-1)/8.0));
                for (int ncolumn = 19; ncolumn < 87; ncolumn++) {
                    final Place place=Yard.this.getPlace(ncolumn,nrow);
                    place.clear();
                    // Формирование запроса
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://tlc2.amk.lan/sklad_lo/android/lists_byPlace.php").newBuilder();
                    urlBuilder.addQueryParameter("place", "'"+ncolumn+"-"+nrow+"'");
                    String request = urlBuilder.build().toString();
                    if(place.equals(Place.emptyPlace) || place.listCount==0) continue;
                    try {
                        Request rq = new Request.Builder()
                                .url(request)
                                .build();
                        // Запрос
                        Response response = client.newCall(rq).execute();
                        // Получение результатов
                        JSONArray arr=new JSONArray(response.body().string());
                        for (int i = 0; i < arr.length(); i++) {
                            Plate plate = Plate.parse(arr.getJSONObject(i));
                            if (plate != null) {
                                place.addPlate(plate);
                            }
                        }
                        // Сохранение результатов в память
                        savePlatesToDisk(context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        /**
         * Установка диалогового окна
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogS.setProgress(0);
            dialogS.setMax(100);
            dialogS.setMessage("Закачка карты");
            dialogS.show();
        }

        /**
         * снятие диалогового окна
         * @param aBoolean
         */
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            dialogS.dismiss();
        }
    }

    /**
     * Сохранение данных в телефон
     * @param context
     */
   public  void savePlatesToDisk(Context context){
        try {
            FileOutputStream fos = context.openFileOutput(plates_file_name,Context.MODE_PRIVATE);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            Iterator<Place> it = iterator();
            List<PlaceItem> inst=new ArrayList<>();
            while(it.hasNext()){
                final   Place place=it.next();
            PlaceItem item=new SaveItem(place.getColumn(),place.getRow(),place.getPlates());
                inst.add(item);
            }
            oos.writeObject(inst);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Запрос данных по листам из памяти телефона. Используется при симуляции
     * @param context
     */
    public  void restorePlates(Context context){
        String[] fl = context.fileList();
        boolean exists=false;
        for(String fname: fl){
            if(fname.equals(plates_file_name)) {
                exists=true;
                break;
            }
        }
        if(exists) {
            try {
                FileInputStream fis = context.openFileInput(plates_file_name);
                ObjectInputStream ois = new ObjectInputStream(fis);

                List<PlaceItem> inst= (List<PlaceItem>) ois.readObject();
                for(PlaceItem item: inst){
                    int column = item.getColumn();
                    int row =item.getRow();
                    Place place=getPlace(column,row);
                    place.clear();
                    for(Plate plate:item.getPlates()){
                        place.addPlate(plate);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Удаление всех листов
     */
    public void clearAllPlaces(){
        Iterator<Place> iterator = iterator();
        while(iterator.hasNext()){
            iterator.next().clear();
        }
    }

    /**
     * Перемещение листов между местами при симуляции
     * @param source источник место
     * @param target место цель
     * @param plates листы
     * @param context
     */
    public void moveListOffline(Place source,Place target,List<Plate> plates,Context context){
        for(Plate plate:plates){
            source.removePlate(plate);
            target.addPlate(plate);

        }
        Toast tst = Toast.makeText(context, "Перемещение листов", Toast.LENGTH_SHORT);
        savePlatesToDisk(context);
        tst.show();
    }

    /**
     * Поиск листов при работе оффлайн
     * @param filt
     * @return
     */
    public List<PlaceItem> search(Predicate<Plate> filt){
        List<PlaceItem> results=new ArrayList<>();
        Iterator<Place> it = iterator();
        while(it.hasNext()){
            final Place place=it.next();
            final List<Plate>  search=place.getSearch(filt);
            if(search.size()!=0){
                results.add(new PlaceItem(){
                    @Override
                    public int getColumn() {
                        return place.getColumn();
                    }

                    @Override
                    public int getRow() {
                        return place.getRow();
                    }

                    @Override
                    public List<Plate> getPlates() {
                        return search;
                    }
                });
            }
        }
        return results;

    }
}

/**
 * Класс для созранения данных  в память
 */
class SaveItem implements PlaceItem,Serializable {
    private final int column;
    private final int row;
    private final List<Plate> plates;

    SaveItem(int column, int row, List<Plate>  plates){
        this.column = column;
        this.row=row;
        this.plates=plates;
        
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public List<Plate> getPlates() {
        return plates;
    }
}
