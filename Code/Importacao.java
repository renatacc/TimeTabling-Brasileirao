
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Importacao {
	
	public static DadosConfig importaDadosConfig() throws IOException {
		JFileChooser file = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".dat", "dat");
	    file.setFileFilter(filter);
	    String linha;
	    int i, contLinha = 0;
	    DadosConfig dadosConfig = new DadosConfig();
		
	    file.setDialogType(JFileChooser.OPEN_DIALOG);
	    i = file.showOpenDialog(null);
	    if (i==1){
	    	System.err.println("Erro ao Abrir o arquivo de Dados");
	    } else {
	    	File arquivo = file.getSelectedFile();			//pega o nome do arquivo
			Reader reader = new FileReader(arquivo);		//abre o arquivo para leitura
			//************************************************************************
			BufferedReader lerArq = new BufferedReader(reader);
			linha = lerArq.readLine();	
			
			while (linha != null) {							// enquanto existir linhas a serem lidas
				if(!linha.substring(0, 1).equals("#")) {
					contLinha++;							//incrementa linha apenas se a linha não for um comentario
					switch (contLinha) {					//o arquivo de dados contem exatamente 3 linhas validas com quantos comentarios quiser
						case 1:
								//pega o numero de turnos
								i = pegaPosicaoVirgula(linha);
								dadosConfig.setNumTurnos(Integer.parseInt(linha.substring(0,i)));
								linha = linha.substring(i+1, linha.length());
								//pega o numero de times
								i = pegaPosicaoVirgula(linha);
								dadosConfig.setNumTimes(Integer.parseInt(linha.substring(0,i)));
								linha = linha.substring(i+1, linha.length());
								//pega o numero de rodadas
								dadosConfig.setNumRodadas(Integer.parseInt(linha));
							break;
						case 2:
								dadosConfig.setCaminhoDistancias(linha);
							break;
						case 3:
								dadosConfig.setCaminhoTimes(linha);						
							break;
					}
					
				}
				
				linha = lerArq.readLine();
			}
			//************************************************************************
			reader.close();
	    }
		
		return dadosConfig;
	}
	
	public static Confronto[][] m2017(DadosConfig dados) throws IOException {
		JFileChooser file = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".tabela", "tabela");
	    file.setFileFilter(filter);
	    String linha;
	    int i = 0, j = 0, k;
	    Confronto[][] m = new Confronto[dados.getNumRodadas()][dados.getNumTimes()/2]; 
		
	    file.setDialogType(JFileChooser.OPEN_DIALOG);
	    k = file.showOpenDialog(null);
	    if (k==1){
	    	System.err.println("Erro ao Abrir o arquivo de Dados");
	    } else {
	    	File arquivo = file.getSelectedFile();			//pega o nome do arquivo
			Reader reader = new FileReader(arquivo);		//abre o arquivo para leitura
			//************************************************************************
			BufferedReader lerArq = new BufferedReader(reader);
			linha = lerArq.readLine();	
			
			while (linha != null) {							// enquanto existir linhas a serem lidas
				k = pegaPosicaoVirgula(linha);
				//System.out.println(linha);
				int time1 = (Integer.parseInt(linha.substring(0,k)));
				linha = linha.substring(k+1, linha.length());
				int time2 = (Integer.parseInt(linha));
				m[i][j] = new Confronto(true, time1, time2);
				//System.out.println(m[i][j]);
				if (j==((dados.getNumTimes()/2)-1)) {
					j=0;
					i++;
				} else {
					j++;
				}
				linha = lerArq.readLine();
			}
			//************************************************************************
			reader.close();
	    }
	    
	    return m;
	}
	
	public static List<Time> importaTimes(DadosConfig dados) throws IOException {
		List<Time> listaTimes = new ArrayList<Time>();
	    String linha;
	    int ind = 0;
	    
		Reader reader = new FileReader(dados.getCaminhoTimes());	// abre o arquivo para leitura
		//************************************************************************
		BufferedReader lerArq = new BufferedReader(reader);
		linha = lerArq.readLine();	
		
		while (linha != null) {										// enquanto existir linhas a serem lidas
			if (!linha.substring(0, 1).equals("#")) {
				listaTimes.add(new Time(ind, linha));				// insere o time na lista
				
				ind++;												// incrementa o indice
			}
			
			linha = lerArq.readLine();
		}
		//************************************************************************
		reader.close();
		
		return listaTimes;
	}
	
	public static DistanciaTimes[][] importaDistanciaTimes(DadosConfig dados) throws IOException {
		DistanciaTimes[][] matriz = new DistanciaTimes[dados.getNumTimes()][dados.getNumTimes()];
		DistanciaTimes tempDist;
		String linha;
	    int i = 0;
	    
		Reader reader = new FileReader(dados.getCaminhoDistancias());	// abre o arquivo para leitura
		//************************************************************************
		BufferedReader lerArq = new BufferedReader(reader);
		linha = lerArq.readLine();	
		
		while (linha != null) {											// enquanto existir linhas a serem lidas
			if (!linha.substring(0, 1).equals("#")) {
				tempDist = new DistanciaTimes();
				//pega o time 1
				i = pegaPosicaoVirgula(linha);
				tempDist.setTime1(Integer.parseInt(linha.substring(0,i)));
				linha = linha.substring(i+1, linha.length());
				//pega o time 2
				i = pegaPosicaoVirgula(linha);
				tempDist.setTime2(Integer.parseInt(linha.substring(0,i)));
				linha = linha.substring(i+1, linha.length());
				//pega a distancia
				i = pegaPosicaoVirgula(linha);
				tempDist.setDistancia(Integer.parseInt(linha.substring(0,i)));
				linha = linha.substring(i+1, linha.length());
				//verifica se é estadual
				if (linha.equals("0")) {
					tempDist.setEstadual(false);
				} else {
					tempDist.setEstadual(true);
				}
				matriz[tempDist.getTime1()][tempDist.getTime2()] = new DistanciaTimes(tempDist.getTime1(), tempDist.getTime2(), tempDist.getDistancia(), tempDist.isEstadual());
				matriz[tempDist.getTime2()][tempDist.getTime1()] = new DistanciaTimes(tempDist.getTime2(), tempDist.getTime1(), tempDist.getDistancia(), tempDist.isEstadual());
				
			}
			
			linha = lerArq.readLine();
		}
		//************************************************************************
		reader.close();
		
		return matriz;
	}
	
	private static int pegaPosicaoVirgula(String linha) {
		return linha.indexOf(",");
	}
}
