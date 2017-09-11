package tech;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Класс данных предъявки
 */
public class Pred extends PlateContainer implements Serializable{
	// список листов
  private final Set<Plate> plates=new HashSet<Plate>();
	// карта плавок
  private final Map<String,Heat> heats=new HashMap<String,Heat>();

    /**
     * Выбор предъявки для передачи и одновременно всех элементов внутри
     * @param selected выбор
     */
    @Override
    public void setSelected(boolean selected) {
	    super.setSelected(selected);
	    for(Heat heat: getHeats()){
		    heat.setSelected(selected);
	    }
    }

    /**
     *  Получение всех плавок
     * @return
     */
    public List< Heat> getHeats() {
        List<Heat> hList= new ArrayList<Heat>();
         hList.addAll(heats.values());
         return hList;
    }
    // Номер предъявки и заказа
    private int number,ordNumber;

    /**
     * Получение номера предъявки.
     * @return
     */
    public int getNumber() {
		return number;
	}

    /**
     * Конструктор
     * @param number номер заказа
     * @param ordNumber номер предъявки
     */
    public Pred(int number, int ordNumber){
  	
  	    this.number=number;
	    this.ordNumber=ordNumber;
    }

    /**
     * Номер заказа
     * @return
     */
    public int getOrdNumber(){
        return ordNumber;
    }
 @Override
public boolean equals(Object o) {
	if(o==null) return false;
	if(o instanceof Pred){
		Pred order=(Pred)o;
		return order.getNumber()==number;
	}
	return false;
}

    /**
     * Добавление листа
     * @param plate
     */
    public void add(Plate plate){
	 plates.add(plate);
	 String heatName=plate.getHeat();
	 if(heats.containsKey(heatName)){
		 Heat heat=heats.get(heatName);
		 heat.add(plate);
	 }
	 else{
		 Heat heat=new Heat(this, heatName,plate.getGrade());
		 heats.put(heatName, heat);
		 heat.add(plate);
	 }
 }

 @Override
    public String toString() {
        return ""+number;
    }

    /**
     * Удаление листа
     * @param plate
     */
    public void delete(Plate plate){
         String heatName=plate.getHeat();
         Heat heat=heats.get(heatName);
         heat.delete(plate);
         if(heat.empty()){
             heats.remove(heatName);
         }
         plates.remove(plate);
     }

    /**
     * Список листов в предъявке
     * @return
     */
    public List<Plate> getPlates() {
        List<Plate> ret=new ArrayList<Plate>();
        ret.addAll(plates);
        return ret;
    }
    @Override
    public int hashCode() {
        return number;
    }

    /**
     * Предъявка пустая
     * @return
     */
    public boolean empty(){
	return plates.size()==0;
}

    /**
     * Получение размера предъявки
     * @return
     */
    public int getSize(){
	return plates.size();
}

    /**
     * Суммарная масса листов предъявки
     * @return
     */
    public double getMass(){
        double mass=0.0;
       for(Plate plate: plates){
           mass+=plate.getMass();
       }
       return mass;
    }

    /**
     * Число листов в предъявке
     * @return
     */
    public int getCount(){
    return plates.size();
}
  }