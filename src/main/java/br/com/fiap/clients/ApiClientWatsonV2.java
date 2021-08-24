package br.com.fiap.clients;

import br.com.fiap.config.PropertiesLoaderImpl;
import com.google.gson.internal.LinkedTreeMap;
import com.ibm.cloud.sdk.core.http.HttpConfigOptions;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  Lucas Goiana
 * <a href='https://cloud.ibm.com/apidocs/assistant/assistant-v2'>Link Documentação</a>
 */

public class ApiClientWatsonV2 
{

	// Variável que armazena o id da sessão do watsonsession_id_watson
	String session_id_Watson = "";

	// Context é a variavel que armazena o contexto atual,
	// utilizado para controlar a Máquina de Estado junto ao watson ibm
	MessageContextStateless context;

	// Armazena o primeiro contexto, caso o usuário decida voltar ao menu inicial em algum momento.
	MessageContextStateless first_context;

	//Retorno da autenticação
	Assistant auth = authWatson();

	// Conta o turnCount de um filme similar
	Long turnContNameMovieSimilar = null;

	// Conta o turnCount de um filme pesquisado por nome
	Long turnContNameMovie = null;

	// Conta o turnCount de uma cidade
	Long turnContNameCity = null;


	/**
	 * Metódo responsavel por autenticar no Watson ibm.
	 * @return Retorna a autenticação feita no Watson.
	 */
	public Assistant authWatson() 
	{
		
		IamAuthenticator authenticator = new IamAuthenticator(PropertiesLoaderImpl.getValue("api.watson.api_key"));
		Assistant assistant = new Assistant(PropertiesLoaderImpl.getValue("api.watson.version"), authenticator);
		assistant.setServiceUrl(PropertiesLoaderImpl.getValue("api.watson.url"));
		
		return assistant;
	
	}

	/**
	 * Crie uma nova sessão. Uma sessão é usada para enviar entrada do usuário para uma habilidade e receber respostas.
	 * Também mantém o estado da conversa.
	 * Uma sessão persiste até ser excluída ou até atingir o tempo limite devido à inatividade.
	 * @return
	 */
	public String createSession() {
		
		CreateSessionOptions options = new CreateSessionOptions.Builder(PropertiesLoaderImpl.getValue("api.watson.assistant_id")).build();
		SessionResponse response = auth.createSession(options).execute().getResult();
		return response.getSessionId();
		
	}

	/**
	 * Metódo para desativar o ssl permitindo assim autenticações "inseguras",
	 * ou seja, sem necessidade da criação de um certificado SSL.
	 */
	public void desactiveSSL() 
	{		
		HttpConfigOptions configOptions = new HttpConfigOptions.Builder().disableSslVerification(true).build();
		auth.configureClient(configOptions);
	}

	/**
	 * Método utilizado para enviar, verificar e receber as mensagem do watson
	 * @param mensagem Mensagem digitada pelo usuário
	 * @return Mensagem devolvida pelo watson
	 * @throws IOException
	 */
	public String getWatsonV2(String mensagem) throws IOException{
		
		//Desativando verificação SSL
		desactiveSSL();

		//Criando uma sessão no watson
		session_id_Watson = createSession();	

		MessageInputStateless input = new MessageInputStateless.Builder().messageType("text").text(mensagem).build();
		MessageStatelessOptions options = new MessageStatelessOptions.Builder().assistantId(PropertiesLoaderImpl.getValue("api.watson.assistant_id")).input(input).context(context).build();
		MessageResponseStateless response = auth.messageStateless(options).execute().getResult();

		//Armazena o contexto atual
		context = response.getContext();

		//Armazamento do First context
		if(response.getOutput().getIntents().contains(new RuntimeIntent.Builder().intent("escolhaOpcoes").confidence(1.0).build())){
			first_context = response.getContext();
		}

		//Transfere o usuário para o menu inicial
		if(mensagem.equalsIgnoreCase("cancelar") || mensagem.equalsIgnoreCase("voltar")) {
			context = first_context;
			
			return "Para continuar digite uma das opções:\n" + 
					"1 - Clima Tempo;\n" + 
					"2 - Diálogo de Compra;\n" + 
					"3 - Consultar Filmes;";
			
		}
			
		List<RuntimeResponseGeneric> runtimes = response.getOutput().getGeneric();
			
		for(RuntimeResponseGeneric runtime : runtimes) {

			//Armazena o turncount do filme similar pelo última mensagem devolvida do watson
			if( response.getOutput().getGeneric().get(0).text().equals("Digite o nome do filme similar:")){
				turnContNameMovieSimilar = response.getContext().global().system().turnCount() +1;
			}

			//Armazena o turncount da cidade pelo última mensagem devolvida do watson
			if( response.getOutput().getGeneric().get(0).text().equals("Digite o nome da Cidade:")){
				
				turnContNameCity = response.getContext().global().system().turnCount() +1;
				
			}

			//Armazena o turncount pelo nome do filme pelo última mensagem devolvida do watson
			if( response.getOutput().getGeneric().get(0).text().equals("Digite o nome do Filme:")){
			
				turnContNameMovie = response.getContext().global().system().turnCount() +1;
			}

			//Se o turnContNameCity está preenchido retorna a string clima usada para válidar no main.java
			if(turnContNameCity == response.getContext().global().system().turnCount()){
				
				turnContNameCity = null;
				return "clima";
				
			}

			//Se o turnContNameMovieSimilar está preenchido retorna a string similar usada para válidar no main.java
			if(turnContNameMovieSimilar == response.getContext().global().system().turnCount()){
				turnContNameMovieSimilar = null;
				return "similar";					
			}

			//Se o turnContNameMovie está preenchido retorna a string similar usada para válidar no main.java
			if(turnContNameMovie == response.getContext().global().system().turnCount()){
				turnContNameMovie = null;
				return "procurar";					
			}
			
			 JSONObject obj = new JSONObject(runtime.toString());
				
			 if(obj.get("response_type").equals("suggestion")) {
				
				 List<DialogSuggestion> suggestions = runtime.suggestions();
				 
				 for(DialogSuggestion suggestion : suggestions) {
						
					 if(suggestion.getLabel().equalsIgnoreCase("Compra de produto")) {
						ArrayList suggestionArray = (ArrayList) suggestion.getOutput().get("generic");
						LinkedTreeMap<String, String> singleSuggestion = (LinkedTreeMap<String, String>) suggestionArray.get(0);
						 return  singleSuggestion.get("text");
					 }
					 
				 }
				 			
			 }

			 return  (String) obj.get("text");
			
		}

		context = first_context;
		return "Para continuar digite uma das opções:\n" +
				"1 - Clima Tempo;\n" +
				"2 - Diálogo de Compra;\n" +
				"3 - Consultar Filmes;";

	}

	/**
	 * Exclui uma sessão explicitamente antes que ela expire.
	 * @param session Variável responsável por armazenar o id da sessão atual.
	 */
	public void deleteSession(String session) {
		DeleteSessionOptions options = new DeleteSessionOptions.Builder(PropertiesLoaderImpl.getValue("api.watson.assistant_id"), session).build();
		auth.deleteSession(options).execute();
	}
}
