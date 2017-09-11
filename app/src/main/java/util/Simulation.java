package util;


/**
 * Класс используемый длдя симуляции
 */
public class Simulation {
public final static String[] grades={"2FBU","09Г2С","Steel3","K56","K60","A20","K100","Steel5","U","Ageev"};
public static final  int[] orderNums={2434,5463,6754,1512,4533,1111,8021,3265,9076,1001,4564,3324,33465,344532,6432,6743,555,4443,22355,3434};
public static String heats[]=new String[10];
static{
	for(int i=1;i<10;i++){
		heats[i]="Heat Sim "+(int)Math.random()*100;
	}
}

    /**
     * Получение случайного номера.
     * @param n
     * @return
     */
    static int genNum(int n){
        return 1+(int) (Math.random()*n);
    }

}
