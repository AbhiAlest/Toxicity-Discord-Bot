import com.google.cloud.language.v1.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ToxicBot extends ListenerAdapter {
    private final String prefix = "!toxic_bot";
    private final LanguageServiceClient languageClient;
    private final JDA discord;

    public ToxicBot(String discordToken) {
        this.discord = new JDABuilder(discordToken).addEventListeners(this).build();
        this.languageClient = LanguageServiceClient.create();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.getMessage().getContentRaw().startsWith(prefix)) {
            String userMessage = event.getMessage().getContentRaw().substring(prefix.length()).trim();

            // perspective ai
            Document document = Document.newBuilder().setContent(userMessage).setType(Document.Type.PLAIN_TEXT).build();
            AnalyzeSentimentResponse response = languageClient.analyzeSentiment(AnalyzeSentimentRequest.newBuilder().setDocument(document).setEncodingType(EncodingType.UTF8).build());

            // toxic %
            double toxicityPercentage = Math.round(response.getDocumentSentiment().getScore() * 100 * 100.0) / 100.0;

            MessageChannel channel = event.getChannel();
            channel.sendMessage("The toxicity percentage of your message is " + toxicityPercentage + "%.").queue();
        }
    }

    public static void main(String[] args) {
        String discordToken = "DISCORD_BOT_TOKEN";
        new ToxicBot(discordToken);
    }
}
