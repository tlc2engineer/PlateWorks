package tech;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Класс данных плавки.
 */
public class Heat extends PlateContainer implements Serializable{
	// Предъявка
	private final Pred order;
    // Имя плавки
	private final String  heat;
    // Марка стали
	private final String grade;
    // Список листов
	private final Set<Plate> plates=new HashSet<Plate>();

    /**
     * Получение плавки
     * @return
     */
	public String getHeat() {
		return heat;
	}

    /**
     * Получение марки стали
     * @return
     */
	public String getGrade() { return grade;}

    /**
     * Выбор плавки для передачи. Одновременно выбираются и все листы внутри.
     * @param selected
     */
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		for(Plate plate: getPlates()){
			plate.setSelected(selected);
		}
	}

	@Override
	public String toString() {
		return heat;
	}

	@Override
	public int hashCode() {
		return heat.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Heat other = (Heat) obj;
		return other.getHeat().equals(heat) && other.getGrade().equals(grade) && other.order.getNumber()==order.getNumber();
	}

    /**
     * Получение списка листов
     * @return
     */
	public List<Plate> getPlates() {
		 List<Plate> lst = new ArrayList<Plate>();
		lst.addAll(plates);
		return lst;
	}

    /**
     * Конструктор
     * @param order предъявка
     * @param heat плавка
     * @param grade марка стали
     */
	Heat(Pred order, String heat,String grade){
		this.order = order;
		this.heat=heat;
		this.grade=grade;
	}

    /**
     * Добавить лист
     * @param plate
     */
	public void add(Plate plate){
		 plates.add(plate);
	 }

    /**
     * Удалить лист
     * @param plate
     */
	 public void delete(Plate plate){
		 plates.remove(plate);
	 }

    /**
     * Плавка пустая
     * @return
     */
	 public boolean empty(){
			return plates.size()==0;
		}

    /**
     * Получение суммарной массы листов
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
     * Получение количества листов
     * @return
     */
	public int getCount(){
		return plates.size();
	}
	
}