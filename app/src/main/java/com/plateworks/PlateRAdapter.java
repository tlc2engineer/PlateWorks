package com.plateworks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.plateworks.R;
import java.util.ArrayList;
import java.util.List;
import tech.Heat;
import tech.Place;
import tech.Plate;
import tech.PlateContainer;
import tech.Pred;

/**
 * Адаптер для RecycleView служит для отображения листов на позиции 1 и 2.
 */

public class PlateRAdapter extends RecyclerView.Adapter<PlateRAdapter.ViewHolder>{
    // Глубина отображения. 1- заказы, 2 - плавки 3 - листы
    private int deepLevel=1;
    // Текущий номер заказа
    private int currentOrderNum=0;
    // Текущий номер плавки
    private int currentHeatNum=0;
    // Список отображаемых объектов.
    List<PlateContainer> list=new ArrayList();
    // Context
    private Context context;
    // Место для данной позиции
    private Place place;
    /**
     * Функция изменения места.
     */
    public void changePlace(Place place){
        this.place=place;
    }

    /**
     * Конструктор
     * @param context контекст
     * @param place место.
     */
    public PlateRAdapter(Context context,Place place){
        this.context=context;
        this.place=place;
        // Отображение только заказов
        changeDeepLevel(1);
    }

    /**
     * Создание viewHolder
     * @param parent
     * @param type
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        // если заказ
        if(type==2){
            LinearLayout orderView = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.pred_item, null);
            orderView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            OrderHolder oh = new OrderHolder(orderView);
            return  oh;
        }
        // если плавка
        if(type==1){
            LinearLayout heatView = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.heat_item_layout, null);
            heatView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            HeatHolder hh = new HeatHolder(heatView);
            return  hh;

        }
        // Если лист
        if(type==0) {
            LinearLayout plateView = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.plate_item_layout, null);
            plateView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            PlateHolder ph = new PlateHolder(plateView);
            return  ph;
        }
        // невозможно
        throw new IllegalStateException();
    }

    /**
     * Привязка
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Связанный элемент списка
        final PlateContainer item = list.get(position);
        // Если это лист то получение информации при longClick
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               if(item instanceof Plate){
                   Plate plate =(Plate) item;
                    AlertDialog.Builder editDaysDialog = new AlertDialog.Builder(context);
                    editDaysDialog.setMessage(plate.getFullInfO());
                    editDaysDialog.setTitle(context.getResources().getString(R.string.descr));
                    editDaysDialog.setPositiveButton("OK", null);
                    AlertDialog dialog = editDaysDialog.create();
                    dialog.setCancelable(true);
                    dialog.show();

                }
                return false;
            }
        });
        //--------------------Если заказ--------------------------------
        if(holder instanceof  OrderHolder){
            // Присваивание значений элементам
            final OrderHolder oholder = (OrderHolder) holder;
            final Pred pred= (Pred)item;
            oholder.view.setText(" "+pred.getOrdNumber());
            oholder.predView.setText(" "+pred.getNumber());
            oholder.countView.setText(""+pred.getCount());
            oholder.massView.setText(String.format("%.2f",pred.getMass()));
            // Сброс флагов обновления
            pred.exp_mem=pred.exp;
            pred.upd=false;
            // Обработка значения кнопки +-
            if(position==currentOrderNum && deepLevel>1) oholder.plus.setP(false);
            else
                oholder.plus.setP(true);
            // Обработка шапки
            if(position>0 &&  (list.get(position-1) instanceof Pred) ){
                oholder.layout.removeView(oholder.head);
            }
            else{
                if(oholder.layout.getChildCount()==1){
                    oholder.layout.addView(oholder.head,0);

                }
            }
            // Обработка нажатия кнопки +-
            oholder.plus.setOnClickListener(new View.OnClickListener(){
                // Сброс флагов + у всех кнопок
                @Override
                public void onClick(View v) {
                    for(PlateContainer pc:list){
                        if(pc instanceof Pred){
                            pc.exp=false;
                        }
                    }
                    // Если представление заказа развернуто свернуть
                    if(currentOrderNum==place.getOrders().indexOf(pred) && deepLevel>1){
                        pred.exp=false;
                        if(currentOrderNum<(place.getOrders().size()-1)){
                            Pred prd = place.getOrders().get(currentOrderNum + 1);
                            prd.upd=true;

                        }
                        changeDeepLevel(1);
                    }
                    // иначе развернуть
                    else{
                        if(currentOrderNum<(place.getOrders().size()-1)){
                            Pred prd = place.getOrders().get(currentOrderNum + 1);
                            prd.upd=true;
                        }
                        currentOrderNum=place.getOrders().indexOf(pred);
                        pred.exp=true;
                        if(currentOrderNum<(place.getOrders().size()-1)){
                            Pred prd = place.getOrders().get(currentOrderNum + 1);
                            prd.upd=true;
                        }
                        changeDeepLevel(2);
                    }
                }

            });
            // Если заказ выбран установить флаг
            oholder.cb.setChecked(pred.isSelected());
            // Обработка выбора заказа
            oholder.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pred.setSelected(oholder.cb.isChecked());
                    notifyDataSetChanged();
                }
            });
        }
        //---------------------Если плавка---------------------------------
        if(holder instanceof  HeatHolder){
            // Присваивание значений элементам
            final HeatHolder hholder = (HeatHolder) holder;
            final Heat heat= (Heat) item;
            hholder.view.setText(heat.toString());
            hholder.gradeText.setText(heat.getGrade());
            hholder.countView.setText(""+heat.getCount());
            hholder.massView.setText(String.format("%.2f",heat.getMass()));
            final int heatNum = place.getOrders().get(currentOrderNum).getHeats().indexOf(item);
            // Сброс флагов обновления
            heat.exp_mem=heat.exp;
            // Обработка значения кнопки +-
            if(deepLevel>2 && currentHeatNum==heatNum)
                hholder.plus.setP(false);
            else
                hholder.plus.setP(true);
            // Обработка отрисовки шапки
            if(  (list.get(position-1) instanceof Heat) ){
                            hholder.layout.removeView(hholder.head);
            }
            else{
                if(hholder.layout.getChildCount()==1){
                    hholder.layout.addView(hholder.head,0);

                }
            }
            // Обработка нажатия кнопки +-
            hholder.plus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // Сброс флагов + у всех кнопок
                    for(PlateContainer pc:list){
                        if(pc instanceof Heat){
                            pc.exp=false;
                        }
                    }
                    // Если представление развернуто свернуть
                    if(currentHeatNum==heatNum && deepLevel>2){
                        heat.exp=false;
                        changeDeepLevel(2);
                    }
                    else
                        // иначе развернуть
                    {
                        heat.exp=true;
                        currentHeatNum=heatNum;
                        changeDeepLevel(3);
                    }
                }
            });
            // Если плавка выбрана установить флаг
            hholder.cb.setChecked(heat.isSelected());
            // Обработчик выбора плавки
            hholder.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    heat.setSelected(hholder.cb.isChecked());
                    notifyDataSetChanged();
                }
            });

        }
        //----------------------Если лист------------------------------
        if(holder instanceof  PlateHolder){
            // Присваивание значений элементам
            final PlateHolder pholder = (PlateHolder) holder;
            final Plate plate= (Plate) item;
            pholder.view.setText(""+plate.hashCode()/10);
            pholder.kratView.setText(""+plate.hashCode()%10);
            pholder.sizeView.setText(plate.getSize());
            pholder.massView.setText(""+plate.getMass());
            // Обработка отрисовки шапки
            if(  (list.get(position-1) instanceof Plate) ){
                pholder.layout.removeView(pholder.head);
            }
            else{
                if(pholder.layout.getChildCount()==1){
                    pholder.layout.addView(pholder.head,0);
                }
            }
            // Если лист выбран установить флаг
            pholder.cb.setChecked(plate.isSelected());
            // Обработка выбора листа
            pholder.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plate.setSelected(pholder.cb.isChecked());
                    notifyDataSetChanged();
                }
            });
        }

    }

    /**
     * Количество отображаемых элементов.
     * @return
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Тип отображения. Если заказ -2 плавка -1 лист 0
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        PlateContainer item = list.get(position);
        if(item instanceof  Plate) return 0;
        if(item instanceof  Heat) return 1;
        return 2;

    }


    /**
     * Базовый класс ViewHolder
     */
    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        // Разводка
        final public LinearLayout layout;
        public ViewHolder(LinearLayout layout) {
            super(layout);
            this.layout=layout;

        }
    }

    /**
     * Класс для хранения отоображения заказа.
     */
    public class OrderHolder extends ViewHolder{
        final TextView view;
        final TextView predView;
        final TextView countView;
        final TextView massView;
        final PButton plus;
        final CheckBox cb;
        final View head;


        public OrderHolder(LinearLayout layout) {
            super(layout);
            view=(TextView) layout.findViewById(R.id.item_order_text);
            predView=(TextView) layout.findViewById(R.id.pred_text);
            countView=(TextView) layout.findViewById(R.id.item_order_count);
            massView=(TextView) layout.findViewById(R.id.item_order_mass);
            plus=new PButton(context);
            LinearLayout llpb=(LinearLayout) layout.findViewById(R.id.item_order_pb);
            llpb.addView(plus);
            cb=(CheckBox) layout.findViewById(R.id.item_order_check);
            head = layout.findViewById(R.id.head_pred_item);
        }
    }
    /**
     * Класс для хранения отоображения плавки.
     */
    public class HeatHolder extends ViewHolder{
        final TextView view;
        final TextView gradeText;
        final TextView countView;
        final TextView massView;
        final PButton plus;
        final CheckBox cb;
        final View head;


        public HeatHolder(LinearLayout layout) {
            super(layout);

            view=(TextView) layout.findViewById(R.id.item_heat_text);
            gradeText = (TextView) layout.findViewById(R.id.item_grade_text);
            countView=(TextView) layout.findViewById(R.id.item_heat_count);
            massView=(TextView) layout.findViewById(R.id.item_heat_mass);
           // heatNum= place.getOrders().get(currentOrderNum).getHeats().indexOf(item);
            plus=new PButton(context);
            LinearLayout llpb=(LinearLayout) layout.findViewById(R.id.item_heat_pb);
            llpb.addView(plus);
            cb=(CheckBox) layout.findViewById(R.id.item_heat_check);
           head = layout.findViewById(R.id.head_heat_item);
        }
    }
    /**
     * Класс для хранения отоображения листа.
     */
    public class PlateHolder extends ViewHolder{
        final TextView view;
        final TextView kratView;
        final TextView sizeView;
        final TextView massView;
        private final CheckBox cb;
        private final View head;

        public PlateHolder(LinearLayout layout) {
            super(layout);
            view= (TextView) layout.findViewById(R.id.item_plate_text);
            kratView=(TextView) layout.findViewById(R.id.item_plate_krat);
            sizeView=(TextView) layout.findViewById(R.id.item_plate_size);
            massView=(TextView) layout.findViewById(R.id.item_plate_mass);
             cb=(CheckBox) layout.findViewById(R.id.item_plate_check);
            head = layout.findViewById(R.id.head_plate_item);
        }
    }

    /**
     * Переход на 1 уровень.
     */
    public void resetlevel(){
        changeDeepLevel(1);
    }
    /**
     * Задание глубины отображения.
     * @param level глубина
     */
    private void changeDeepLevel(int level){
        // Копирование старого списка отображения
        List<PlateContainer> oldList=new ArrayList<>();
        oldList.addAll(list);
        // Очистка списка
        list.clear();
        // Заказы
        List<Pred> orders=place.getOrders();
        // Плавки
        List<Heat> heats;
        switch(level){
            // Отображаются только заказы
            case 1:
                deepLevel=1;
                list.addAll(orders);
                break;
            // Отображаются заказы и плавки
            case 2:
                // Список плавок
                heats=orders.get(currentOrderNum).getHeats();
                // Заказы до текущего
                for(int i=0;i<=currentOrderNum;i++){
                    list.add(orders.get(i));
                }
                // Плавки текущего заказа
                for(int i=0;i<heats.size();i++){
                    list.add(heats.get(i));
                }
                // Заказы после текущего
                for(int i=currentOrderNum+1;i<orders.size();i++){
                    list.add(orders.get(i));
                }
                deepLevel=2;
                break;
            // Отображаются плавки заказы и листы
            case 3:
                // Список плавок
                heats=orders.get(currentOrderNum).getHeats();
                // Заказы до текущего
                for(int i=0;i<=currentOrderNum;i++){
                    list.add(orders.get(i));
                }
                // Плавки до текущей плавки
                for(int i=0;i<=currentHeatNum;i++){
                    list.add(heats.get(i));
                }
                // Листы текущей плавки
                list.addAll(heats.get(currentHeatNum).getPlates());
                //--------------------------------------
                // Плавки после текущей
                for(int i=currentHeatNum+1;i<heats.size();i++){
                    list.add(heats.get(i));
                }
                // Заказы после текущего
                for(int i=currentOrderNum+1;i<orders.size();i++){
                    list.add(orders.get(i));
                }
                deepLevel=3;
                break;

        }
        // У всех объектов удаляемых из списка сброс флагов
        for(PlateContainer pl:oldList){
            if(!list.contains(pl)){
                pl.exp=false;
                pl.exp_mem=false;
            }
        }
        // Обновление содержимого адаптера. Старый список сравнивается с новым.
        final ContainerDiffCallback diffCallback = new ContainerDiffCallback(oldList, list);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.dispatchUpdatesTo(this);

    }
}
