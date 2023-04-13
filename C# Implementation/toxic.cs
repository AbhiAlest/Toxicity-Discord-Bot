using Discord;
using Discord.WebSocket;
using Google.Cloud.Language.V1;
using System;
using System.Threading.Tasks;

namespace ToxicBot
{
    class Program
    {
        static async Task Main(string[] args)
        {
            var discordClient = new DiscordSocketClient();
            var languageClient = LanguageServiceClient.Create();
            var prefix = "!toxic_bot";

            discordClient.Log += Log;

            discordClient.MessageReceived += async (message) =>
            {
                if (!message.Author.IsBot && message.Content.StartsWith(prefix))
                {
                    var userMessage = message.Content.Substring(prefix.Length).Trim();

                    // perspective ai
                    var document = new Document
                    {
                        Content = userMessage,
                        Type = Document.Types.Type.PlainText
                    };
                    var response = await languageClient.AnalyzeSentimentAsync(new AnalyzeSentimentRequest
                    {
                        Document = document,
                        EncodingType = EncodingType.Utf8
                    });

                    // toxic %
                    var toxicityPercentage = Math.Round(response.DocumentSentiment.Score * 100, 2);

                    await message.Channel.SendMessageAsync($"The toxicity percentage of your message is {toxicityPercentage}%.");
                }
            };

            await discordClient.LoginAsync(TokenType.Bot, "DISCORD_BOT_TOKEN");
            await discordClient.StartAsync();

            await Task.Delay(-1);
        }

        private static Task Log(LogMessage message)
        {
            Console.WriteLine(message.ToString());
            return Task.CompletedTask;
        }
    }
}
