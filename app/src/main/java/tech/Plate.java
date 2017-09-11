package tech;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Класс данных листа.
 */
public class Plate extends PlateContainer implements Serializable {
    static final long serialVersionUID =1121L;
    private String grade="";//Марка стали
    private String heat="";//Плавка
    private Size size;//Размер
    private int number;//Номер предъявки
    private int ID;// ID листа
    private int krat;// Крат
    private int order;// Номер заказа

    /**
     * Получение массы листа
     * @return
     */
	public double getMass() {
		return mass;
	}

	private double mass; //Масса
    private int uniq; //Уникальный номер листа

    /**
     * Получение толщины
     * @return
     */
   public double getThick(){
       return size.h;
   }

    /**
     * Получение номера
     * @return
     */
    public int getNumber() {
		return number;
	}

    /**
     * Получение номера предъявки
     * @return
     */
	public int getOrder() {
		return order;
	}

    /**
     * Конструктор
     * @param grade марка
     * @param heat плавка
     * @param size размер
     * @param number номер
     * @param id id
     * @param krat крат
     * @param order предъявка
     * @param mass масса
     * @param uniq уникальный номер
     */
	public Plate(String grade, String heat, Size size, int number, int id,
			int krat, int order, double mass,int uniq) {
		super();
		this.grade = grade;
		this.heat = heat;
		this.size = size;
		this.number = number;
		this.ID = id;
		this.krat = krat;
		this.order = order;
		this.mass = mass;
		this.uniq=uniq;
	}

	@Override
    public boolean equals(Object obj) {
        if(obj instanceof Plate){
           return (ID ==((Plate)obj).ID && krat==((Plate)obj).krat);
        }
        return false;
    }

    @Override
    public int hashCode() {
       int hash =   ID *10+krat;
        return hash;
    }

    /**
     * @return получение марки
     */
    public String getGrade() {
        return grade;
    }

    /**
     * @return номер
     */
    public int getNum() {
        return number;
    }

    /**
     * Класс размеров листа
     */
   public static  class Size implements Serializable{
	   Double h,w,l;
    	public Size(Double h,Double w,Double l){
    		this.h=h;
    		this.w=w;
    		this.l=l;
    	}
		@Override
		public boolean equals(Object o) {
			if(o instanceof Size){
				Size size=(Size) o;
				return h==size.h&&w==size.w&&l==size.l;
			}
			return false;
		}
		@Override
		public String toString() {
			return ""+l.intValue()+"x"+w.intValue()+"x"+h.intValue();
		}
    }

    /**
     * Получение листа из json объекта
     * @param obj
     * @return
     */
    public static Plate parse(JSONObject obj){
    	if(obj.isNull("idslab")) return null;
    	try {
			String ids=obj.getString("idslab");
			String[] idkrat=ids.split("-");
			int id=Integer.valueOf(idkrat[0]);
			int krat=Integer.valueOf(idkrat[1]);
			String grade=obj.getString("grade");
			int order=Integer.valueOf(obj.getString("orders"));
			String heat=obj.getString("heat");
			String size_string=obj.getString("size");
			String[] h_l_w=obj.getString("size").split("x");
			int number=Integer.valueOf(obj.getString("number"));
			Double h=Double.valueOf(h_l_w[0]);
			Double l=Double.valueOf(h_l_w[2]);
			Double w=Double.valueOf(h_l_w[1]);
			double mass=Double.valueOf(obj.getString("m_pl"));
			int uniq=Integer.valueOf(obj.getString("iniqueid"));
			return new Plate(grade,heat,new Size(h,w,l),number,id,krat,order,mass,uniq);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return null;
    }

	@Override
	public String toString() {
		return ""+ ID +"-"+krat;
	}

    /**
     * Получение плавкт
     * @return
     */
	public String getHeat() {
		return heat;
	}

    /**
     * Получение размеров
     * @return
     */
   public String getSize(){
	   return size.toString();
   }

    /**
     * Получение информации
     * @return
     */
 public  String getFullInfO(){
	return "Номер: "+ ID +"-"+krat+"\nСорт:"+grade+"\n"+"Размер:"+size.toString()+"\n"+"Предъявка:"+number+"\nПлавка:"+heat;
	   
   }

    /**
     * Уникальный номер
     * @return
     */
    public int getUniq() {
	return uniq;
}

    /**
     * Получение листа
     * @return
     */
    @Override
public List<Plate> getPlates() {
	// TODO Auto-generated method stub
	ArrayList<Plate> rt = new ArrayList<Plate>();
	rt.add(this);
	return rt;
}
 
}
