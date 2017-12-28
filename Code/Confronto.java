

import java.util.List;

public class Confronto {
	private boolean mando;	// true mando do time 1 // false mando do time 2
	private int time1;
	private int time2;
	
	public Confronto(boolean mando, int time1, int time2) {
		this.mando = mando;
		this.time1 = time1;
		this.time2 = time2;
	}
	
	public boolean getMando() {
		return mando;
	}

	public void setMando(boolean mando) {
		this.mando = mando;
	}

	public int getTime1() {
		return time1;
	}

	public void setTime1(int time1) {
		this.time1 = time1;
	}

	public int getTime2() {
		return time2;
	}

	public void setTime2(int time2) {
		this.time2 = time2;
	}
	
	@Override
	public String toString() {
		return mandoToString() + time1 + "x" + time2;
	}
	
	public String toStringCompleto(List<Time> times) {
		if (mando) {
			
			return times.get(time1).getNome() + " x " + times.get(time2).getNome(); 
		} else {
			
			return times.get(time2).getNome() + " x " + times.get(time1).getNome(); 
		}
	}
	
	public String mandoToString() {
		if (mando) {
			return "+";
		} else {
			return "-";
		}
	}

}
