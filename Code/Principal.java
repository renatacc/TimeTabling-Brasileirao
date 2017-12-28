import java.io.IOException;
import java.util.Scanner;

public class Principal {

	public static void main(String[] args) throws IOException {
		Scanner read = new Scanner(System.in);
		System.out.println("======= Menu =======");
		System.out.println("1. Gerar Tabela a partir de Entrada de Dados (*.dat)(*.dist)(*.tim)");
		System.out.println("2. Função objetiva de determinada tabela");
		System.out.println("Digite uma opção: ");
		int op = read.nextInt();
		read.close();
		System.out.println("Selecione o arquivo *.dat");
		DadosConfig dados = Importacao.importaDadosConfig();
		Campeonato campeonato = new Campeonato(dados);
		
		switch (op) {
		case 1:
				Confronto[][] solucao;
				long inicio, fim, dif;
				int cont = 1;
				double /*fO1,*/ fO2;
				
				inicio = System.currentTimeMillis();
				solucao = campeonato.simulatedAnnealingAdaptado(dados, 0.97, 10000000, 8000);
				solucao = campeonato.simulatedAnnealingBasico(dados, 0.95, 100000, 3000);
				
				// para executar n vezes até que uma solução que atenda todos os requisitos seja encontrada
				/*while (campeonato.errosRegra1(dados, solucao)+campeonato.errosRegra2(dados, solucao)+
						campeonato.errosRegra3(dados, solucao)+campeonato.errosRegra4(dados, solucao)+
						campeonato.errosRegra5(dados, solucao)+campeonato.errosRegra6(dados, solucao)+
						campeonato.errosRegra7(dados, solucao) != 0) {
					cont++;
					System.out.println("Rodando novamente " + cont);
					campeonato = new Campeonato(dados);
					solucao = campeonato.simulatedAnnealingAdaptado(dados, 0.97, 10000000, 8000);
					solucao = campeonato.simulatedAnnealingBasico(dados, 0.95, 10000, 3300);
				}*/
				
				fim = System.currentTimeMillis();
				campeonato.imprimeSolucaoCompleta(solucao, dados);
				
				//fO1 = campeonato.avaliaSolucao(solucao, campeonato.criaVetorRestricoes(true), dados);
				fO2 = campeonato.avaliaSolucao(solucao, campeonato.criaVetorRestricoes(false), dados);
				System.out.printf("Final: %f nErro1: %d nErro2: %d nErro3: %d nErro4: %d\n", 
						fO2, 
						campeonato.errosRegra1(dados, solucao),campeonato.errosRegra2(dados, solucao),
						campeonato.errosRegra3(dados, solucao),campeonato.errosRegra4(dados, solucao));
				System.out.println();
				System.out.printf("\t\tnErro5: %d nErro6: %d nErro7: %d nErro8: %d nErro9: %d\n", 
						campeonato.errosRegra5(dados, solucao),campeonato.errosRegra6(dados, solucao),
						campeonato.errosRegra7(dados, solucao),campeonato.errosRegra8(dados, solucao),
						campeonato.errosRegra9(dados, solucao));
				
				dif = (fim - inicio);
				System.out.printf("Executou em " + cont + " Tentativas / Tempo de execução: %02d minutos  e %02d segundo\n", dif/60000, dif%60000/1000);
			break;
		case 2:
				System.out.println("Selecione o arquivo *.tabela");
				Confronto[][] m2017 = Importacao.m2017(dados);
				m2017 = campeonato.geraReturno(dados, m2017);
				//fO1 = campeonato.avaliaSolucao(m2017, campeonato.criaVetorRestricoes(true), dados);
				fO2 = campeonato.avaliaSolucao(m2017, campeonato.criaVetorRestricoes(false), dados);
	
				System.out.printf("Função Objetiva: %f nErro1: %d nErro2: %d nErro3: %d nErro4: %d\n", 
						fO2, 
						campeonato.errosRegra1(dados, m2017),campeonato.errosRegra2(dados, m2017),
						campeonato.errosRegra3(dados, m2017),campeonato.errosRegra4(dados, m2017));
				System.out.printf("\t\t\t\t\t\tnErro5: %d nErro6: %d nErro7: %d nErro8: %d nErro9: %d\n",
						campeonato.errosRegra5(dados, m2017),campeonato.errosRegra6(dados, m2017),
						campeonato.errosRegra7(dados, m2017),campeonato.errosRegra8(dados, m2017),
						campeonato.errosRegra9(dados, m2017));
			break;
		default:
			break;
		}
	}	
}
