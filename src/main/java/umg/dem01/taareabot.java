package umg.dem01;


import java.util.ArrayList;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class taareabot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "Tilinzanbot";
    }

    @Override
    public String getBotToken() {
        return "7356487611:AAFV-QYv_mYaYdtXbLUfR3CMU1qkqNLLBV4";
    }


    private void sendWelcomeMessage(Long chatId) {
        String welcomeText = "Hola me llamo Epsilon que sea que haga por usted?\n\nPor ahora esto es lo que puedo hacer\n" +
                "1. /info   --Informacion del programador\n" +
                "2. /progra --Que pienso de la clase\n" +
                "3. /hola   --Te puedo saludar y decir la fecha actual\n" +
                "4. /cambio --Conversion de moneda\n" +
                "5. /grupo --Mensaje a tus Amigos";

        sendText(chatId, welcomeText);
    }



    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            String[] parts = messageText.split(" ", 2);

            System.out.println("User id: " + chatId + " Message: " + messageText);

            String userName = update.getMessage().getFrom().getFirstName();

            if (!userId.contains(chatId)) {
                userId.add(chatId);
                System.out.println("ID almacenado: " + chatId);
            }

            switch (parts[0]) {
                case "/start":
                    sendWelcomeMessage(chatId);
                    break;
                case "/info":
                    sendInfoMessage(chatId);
                    break;
                case "/progra":
                    sendPrograMessage(chatId);
                    break;
                case "/hola":
                    sendHolaMessage(chatId, userName);
                    break;
                case "/cambio":
                    if (parts.length == 2) {
                        sendCambioMessage(chatId, parts[1]);
                    } else {
                        sendText(chatId, "Por favor, ingrese la cantidad que desea convertir");
                    }
                    break;
                case "/grupo":
                    if (parts.length == 2) {
                        sendGrupalMessage(parts[1]);
                    } else {
                        sendText(chatId, "Ingrese por favor el mensaje que desea mandar, ejemplo /grupo Hola que tal!");
                    }
                    break;
            }
        }
    }




    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    private void sendInfoMessage(Long chatId) {
        String infoText = " Mario Fernando Cerna Najera " +
                " Carnet: 0905-23-5025 "+
                " Tel: 55969903 "+
                " Cuarto Semestre ";
        sendText(chatId, infoText);
    }


    private void sendPrograMessage(Long chatId) {
        String prograText = "------Comentario sobre la carrera-------"+
                "\n" +
                " Me gusta la programación, pero al mismo tiempo me da miedo ya que sabe que es el campo en el que tendré que trabajar en el futuro. Aun así, sé que depende de mí aprender lo más que pueda y seguir avanzando para alcanzar mis objetivos."+
                " Afortunadamente contamos con un ingeniero que nos está enseñando todo lo que sabe y hace que la clase sea más entretenida al compartir sus amplios conocimientos de manera amigable. Se esfuerza por enseñarnos lo que sabe, y lo hace de una forma que facilita nuestro aprendizaje. Él cumple con su parte al proporcionarnos información y compartiendo sus experiencias como programador, y  debo cumplir con la mía, aprendiendo y tratando de superarme al programar y a cometer errores en el proceso."+
                " De todas las clases que he tomado en los dos años que llevo en la universidad, actualmente es mi clase favorita ";

        sendText(chatId, prograText);
    }


    private void sendHolaMessage(Long chatId, String userName) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy");
        String currentDate = dateFormat.format(new Date());

        String holaText = "Hola que tal estas  " + userName + " la fecha de hoy es " + currentDate + " y ponte hacer algo gato baboso";

        sendText(chatId, holaText);
    }


    private void sendCambioMessage(Long chatId, String amountText) {
        try {
            double amount = Double.parseDouble(amountText);
            double rate = getExchangeRate("EUR", "GTQ");
            double result = amount * rate;

            String responseText = String.format(" CAMBIO \n\n%.2f euros son %.2f quetzales.", amount, result);
            sendText(chatId, responseText);
        } catch (NumberFormatException e) {
            sendText(chatId, "Ingresa un numero valido.");
        } catch (Exception e) {
            sendText(chatId, "No se pudo hacer la conversion ");
        }
    }


    private double getExchangeRate(String fromCurrency, String toCurrency) throws Exception {
        String apiKey = "4e32a4d142c57b6e64c3f6ce";  //
        String url = String.format("https://api.exchangerate-api.com/v4/latest/%s", fromCurrency);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonResponse = new JSONObject(response.body());
        return jsonResponse.getJSONObject("rates").getDouble(toCurrency);
    }

    private List<Long> userId = new ArrayList();
    private List<Long> friendsID = List.of(7219737695l,1262374416l,6709392176l,1533824724l);


    private void sendGrupalMessage(String message) {
        for (Long friendID : friendsID) {

            sendText(friendID, message);
        }
    }

}

