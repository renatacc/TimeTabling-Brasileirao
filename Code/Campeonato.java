
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Campeonato {
	private DistanciaTimes[][] matrizDistancias;
	private Confronto[][] matrizConfrontos;
	private List<Confronto> listaConfrontos;
	private List<Time> listaTimes;
	private final double tempCongelamento = 0.00000000001;
	private final double tempReaquecimento = 0.0000001;
	private final double[] vetorPesos = {100000000, 1000000000, 100000000, 1000000000, 2000000000, 10000000, 10000000, 1, 100};
	
	public Campeonato(DadosConfig dados) throws IOException {
		this.matrizDistancias = Importacao.importaDistanciaTimes(dados);
		this.listaTimes 	  = Importacao.importaTimes(dados);		
		this.listaConfrontos  = geraListaConfrontos(dados);
		this.matrizConfrontos = geraConfrontosIniciais(dados);		
	}

	public List<Confronto> geraListaConfrontos(DadosConfig dados) {
		// Gera uma lista com todos os confrontos possiveis entre os times, sem repetições
		// O mando de campo é definido aleatoriamente
		List<Confronto> listaConfrontos = new ArrayList<Confronto>();
		int aux;

		for (int i = 0; i < dados.getNumTimes(); i++) {
			for (int j = i+1; j < dados.getNumTimes(); j++) {
				aux = (int) (Math.random()*2);
				if (aux == 0) {		// true mando do time 1 
					listaConfrontos.add(new Confronto(true, listaTimes.get(i).getId(), listaTimes.get(j).getId()));
				} else {			// false mando do time 2
					listaConfrontos.add(new Confronto(false, listaTimes.get(i).getId(), listaTimes.get(j).getId()));
				}
			}
		}
		
		return listaConfrontos;
	}
	
	public Confronto[][] geraConfrontosIniciais(DadosConfig dados) {
		// Gera uma tabela inicial com confrontos selecionados aleatoriamente 
		// As linhas da matriz correspondem a cada rodada do primeiro turno, já as colunas correspondem aos jogos das rodadas
		// A matriz será criada apenas para o primeiro turno, pois o segundo turno (returno) é um espelho do primeiro, mudando apenas os mandos de campo
		Confronto[][] confrontosIniciais = new Confronto[dados.getNumRodadas()][dados.getNumTimes()/2]; 
		int i = 0, 														// representa a rodada
			j = 0, 														// representa o jogo
			numSort;
		
		while (listaConfrontos.size() != 0) {							// enquanto a lista não for vazia
			numSort = (int) (Math.random()*listaConfrontos.size());		// sorteia um confronto aleatoriamente da lista
			confrontosIniciais[i][j] = listaConfrontos.get(numSort);	// insere o confronto na matriz
			listaConfrontos.remove(numSort);							// remove este confronto da lista
			
			if (i < (dados.getNumRodadas()/2)-1) {
				i++;
			} else {
				j++;
				i = 0;
			}
		}
		confrontosIniciais = geraReturno(dados, confrontosIniciais);
		return confrontosIniciais;
	}

	public Confronto[][] simulatedAnnealingAdaptado(DadosConfig dados, double razaoResfriamento, double temperaturaInicial, double iterMax) {
		Confronto[][] melhorSolucao = copiaConfrontos(dados, matrizConfrontos);	// faz uma copia da lista de confrontos original s*
		Confronto[][] solucaoVizinho;											// solução gerada para modificar vizinhos com swapp s'
		int iter = 0;															// numero de iteracoes na temperatura T
		int sortI, sortJ, numSortI, numSortJ;									// indices para sortear vizinhos
		double t = temperaturaInicial;											// temperatura inicial
		double x;																// fator aleatorio de avaliação se aceita ou não solução
		double delta;
		Confronto tempConfronto;
		EMovimento movimento;
		Boolean[] vetorRestricoes = criaVetorRestricoes(true);
		
		while (t > tempCongelamento) {
			//System.out.println("Temperatura: " + t);
			while (iter < iterMax) {
				iter++;
				movimento = sorteiaMovimento();									// sorteia um dos movimentos, troca de mando ou troca de jogo
				// sorteia vizinho para gerar nova solucao
				sortI = (int) (Math.random()*(dados.getNumRodadas()/2));		// sorteia linha na matriz
				sortJ = (int) (Math.random()*(dados.getNumTimes()/2));			// sorteia coluna na matriz
				solucaoVizinho = copiaConfrontos(dados, matrizConfrontos);		// faz uma copia para modificar vizinho s'
				//
				//imprimeSolucao(matrizConfrontos, dados);
				if (movimento == EMovimento.TROCA_MANDO) {						// troca mando de campo
					//System.out.println("Troca de Mando "+(sortI+1)+" "+sortJ);
					//System.out.println("Antes: "+solucaoVizinho[sortI][sortJ]);
					solucaoVizinho[sortI][sortJ].setMando(!solucaoVizinho[sortI][sortJ].getMando());
					//System.out.println("Depois: "+solucaoVizinho[sortI][sortJ]);
				} else {								
					numSortI = (int) (Math.random()*(dados.getNumRodadas()/2));	// sorteia outra linha na matriz para realizar uma troca
					numSortJ = (int) (Math.random()*(dados.getNumTimes()/2));	// sorteia outra linha na matriz para realizar uma troca	// troca jogo
					//System.out.println("Troca de Jogo "+(sortI+1)+" "+sortJ+" por "+(numSortI+1)+" "+numSortJ);
					// faz troca entre os jogos sorteados
					tempConfronto 				   	   = solucaoVizinho[sortI][sortJ];					
					solucaoVizinho[sortI][sortJ]  	   = solucaoVizinho[numSortI][numSortJ];
					solucaoVizinho[numSortI][numSortJ] = tempConfronto;
				}
				/******************************/
				delta = avaliaSolucao(solucaoVizinho, vetorRestricoes, dados) - avaliaSolucao(matrizConfrontos, vetorRestricoes, dados);
				
				if (delta < 0) {												// se delta é negativo então é uma solução melhor que a anterior
					matrizConfrontos = solucaoVizinho;
					if (avaliaSolucao(solucaoVizinho, vetorRestricoes, dados) <= avaliaSolucao(melhorSolucao, vetorRestricoes, dados)) {
						melhorSolucao = solucaoVizinho;							// se é melhor que a melhor solução altera a matriz
						System.out.printf("Nova Melhor Solução Encontrada: %f nErro1: %d nErro2: %d nErro3: %d nErro4: %d\n", 
											avaliaSolucao(solucaoVizinho, vetorRestricoes, dados), 
											errosRegra1(dados, solucaoVizinho),errosRegra2(dados, solucaoVizinho),
											errosRegra3(dados, solucaoVizinho),errosRegra4(dados, solucaoVizinho));
						if (avaliaSolucao(solucaoVizinho, vetorRestricoes, dados) == 0) {
							System.out.printf("Final: %f nErro1: %d nErro2: %d nErro3: %d nErro4: %d\n", 
								avaliaSolucao(solucaoVizinho, vetorRestricoes, dados), 
								errosRegra1(dados, solucaoVizinho),errosRegra2(dados, solucaoVizinho),
								errosRegra3(dados, solucaoVizinho),errosRegra4(dados, solucaoVizinho));
							matrizConfrontos = melhorSolucao;					
							
							return matrizConfrontos;							// retorna a melhor solução
						}
					}
				} else {														// tenta vasculhar novas areas em busca de um novo melhor local, 
					x = (double) (Math.random()*2);								// mesmo que este vizinho seje ruim
					if (x < Math.exp(-delta/t)) {
						matrizConfrontos = solucaoVizinho;
					}
				}
			}
			
			t = razaoResfriamento * t;											// reduz a temperatura
			iter = 0;															// reinicia as iterações
			/******************************/
			if (t < tempReaquecimento) {
				if (!verificaFactibilidade(melhorSolucao, vetorRestricoes, dados)) {			// se ainda não foi encontrada uma boa solução, reaquece
					t 		= 0.2 * (0.1 * temperaturaInicial);
					iterMax = 0.2 * iterMax; 
					//t = temperaturaInicial;
				} else {
					t = tempCongelamento-1; 									// congela o sistema para terminar a execução
				}
			}
		}
		
		matrizConfrontos = melhorSolucao;										// retorna a melhor solução
		
		return matrizConfrontos;
	}
	
	public Confronto[][] simulatedAnnealingBasico(DadosConfig dados, double razaoResfriamento, double temperatura, double iterMax) {
		matrizConfrontos = geraReturno(dados, matrizConfrontos);
		Confronto[][] melhorSolucao = copiaConfrontos(dados, matrizConfrontos);	// faz uma copia da lista de confrontos original s*
		Confronto[][] solucaoVizinho;											// solução gerada para modificar vizinhos com swapp s'
		int sortI, sortJ;														// indices para sortear vizinhos
		int iter = 0;															// numero de iteracoes na temperatura T
		double t = temperatura;													// temperatura inicialator aleatorio de avaliação se aceita ou não solução
		double x;																// fator aleatorio de avaliação se aceita ou não solução
		double delta;
		Boolean[] vetorRestricoes = criaVetorRestricoes(false);
		
		while (t > 0.000001) {
			while (iter < iterMax) {
				iter++;
				sortI = (int) (Math.random()*(dados.getNumRodadas()/2));		// sorteia linha na matriz
				sortJ = (int) (Math.random()*(dados.getNumTimes()/2));			// sorteia coluna na matriz
				solucaoVizinho = copiaConfrontos(dados, matrizConfrontos);		// faz uma copia para modificar vizinho s'
				solucaoVizinho[sortI][sortJ].setMando(!solucaoVizinho[sortI][sortJ].getMando());	// troca mando de campo
				solucaoVizinho = geraReturno(dados, solucaoVizinho);			// gera o returno do vizinho
				delta = avaliaSolucao(solucaoVizinho, vetorRestricoes, dados) - avaliaSolucao(matrizConfrontos, vetorRestricoes, dados);
				
				if (delta < 0) {												// se delta é negativo então é uma solução melhor que a anterior
					matrizConfrontos = solucaoVizinho;
					if (avaliaSolucao(solucaoVizinho, vetorRestricoes, dados) < avaliaSolucao(melhorSolucao, vetorRestricoes, dados)) {
						melhorSolucao = solucaoVizinho;							// se é melhor que a melhor solução altera a matriz
						System.out.printf("Nova Melhor Solução Encontrada: %f nErro1: %d nErro2: %d nErro3: %d nErro4: %d\n"
								+ "\t\t\t\t\t\tnErro5: %d nErro6: %d nErro7: %d nErro8: %d nErro9: %d\n", 
											avaliaSolucao(solucaoVizinho, vetorRestricoes, dados), 
											errosRegra1(dados, solucaoVizinho),errosRegra2(dados, solucaoVizinho),
											errosRegra3(dados, solucaoVizinho),errosRegra4(dados, solucaoVizinho),
											errosRegra5(dados, solucaoVizinho),errosRegra6(dados, solucaoVizinho),
											errosRegra7(dados, solucaoVizinho),errosRegra8(dados, solucaoVizinho),
											errosRegra9(dados, solucaoVizinho));
					}
				} else {														// tenta vasculhar novas areas em busca de um novo melhor local, 
					x = (double) (Math.random()*2);								// mesmo que este vizinho seje ruim
					if (x < Math.exp(-delta/t)) {
						matrizConfrontos = solucaoVizinho;
					}
				}
			}
			t = razaoResfriamento * t;											// reduz a temperatura
			iter = 0;															// reinicia as iterações
		}

		matrizConfrontos = melhorSolucao;										// retorna a melhor solução
		
		return matrizConfrontos;
	}
	
	public Confronto[][] geraReturno(DadosConfig dados, Confronto[][] matriz) {
		
		for (int i = 0; i < dados.getNumRodadas()/2; i++) {		// rodadas
			for (int j = 0; j < dados.getNumTimes()/2; j++) {	// jogos da rodada
				matriz[i+(dados.getNumRodadas()/2)][j] = new Confronto(!matriz[i][j].getMando(), 
																		matriz[i][j].getTime1(), 
																		matriz[i][j].getTime2());
			}
		}
		
		return matriz;
	}
	
	public void imprimeSolucao(Confronto[][] s, DadosConfig dados) {
		for (int i = 0; i < dados.getNumRodadas()/2; i++) {
			System.out.print("Rodada " + (i+1) + "\t");
			for (int j = 0; j < dados.getNumTimes()/2; j++) {
				System.out.print(s[i][j] + "\t");
				//System.out.print(s[i][j].mandoToString() + listaTimes.get(s[i][j].getTime1()).getNome() + " x " + listaTimes.get(s[i][j].getTime2()).getNome() + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void imprimeSolucaoCompleta(Confronto[][] s, DadosConfig dados) {
		for (int i = 0; i < dados.getNumRodadas(); i++) {
			System.out.print("Rodada " + (i+1) + "\t");
			for (int j = 0; j < dados.getNumTimes()/2; j++) {
				System.out.print(s[i][j].toStringCompleto(listaTimes) + "\t\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	private boolean verificaFactibilidade(Confronto[][] solucao, Boolean[] restricoes, DadosConfig dados) {
		if (restricoes[0]) {
			if (errosRegra1(dados, solucao) != 0) {
				return false;
			}
		}
		if (restricoes[1]) {
			if (errosRegra2(dados, solucao) != 0) {
				return false;
			}
		}
		if (restricoes[2]) {
			if (errosRegra3(dados, solucao) != 0) {
				return false;
			}
		}
		if (restricoes[3]) {
			if (errosRegra4(dados, solucao) != 0) {
				return false;
			}
		}
		
		return true;
	}

	public int errosRegra1(DadosConfig dados, Confronto[][] matriz) {
		// Um time não pode jogar mais de uma vezes na mesma rodada
		int erro = 0, cont;
		
		for (int k = 0; k < listaTimes.size(); k++) {
			for (int i = 0; i < dados.getNumRodadas()/2; i++) {		// rodadas
				cont = 0;
				for (int j = 0; j < dados.getNumTimes()/2; j++) {	// jogos da rodada
					if (matriz[i][j].getTime1() == listaTimes.get(k).getId() || 
							matriz[i][j].getTime2() == listaTimes.get(k).getId()) {
						cont++;
					}
				}
				if (cont > 1) {
					erro+=cont;
					erro--;
				}
			}
		}
		
		return erro;
	}
	
	public int errosRegra2(DadosConfig dados, Confronto[][] matriz) {
		// Nas duas primeiras rodadas, do turno e returno, que cada time participar, um jogo deve ser realizado em sua sede e outro fora. 
		// Por exemplo, se na primeira rodada do turno o confronto de um time for dentro de casa, então na segunda rodada o confronto 
		// deste time deve ser fora de casa
		int erro = 0;
		
		for (int k = 0; k < listaTimes.size(); k++) {	
			// se o mando das duas primeiras rodadas forem iguais, então contabiliza um erro
			if (isMandante(dados, listaTimes.get(k).getId(), 0, matriz) == isMandante(dados, listaTimes.get(k).getId(), 1, matriz)) {
				erro++;
			}
		}
		
		return erro;
	}
	
	public int errosRegra3(DadosConfig dados, Confronto[][] matriz) {
		// As duas últimas rodadas do turno, que cada time participar, devem a configuração contraria de seus dois primeiros confrontos. 
		// Exemplificando, se os dois primeiros confrontos de um time (turno) foram jogar fora de casa e depois em casa, respectivamente,
		// então os dois últimos confrontos desse time (turno) devem ser, respectivamente, jogar em casa e depois fora de casa.
		// A mesma regra vale para a fase de returno;
		int erro = 0;
		
		for (int k = 0; k < listaTimes.size(); k++) {	
			// se o mando da primeira rodada for diferente do mando da ultima então contabiliza erro
			if (isMandante(dados, listaTimes.get(k).getId(), 0, matriz) != isMandante(dados, listaTimes.get(k).getId(), dados.getNumRodadas()/2-1, matriz)) {
				erro++;
			}
			// se o mando da segunda rodada for diferente do mando da penultima então contabiliza erro
			if (isMandante(dados, listaTimes.get(k).getId(), 1, matriz) != isMandante(dados, listaTimes.get(k).getId(), dados.getNumRodadas()/2-2, matriz)) {
				erro++;
			}
		}
		
		return erro;
	}
	
	public int errosRegra4(DadosConfig dados, Confronto[][] matriz) {
		// Não pode haver jogos entre clubes do mesmo estado na última rodada;
		int erro = 0;
		
		for (int j = 0; j < dados.getNumTimes()/2; j++) {
			// se ambos os times de um jogo da ultima rodada forem do mesmo estado contabiliza um erro
			if (isMesmoEstado(dados, matriz[dados.getNumRodadas()/2-1][j].getTime1(), 
									 matriz[dados.getNumRodadas()/2-1][j].getTime2())) {
				erro++;
			}
		}
		
		return erro;
	}
	
	public int errosRegra5(DadosConfig dados, Confronto[][] matriz) {
		// O numero de jogos realizados fora de casa deve ser igual ao numero de jogos em casa
		int contMandante = 0, contVisitante = 0, erro = 0, aux;
		
		for (int j = 0; j < dados.getNumTimes(); j++) {
			contMandante = 0;
			contVisitante = 0;
			for (int i = 0; i < dados.getNumRodadas()/2; i++) {	// rodadas
				if (isMandante(dados, listaTimes.get(j).getId(), i, matriz)) {	
					contMandante++;							// contabiliza todos os jogos em que o time é mandante
				} else {
					contVisitante++;						// contabiliza todos os jogos em que o time é visitante
				}
			}
			aux = Math.abs(contMandante - contVisitante);	// se a diferença entre os jogos de mandante e visitante for maior que 1 ocorreu erro
			if (aux > 1) {
				erro+=aux-1;
			}
		}
		
		return erro;
	}
	
	public int errosRegra6(DadosConfig dados, Confronto[][] matriz) {
		// Evitar que um time jogue mais de duas vezes consecutivas em casa
		int erro = 0, cont = 0;
		
		for (int j = 0; j < dados.getNumTimes(); j++) {
			cont = 0;
			for (int i = 0; i < dados.getNumRodadas()/2; i++) {		// rodadas
				if (isMandante(dados, listaTimes.get(j).getId(), i, matriz)) {
					cont++;				// contabiliza que o time foi mandante
				} else {
					if (cont > 2) { 	// se vai jogar fora de casa, verifica quantas vezes ele foi mandante
						erro+=cont - 2; // contabiliza o numero de jogos em casa menos 2
					}
					cont = 0;
				}
			}
		}
		
		return erro;
	}
	
	public int errosRegra7(DadosConfig dados, Confronto[][] matriz) {
		// Evitar que um time jogue mais de duas vezes consecutivas fora de casa
		int erro = 0, cont = 0;
		
		for (int j = 0; j < dados.getNumTimes(); j++) {
			cont = 0;
			for (int i = 0; i < dados.getNumRodadas()/2; i++) {		// rodadas
				if (!isMandante(dados, listaTimes.get(j).getId(), i, matriz)) {
					cont++;				// contabiliza que o time foi visitante
				} else {
					if (cont > 2) { 	// se vai jogar em casa, verifica quantas vezes ele foi visitante
						erro+=cont - 2; // contabiliza o numero de jogos fora de casa menos 2
					}
					cont = 0;
				}
			}
		}
		
		return erro;
	}

	public int errosRegra8(DadosConfig dados, Confronto[][] matriz) {
		// minimizar distancia total percorida pelos times
		int erro = 0, lugarAnterior, lugarNovo, distancia;
		
		for (int j = 0; j < dados.getNumTimes(); j++) {
			lugarAnterior = listaTimes.get(j).getId();
			distancia = matrizDistancias[lugarAnterior][lugarAnterior].getDistancia();
			
			for (int i = 0; i < dados.getNumRodadas(); i++) {		// rodadas
				if (isMandante(dados, listaTimes.get(j).getId(), i, matriz)) {
					distancia+= matrizDistancias[lugarAnterior][listaTimes.get(j).getId()].getDistancia();
					lugarAnterior = listaTimes.get(j).getId();
				} else {
					lugarNovo = retornaAdversario(dados, listaTimes.get(j).getId(), i, matriz);
					distancia+= matrizDistancias[lugarAnterior][lugarNovo].getDistancia();
					lugarAnterior = lugarNovo;
				}
			}
			
			distancia+= matrizDistancias[lugarAnterior][listaTimes.get(j).getId()].getDistancia();
			erro+=distancia;
		}
		
		return erro;
	}

	public int errosRegra9(DadosConfig dados, Confronto[][] matriz) {
		// diferença entre o time que se deslocou mais e o que se deslocou menos
		int lugarAnterior, lugarNovo, distancia;
		int menorDistancia = +9999999;
		int maiorDistancia = -9999999;
		
		for (int j = 0; j < dados.getNumTimes(); j++) {
			lugarAnterior = listaTimes.get(j).getId();
			distancia = matrizDistancias[lugarAnterior][lugarAnterior].getDistancia();
			
			for (int i = 0; i < dados.getNumRodadas(); i++) {		// rodadas
				if (isMandante(dados, listaTimes.get(j).getId(), i, matriz)) {
					distancia+= matrizDistancias[lugarAnterior][listaTimes.get(j).getId()].getDistancia();
					lugarAnterior = listaTimes.get(j).getId();
				} else {
					lugarNovo = retornaAdversario(dados, listaTimes.get(j).getId(), i, matriz);
					distancia+= matrizDistancias[lugarAnterior][lugarNovo].getDistancia();
					lugarAnterior = lugarNovo;
				}
			}
			
			distancia+= matrizDistancias[lugarAnterior][listaTimes.get(j).getId()].getDistancia();
			
			if (distancia > maiorDistancia) {
				maiorDistancia = distancia;
			}
			if (distancia < menorDistancia) {
				menorDistancia = distancia;
			}
		}
		
		return maiorDistancia - menorDistancia;
	}
	
	public double avaliaSolucao(Confronto[][] matriz, Boolean[] restricoes, DadosConfig dados) {
		double result = 0;
		
		if (restricoes[0]) {
			result+= vetorPesos[0] * errosRegra1(dados, matriz) * 2;
		}
		if (restricoes[1]) {
			result+= vetorPesos[1] * errosRegra2(dados, matriz) * 2;
		}
		if (restricoes[2]) {
			result+= vetorPesos[2] * errosRegra3(dados, matriz) * 2;
		}
		if (restricoes[3]) {
			result+= vetorPesos[3] * errosRegra4(dados, matriz) * 2;
		}
		if (restricoes[4]) {
			result+= vetorPesos[4] * errosRegra5(dados, matriz) * 2;
		}
		if (restricoes[5]) {
			result+= vetorPesos[5] * errosRegra6(dados, matriz) * 2;
		}
		if (restricoes[6]) {
			result+= vetorPesos[6] * errosRegra7(dados, matriz) * 2;
		}
		if (restricoes[7]) {
			result+= vetorPesos[7] * errosRegra8(dados, matriz);
		}
		if (restricoes[8]) {
			result+= vetorPesos[8] * errosRegra9(dados, matriz);
		}
		
		return result;
	}

	private boolean isMandante(DadosConfig dados, int time, int indRodada, Confronto[][] matriz) {
		for (int j = 0; j < dados.getNumTimes()/2; j++) {	// analiza os jogos da rodada
			if (matriz[indRodada][j].getTime1() == time) {	// se encontrou o time no time1 verifica se é seu mando
				if (matriz[indRodada][j].getMando()) {		// true se o time 1 for mandante
					return true;
				} else {												// false se o time 1 nao for mandante
					return false;
				}
			}
			
			if (matriz[indRodada][j].getTime2() == time) {	// se encontrou o time no time2 verifica se é seu mando
				if (!matriz[indRodada][j].getMando()) {		// true se o time 2 for mandante
					return true;
				} else {												// false se o time 2 nao for mandante
					return false;
				}
			}
		}
		
		return false;
	}
	
	private int retornaAdversario(DadosConfig dados, int indTime, int indRodada, Confronto[][] matriz) {
		for (int j = 0; j < dados.getNumTimes()/2; j++) {	// jogos da rodada
			if (matriz[indRodada][j].getTime1() == indTime ) {
				return matriz[indRodada][j].getTime2();
			}
			if (matriz[indRodada][j].getTime2() == indTime) {
				return matriz[indRodada][j].getTime1();
			}
		}
		
		return 0;
	}
	
	private boolean isMesmoEstado(DadosConfig dados, int time1, int time2) {
		if (matrizDistancias[time1][time2].isEstadual()) {
			return true;
		}
		
		return false;
	}
	
	private Confronto[][] copiaConfrontos(DadosConfig dados, Confronto[][] matriz) {
		// retorna uma copia da matriz de confrontos, para que possa ser feita alterações nos vizinhos 
		Confronto[][] newConfrontos = new Confronto[dados.getNumRodadas()][dados.getNumTimes()/2];
		
		for (int i = 0; i < dados.getNumRodadas(); i++) {
			for (int j = 0; j < dados.getNumTimes()/2; j++) {
				newConfrontos[i][j] = new Confronto(matriz[i][j].getMando(), matriz[i][j].getTime1(), matriz[i][j].getTime2());
			}
		}
		
		return newConfrontos;
	}

	private EMovimento sorteiaMovimento() {
		int aux = (int) (Math.random()*2);
		if (aux == 0) {
			return EMovimento.TROCA_JOGO;
		} else {
			return EMovimento.TROCA_MANDO;
		}
	}

	public Boolean[] criaVetorRestricoes(boolean SA_Adaptado) {
		if (SA_Adaptado) {
			Boolean [] vetor = {true,true,true,true,false,false,false,false,false};
			return vetor;
		} else {
			Boolean [] vetor = {false,true,true,false,true,true,true,true,true};
			return vetor;
		}
	}
}
