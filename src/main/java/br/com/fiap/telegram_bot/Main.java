package br.com.fiap.telegram_bot;

import br.com.fiap.clients.ApiClientMovieDb;
import br.com.fiap.clients.ApiClientWatsonV2;
import br.com.fiap.clients.ApiClientWeather;
import br.com.fiap.config.PropertiesLoaderImpl;
import br.com.fiap.domains.Movie;
import br.com.fiap.domains.Weather;
import br.com.fiap.errors.AuthenticationFailedError;
import br.com.fiap.errors.ClientRequestError;
import br.com.fiap.errors.MovieNotFoundError;
import br.com.fiap.errors.UnprocessableEntityError;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;


import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, MovieNotFoundError, AuthenticationFailedError, UnprocessableEntityError {
        System.out.println("TelegramBot em execucão...");
        String resposta = null;
       
        ApiClientMovieDb clientMovie = new ApiClientMovieDb();
        ApiClientWatsonV2 clientWatsonV2 = new ApiClientWatsonV2();

        // Criacao do objeto bot com as informacoes de acesso.
        TelegramBot bot = new TelegramBot(PropertiesLoaderImpl.getValue("api.telegram.bot.token"));

        // Objeto responsavel por receber as mensagens.
        GetUpdatesResponse updatesResponse;

        // Objeto responsavel por gerenciar o envio de respostas.
        SendResponse sendResponse;

        // Objeto responsavel por gerenciar o envio de acoes do chat.
        BaseResponse baseResponse;

        // Controle de off-set, isto e, a partir deste ID sera lido as mensagens
        // pendentes na fila.
        int m = 0;

        // Loop infinito pode ser alterado por algum timer de intervalo curto.
        while (true) {
            // Executa comando no Telegram para obter as mensagens pendentes a partir de um
            // off-set (limite inicial).
            updatesResponse = bot.execute(new GetUpdates().limit(100).offset(m));

            // Lista de mensagens.
            List<Update> updates = updatesResponse.updates();

            // Analise de cada acao da mensagem.
            for (Update update : updates) {

                // Atualizacao do off-set.
                m = update.updateId() + 1;

                System.out.println("Recebendo mensagem: " + update.message().text());

                String mensagem = update.message().text();

                //Enviar as requisição e armazena o response do Watson na váriavle resposta
                resposta = clientWatsonV2.getWatsonV2(update.message().text());

                System.out.println("Mensagem: " + mensagem);

                // Envio de "Escrevendo" antes de enviar a resposta.
                baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));

                try {
                    if (resposta.equalsIgnoreCase("clima")) {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), ApiClientWeather.getWeather(mensagem, new Weather())));
                        resposta = "Clima☝\n\nDigite voltar que te transfiro para o menu inicial";
                    }
                } catch (Exception error){
                    resposta = "ops, tivemos um problema ao processar sua solicitação do clima tempo... por favor, tente novamente mais tarde!";
                    error.printStackTrace();
                }

                try {

                    if (mensagem.equalsIgnoreCase("Filmes Populares")) {
                        clientMovie.getMoviePopular().forEach(movie -> {
                            botSendMovie(bot,update.message().chat().id(), movie);
                        });
                    }

                    if (mensagem.equalsIgnoreCase("Filmes em Cartaz")) {
                        clientMovie.getMovieNowPlaying().forEach(movie -> {
                            botSendMovie(bot,update.message().chat().id(), movie);
                        });
                    }

                    if (resposta.equals("similar")) {
                        clientMovie.getMovieSimilar(update.message().text()).forEach(movie -> {
                            botSendMovie(bot,update.message().chat().id(), movie);
                        });
                        resposta = "Filmes Similares acima ☝️\n Digite voltar que te transfiro para o menu inicial";
                    }

                    if (resposta.equals("procurar")) {
                        clientMovie.getMovieByName(update.message().text()).forEach(movie -> {
                            botSendMovie(bot,update.message().chat().id(), movie);
                        });
                        resposta = "Filmes ☝\n Digite voltar que te transfiro para o menu inicial";
                    }

                } catch (MovieNotFoundError movieNotFoundError) {
                    resposta = "ops, não consegui encontrar o filme que voce mencionou";
                    movieNotFoundError.printStackTrace();
                } catch (AuthenticationFailedError | ClientRequestError error) {
                    resposta = "ops, tivemos um problema interno... por favor, tente novamente mais tarde!";
                    error.printStackTrace();
                } catch (UnprocessableEntityError unprocessableEntityError) {
                    resposta = "ops, tivemos um problema ao processar sua solicitação... por favor, verifique o conteudo enviado!";
                    unprocessableEntityError.printStackTrace();
                } catch (Exception ex) {
                    resposta = "ops, tivemos um problema ao processar sua solicitação.. tente novamente mais tarde!";
                    ex.printStackTrace();
                }
                
                // Verificacao de acao de chat foi enviada com sucesso.
                System.out.println("Resposta de Chat Action Enviada? " + baseResponse.isOk());

                // Envio da mensagem de resposta.
                sendResponse = bot.execute(new SendMessage(update.message().chat().id(), resposta));
            }
        }

    }
    public static void botSendMovie(TelegramBot bot, Long chatId, Movie movie) {
    	bot.execute(new SendPhoto(chatId, PropertiesLoaderImpl.getValue("api.moviedb.url_image") + movie.getPoster()));
        bot.execute(new SendMessage(chatId, movie.showInfoMovie()));
    }
}
