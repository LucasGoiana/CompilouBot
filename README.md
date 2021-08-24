
<h1 align="center"> ChatBotTelegram </a>  </h1>
 <p align="center"> ChatBot criado para o modulo "JAVA PLATFORM" - FIAP - Grupo 1 <br> Bot responsável por consultar Clima Tempo, Filmes e Simular a compra de um celular</p>

###  Integrantes do Grupo<Br>
 341417 - LUCAS GOIANA MALICIA<br>
 340887 - VINICIUS BEZERRA LIMA<br>
 342125 - JACKSON DOS SANTOS ROQUE DA SILVA<Br>
 340657 - ALEXANDRE AKIRA ENJIU<br>
 340710 - GUILHERME CRISTIANO DA SILVA COSTA<br>
 341447 - LARISSA ROCHA FERREIRA<Br>

# Como utilizar o backend

### Pré-requisitos

 - Java 11
 - Maven
 
 <p> Clone ou Extraia o projeto em um diretório de sua preferência:</p>
 
    "diretório de sua preferencia"
    git clone <projeto>
    
ou

	"diretório de sua preferencia"
	unzip file.zip

    
### Configuração
Altere os valores  do properties em `src/config/application.properties` e cadastre as chaves necessárias para o funcionamento das API's (MovieDB, Watson e OpenWeather)


	api.telegram.bot.token = <seu_token_telegram>
	api.moviedb.api_key = <seu_token_moviedb>
	api.watson.api_key = <seu_token_watson>
	api.watson.assistant_id = <seu_id_assistant>
	api.weather.appid = <seu_token_weather>
	


### Build
Para buildar o projeto com o Maven, executar o comando abaixo:

    mvn clean install

### Run
Após o passo de build, entre na pasta target/ e execute o comando abaixo para executar o telegramBot:

    java -jar ChatBotTelegram-1.0-SNAPSHOT.jar


# Como utilizar o TelegramBot

Após a etapa de run, procure no telegram o seguinte bot **@compiloubot**.
Você pode iniciar a conversa com o bot com as seguintes mensagens:


 - Oi
 - Ola
 - /start
 
 O bot então, irá apresentar os seguintes serviços:
 
 - 1 - Clima Tempo
 - 2 - Diálogo de Compra
 - 3 - Consultar Filmes

> e.g : Digite "1" para consultar o clima tempo

Você pode retornar as opções acima digitando em qualquer momento da interação com o bot as palavras "cancelar" ou "voltar".
 
 ## Clima Tempo
 Caso você selecione o **clima tempo**, o bot irá perguntar em qual cidade do Brasil você gostaria de consultar o clima.
 Basta apenas escrever o nome da cidade que ele te enviará o clima tempo da sua escolha.

## Diálogo de Compra

Caso você selecione **diálogo de compra**, o bot lhe apresentará algumas opções para que você simule a compra de um celular.

> e.g : Digite "Gostaria de comprar um celular" e responda os questionamentos que serão feitos pelo bot para simular a compra de um celular. 

## Consultar Filmes
Caso você selecione **consultar filmes**, o bot lhe apresentará algumas alternativas de consulta de filmes:
- Filmes em Cartaz
	- Retorna 5 opções de filmes com maiores notas e que estão atualmente em cartaz no brasil.
- Procurar Filme;
	- O bot lhe perguntará qual seria o nome do filme que você deseja pesquisar.
	- O bot lhe retornará até 5 opções de filmes que possuam referência ao nome pesquisado.
- Filmes Similares; 
	- O bot lhe perguntará qual seria o nome do filme que você gostaria que ele encontrasse os filmes similares.
	- O bot lhe retornará até 5 opções de filmes similares.
- Filmes Populares
	- Retorna 5 opções de filmes que estão com as maiores notas.
		
> e.g : Digite "Filmes em Cartaz" para receber 5 opções de filmes que estão em cartaz atualmente no brasil. 

    



