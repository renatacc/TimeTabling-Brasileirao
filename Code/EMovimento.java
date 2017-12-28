

public enum EMovimento {
	TROCA_MANDO("Troca Mando"), TROCA_JOGO("Troca Jogo");
	
	private String nome;
	
	private EMovimento(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

}
