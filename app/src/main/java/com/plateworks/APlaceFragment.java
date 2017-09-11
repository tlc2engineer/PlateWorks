package com.plateworks;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.plateworks.R;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tech.Heat;
import tech.Place;
import tech.Plate;
import tech.Pred;
import tech.Yard;

import static com.example.plateworks.R.string.sim_on;
import static com.plateworks.MapFragment.actColumn;

/**
 * Фрагмент основного окна для перемещения листов. Окно делится на две части, в каждой части
 * раскрываемый список листов, построенный в виде дерева.
 */

public class APlaceFragment extends Fragment {
    // Имена мест
    TextView placeView1,placeView2;
    // Начальные значения колонок и столбцов.
    int colNum1=19,colNum2=20,rowNum1=1,rowNum2=1;
    // Кнопки управления
    ImageButton upd1,upd2,map_btn1,map_btn2,post_btn1,post_btn2;
    // Списки листов для перемещения
    RecyclerView listPlace1;
    RecyclerView listPlace2;
    // Склад
    private Yard yard;
    // Места 1 и 2
    Place place1,place2;
    // Адаптеры для RecycleView
    private PlateRAdapter adapter1;
    private PlateRAdapter adapter2;
    // Интерфейс для связи с Activity
    private ChangeFragment cf;
    // OkHttp client
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Создание view фрагмента
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        // Получение размеров окна
        final Window win=this.getActivity().getWindow();
        final WindowManager wm=this.getActivity().getWindowManager();
        final Display display = this.getActivity().getWindow().getWindowManager().getDefaultDisplay();
        final DisplayMetrics rm = new DisplayMetrics();
        display.getRealMetrics(rm);
        // Установка разводки
        final View view = inflater.inflate(R.layout.nplace_layout, null);
        view.post(new Runnable() {
            @Override
            public void run() {

                // Вычисление реальных размеров окна
                Rect rect = new Rect();
               // Часть экрана не занятого приложеием декор
                win.getDecorView().getWindowVisibleDisplayFrame(rect);
                // Высота экрана не занятого приложением
                int contentViewTop = win.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                // Установка размеров в зависимости от ориентации.
                if(display.getRotation()==Surface.ROTATION_0 || display.getRotation()==Surface.ROTATION_180)
                    listPlace1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (rm.heightPixels-(rm.densityDpi/160)*82-contentViewTop )/2));
                else{
                    LinearLayout root= (LinearLayout) view.findViewById(R.id.root);
                    root.setLayoutParams(new LinearLayout.LayoutParams(rm.widthPixels, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
        });
        // Получение поля склада
        yard= Yard.getInstance();
        // Инициализация элементов
        upd1= (ImageButton) view.findViewById(R.id.updbtnx1);
        upd2= (ImageButton) view.findViewById(R.id.updbtnx2);
        map_btn1=(ImageButton) view.findViewById(R.id.cmap_btn1);
        map_btn2=(ImageButton) view.findViewById(R.id.cmap_btn2);
        post_btn1=(ImageButton) view.findViewById(R.id.postBtn1);
        post_btn2=(ImageButton) view.findViewById(R.id.postBtn2);
        listPlace1= (RecyclerView) view.findViewById(R.id.mlist1);
        listPlace2= (RecyclerView) view.findViewById(R.id.mlist2);
        placeView1= (TextView) view.findViewById(R.id.place_view1);
        placeView2= (TextView) view.findViewById(R.id.place_view2);
        // Ссылка на Activity через интерфейс
        cf = (ChangeFragment) this.getActivity();
        // Получение номеров колонок и столбцов из активности
        colNum1=cf.getStartColumn(1);
        colNum2=cf.getStartColumn(2);
        rowNum1=cf.getStartRow(1);
        rowNum2=cf.getStartRow(2);
        // Установка имен мест на складе
        placeView1.setText(""+colNum1+"-"+rowNum1);
        placeView2.setText(""+colNum2+"-"+rowNum2);
        // Обработчик вызова карты
        map_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cf.setMap(1);
            }
        });
        map_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cf.setMap(2);
            }
        });
        //Обработчик изменения позиции
        place1=yard.getPlace(colNum1,rowNum1);
        place2=yard.getPlace(colNum2,rowNum2);
        // Обработчик кнопок обновления
        upd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePosition1(place1);
            }
        });
        upd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePosition2(place2);
            }
        });
        // Обработчик кнопки посылки листа
        post_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postPlates(1);

            }
        });
        post_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postPlates(2);

            }
        });
        //--------------------------Первое место------------------------------------
        // Создание адаптера RecycleView для первого места
        adapter1=new PlateRAdapter(this.getActivity(),place1);
        listPlace1.setAdapter(adapter1);
        // Установка менеджера компоновки
        listPlace1.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        //------------------------------------Второе место--------------------------------------------------------
        // Аналогично
        adapter2=new PlateRAdapter(this.getActivity(),place2);
        listPlace2.setAdapter(adapter2);
        listPlace2.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        // Установка первой и второй позиции
        changePosition1(place1);
        changePosition2(place2);
        return view;
    }
    /**
     * Функция установки позиции 1. Загружает данные о листах по данной позиции.
     * @param place Позиция.
     */
    public void changePosition1(Place place){
        // Присвоение позиции
        this.place1=place;
        // Загрузка в adapter1
        adapter1.changePlace(place1);
        boolean simulate_on=((Simulated)APlaceFragment.this.getActivity()).getSimOn();
        // если не режим симуляции получение данных по сети
        if(!simulate_on) {
            GetDataToList get = new GetDataToList(place1.getColumn(),place1.getRow(),1);
            get.execute(place1.getColumn(), place1.getRow());
        }
        else{
            adapter1.notifyDataSetChanged();
            listPlace1.invalidate();
        }
        ;
    }

    /**
     * Функция установки второй позиции. Работает аналгично первой функции
     * @param place Позиция.
     */
    public void changePosition2(Place place){

        this.place2=place;
        adapter2.changePlace(place2);
        boolean simulate_on=((Simulated)APlaceFragment.this.getActivity()).getSimOn();
        if(!simulate_on) {
            GetDataToList get = new GetDataToList(place2.getColumn(),place2.getRow(),2);
            get.execute(place2.getColumn(), place2.getRow());
        }
        else{
            adapter2.notifyDataSetChanged();
            listPlace2.invalidate();

        }
    }

    /**
     * Класс загрузки данных по позиции по сети. Использует httpOk. Наследует AsyncTask.
     *
     */
    private class GetDataToList extends AsyncTask<Integer,Integer,Boolean> {
        // Позиция
        Place place;
        // Прогресс диалог
        private ProgressDialog dialogS;
        // Номер колонки и ряда
        int column,row;
        // Номер адаптера.
        int id;

        /**
         * Конструктор класса.
         * @param column колонка
         * @param row ряд
         * @param id номер адаптера
         */
        GetDataToList(int column,int row,int id){
            this.column=column;
            this.row=row;
            this.id=id;
        }

        /**
         * Подготовка к загрузке. Отображение диалога прогресса операции.
         */
        @Override
        protected void onPreExecute() {
            dialogS=new ProgressDialog(APlaceFragment.this.getActivity());
            dialogS.setMessage(getResources().getString(R.string.place)+" "+column+"-"+row);
            dialogS.setProgress(10);
            dialogS.show();

        }

        /**
         * Действия после загрузки
         * @param result результат загрузки
         */
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialogS.dismiss();
            // Если нет такого места
            if(place==null){
                Toast toast = Toast.makeText(APlaceFragment.this.getActivity(), getResources().getString(R.string.no_such_place),Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            // Если первый адаптер
            if(id==1) {
                adapter1.resetlevel();
                adapter1.notifyDataSetChanged();
            }
            // Если второй адаптер
            else {
                adapter2.resetlevel();
                adapter2.notifyDataSetChanged();
            }

        }

        /**
         * Процесс загрузки листа
         * @param params не используется.
         * @return true при удачной загрузке
         */
        @Override
        protected Boolean doInBackground(Integer... params) {
            // Если не сети.
            if(MActivity.noNet) return null;
            // Построение url
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://tlc2.amk.lan/sklad_lo/android/lists_byPlace.php").newBuilder();
            urlBuilder.addQueryParameter("place", "'"+column+"-"+row+"'");
            String request = urlBuilder.build().toString();
            // Загрузка
            try {
                // Очистка данных по данному месту
                if(place==null)
                    place=yard.getPlace(column, row);
                place.clear();
                // Создание запроса
                Request rq = new Request.Builder()
                        .url(request)
                        .build();
                // Получение ответа
                Response response = client.newCall(rq).execute();
                // Перевод данных в json и обработка данных.
                JSONArray arr=new JSONArray(response.body().string());
                for(int i=0;i<arr.length();i++){
                    Plate plate=Plate.parse(arr.getJSONObject(i));
                    if(plate!=null) {
                        place.addPlate(plate);
                    }
                }

            } catch (IOException e) {
                place.clear();
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Класс передачи листов по сети.
     */
    public class Post extends AsyncTask<Integer,Integer,Boolean> {
    // Прогресс диалог
        private ProgressDialog dialogS;
        // Имя
        private String name;
        // Ряд колонка
        int column,row;
        // Ошибка
        boolean error=false;

        /**
         * Конструктор
         * @param name Имя
         * @param column ряд
         * @param row колонка
         */
        public Post(String name,int column,int row){
            this.name=name;
            this.column=column;
            this.row=row;
        }
        /**
         * Подготовка к загрузке. Отображение диалога прогресса операции.
         */
        @Override
        protected void onPreExecute() {
            dialogS=new ProgressDialog(APlaceFragment.this.getActivity());
            dialogS.setProgress(10);
            dialogS.setMessage("Передача листа "+name);
            dialogS.show();
        }

        /**
         * Обработка завершения передачи данных.
         * @param result результат передачи
         */
        @Override
        protected void onPostExecute(Boolean result) {
            // Если ошибка передачи данных
            if(error){
                Toast tst=Toast.makeText(APlaceFragment.this.getActivity(), "Ошибка передачи данных на место "+column+"-"+row, Toast.LENGTH_SHORT);
                tst.show();
                dialogS.dismiss();
                return;
            }
            // Сброс диалога.
            dialogS.dismiss();
            // Изменение данных по позиции 1 и 2.
            changePosition1(place1);
            changePosition2(place2);
        }

        /**
         * Функция передачи данных в отдельном потоке.
         * @param params 1 параметр номер листа
         * @return результат передачи
         */
        @Override
        protected Boolean doInBackground(Integer... params) {

            return post(params[0]);
        }

        /**
         * Функиция передачи данных
         * @param id номер листа
         * @return
         */
        private boolean post(int id){
            try {
                // Формирование запроса
                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://tlc2.amk.lan/sklad_lo/android/move_list_from_android").newBuilder();
                urlBuilder.addQueryParameter("idlist",""+id);
                urlBuilder.addQueryParameter("place", "'"+column+"-"+row+"'");
                String request = urlBuilder.build().toString();
                // Запрос post
                Request rq = new Request.Builder()
                        .url(request).post(RequestBody.create((MediaType.parse("text; charset=utf-8")),""))
                        .build();
                // Получение ответа
                Response response = client.newCall(rq).execute();
                // Возврат true при удачной передаче
                return true;
            } catch (IOException e) {
                // Установка флага ошибки.
                error=true;
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Обработка передачи листов с одного места на другое
     * @param id идентификатор позиции
     */
    public void postPlates(int id) {
        Place sourcePlace=null;
        Place targetPlace=null;
        // Если id=1 передача листов с первой позиции на вторую
        if(id==1){
            sourcePlace = place1;
            targetPlace= place2;
        }
        // иначе
        else{
            sourcePlace = place2;
            targetPlace = place1;
        }
        // Создание списка передаваемых листов.
        List<Plate> postList=new ArrayList<Plate>();
        for(Plate plate: sourcePlace.getPlates()){
            if(plate.isSelected())
                postList.add(plate);
        }
        // Флаг симуляции
        boolean sim_on=((Simulated)APlaceFragment.this.getActivity()).getSimOn();
        if(!sim_on)
            // Если нет симуляции передача по сети
            addPlates(postList,targetPlace.getColumn(),targetPlace.getRow());
        else{
            // Иначе просто перекладываем листы.
            yard.moveListOffline(sourcePlace,targetPlace,postList,APlaceFragment.this.getActivity());
            adapter1.resetlevel();
            adapter2.resetlevel();
            adapter1.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
        }
    }

    /**
     * Обработка передачи листов по сети
     * @param plates список листов
     * @param column колонка
     * @param row ряд
     */
    private void addPlates(final Collection<Plate> plates, final int column, final int row){
        // Создание диалога передачи
        AlertDialog.Builder addPlateDialog = new AlertDialog.Builder(APlaceFragment.this.getActivity());
        StringBuilder platesText=new StringBuilder();
        for(Plate plate: plates){
            platesText.append(plate.toString()+",");
        }
        addPlateDialog.setMessage(getResources().getString(R.string.add_plates)+" "+platesText+" "+getResources().getString(R.string.to_place)+" "+yard.getPlace(column, row)+"?");
        addPlateDialog.setTitle(getResources().getString(R.string.add));
        // Добавление кнопки подтверждения и ее обработчика
        addPlateDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Цикл передачи листов
                for(Plate plate: plates){
                    Post post=new Post(plate.toString(),column,row);
                    post.execute(plate.getUniq());
                }

            }
        });
        // Отображение диалога передачи.
        AlertDialog dialog = addPlateDialog.create();
        dialog.setCancelable(true);
        dialog.show();
    }

}
