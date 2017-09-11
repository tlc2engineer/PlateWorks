package tech;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс данных места.
 */
public class Place implements Comparable,Serializable{
    // ряд,колонна
    private final  int row,column;
    // Карта предъявок
    private Map<Integer, Pred> orders=new ConcurrentHashMap<Integer, Pred>();
    // пустое место
    public static final Place emptyPlace=new Place(-1,-1);
    // Число листов
    public int listCount=0;
    // Список листов
    private final List<Plate> plates=new ArrayList<Plate>();
    // Размер списка
    public int getSize(){
        return plates.size();
    }

    /**
     * Конструктор
     * @param row ряд
     * @param column колонна
     */
    public Place(int row,int column){
       this.row=row;
       this.column=column;
//       Plate plate =new Plate("Gradex", "HX",new Plate.Size(20.0, 2000.0, 12000.0) , 2576, 1245678,
//               1, 5678, 5000.0,435768767);
//       Plate plate2 =new Plate("Gradex", "HX",new Plate.Size(20.0, 2000.0, 12000.0) , 2576, 1245678,
//               2, 5678, 5000.0,435768768);
//       addPlate(plate);
//       addPlate(plate2);
//       addPlate(new Plate("Grade2", "H2",new Plate.Size(20.0, 2000.0, 12000.0) , 2573, 1245671,
//               3, 5670, 5000.0,435768760));
//       addPlate(new Plate("Grade2", "H2",new Plate.Size(20.0, 2000.0, 12000.0) , 2573, 1245671,
//               4, 5670, 5000.0,435768761));
//       addPlate(new Plate("Grade2", "H2",new Plate.Size(20.0, 2000.0, 12000.0) , 2573, 1245671,
//               5, 56708, 5000.0,435768762));
//       addPlate(new Plate("Grade2", "H2",new Plate.Size(20.0, 2000.0, 12000.0) , 2573, 1245671,
//               6, 5670, 5000.0,435768763));
   }

    /**
     * Места идентичны если тот же ряд и колонна
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Place){
            Place place=(Place) obj;
            return ((place.column==column) && (place.row==row));
        }
        return false;
    }

    /**
     * Получение ряда
     * @return
     */
    public int getRow() {
        return row;
    }

    /**
     * Получение колонны
     * @return
     */
    public int getColumn() {
        return column;
    }

    /**
     * Добавление листа на место
     * @param plate лист
     */
    public void addPlate(Plate plate){
        plates.add(plate);
        int orderNum=plate.getOrder();
        int number=plate.getNumber();
        if(orders.containsKey(number)){
  	        Pred order=orders.get(number);
  	        order.add(plate);
        }
        else
        {
  	        Pred order=new Pred(number,orderNum);
  	            order.add(plate);
  	        orders.put(number, order);
        }
    }

    /**
     * Удаление листа
     * @param plate лист
     */
    public void removePlate(Plate plate)  {
        int orderNum=plate.getNumber();
        final Pred order=orders.get(orderNum);
        if(order!=null)
            order.delete(plate);
        if(order!=null && order.empty()){
            orders.remove(orderNum);
        }
        plates.remove(plate);
    }



    @Override
    public String toString() {

	return ""+column+"-"+row;
    }

    /**
     * Получение списка листов
     * @return
     */
  public List<Plate> getPlates(){
	  return plates;
  }

    /**
     * Очистка списка листов
     */
  public void clear(){
	  orders.clear();
	  plates.clear();
  }

    /**
     * Получение списка предъявок
     * @return
     */
   public List<Pred> getOrders(){
	 List<Pred> orList= new ArrayList<Pred>();
	 orList.addAll(orders.values());
	 return orList;
  }

    /**
     * Сравнение листов для сортировки
     * @param another
     * @return
     */
    @Override
    public int compareTo(Object another) {
	    if(another instanceof Place){
		    Place place = (Place)another;
		    int val=place.getColumn()*10+place.getRow();
		    return (val>(column*10+row)) ? -1:1;
	    }
	    return -1;
    }

    /**
     * Поиск листа на месте по заданному критерию поиска.
     * @param filt
     * @return
     */
  public List<Plate> getSearch(Predicate<Plate> filt){
      List<Plate> ret=new ArrayList<>();
      for(Plate plate: plates){
          if(filt.test(plate)) ret.add(plate);
      }
    return ret;
  }
}
