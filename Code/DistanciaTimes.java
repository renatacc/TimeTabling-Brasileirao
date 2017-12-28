

public class DistanciaTimes {
	private int time1;
	private int time2;
	private int distancia;
	private boolean estadual;
	
	public DistanciaTimes(int time1, int time2, int distancia, boolean estadual) {
		this.time1     = time1;
		this.time2     = time2;
		this.distancia = distancia;
		this.estadual  = estadual;
	}

	public DistanciaTimes() {
		
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

	public int getDistancia() {
		return distancia;
	}

	public void setDistancia(int distancia) {
		this.distancia = distancia;
	}

	public boolean isEstadual() {
		return estadual;
	}

	public void setEstadual(boolean estadual) {
		this.estadual = estadual;
	}

	@Override
	public String toString() {
		return "DistanciaTimes [time1=" + time1 + ", time2=" + time2 + ", distancia=" + distancia + ", estadual=" + estadual + "]";
	}
	
}
