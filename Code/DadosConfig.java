

public class DadosConfig {
	private int numTurnos;
	private int numTimes;
	private int numRodadas;
	private String caminhoDistancias;
	private String caminhoTimes;
	
	public DadosConfig() {
		
	}
	
	public DadosConfig(int numTurnos, int numTimes, int numRodadas, String caminhoDistancias, String caminhoTimes) {
		this.numTurnos         = numTurnos;
		this.numTimes          = numTimes;
		this.numRodadas 	   = numRodadas;
		this.caminhoDistancias = caminhoDistancias;
		this.caminhoTimes 	   = caminhoTimes;
	}

	public int getNumTurnos() {
		return numTurnos;
	}

	public void setNumTurnos(int numTurnos) {
		this.numTurnos = numTurnos;
	}

	public int getNumTimes() {
		return numTimes;
	}

	public void setNumTimes(int numTimes) {
		this.numTimes = numTimes;
	}

	public int getNumRodadas() {
		return numRodadas;
	}

	public void setNumRodadas(int numRodadas) {
		this.numRodadas = numRodadas;
	}

	public String getCaminhoDistancias() {
		return caminhoDistancias;
	}

	public void setCaminhoDistancias(String caminhoDistancias) {
		this.caminhoDistancias = caminhoDistancias;
	}

	public String getCaminhoTimes() {
		return caminhoTimes;
	}

	public void setCaminhoTimes(String caminhoTimes) {
		this.caminhoTimes = caminhoTimes;
	}
}
