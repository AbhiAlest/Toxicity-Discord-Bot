const Discord = require('discord.js');
const { LanguageServiceClient } = require('@google-cloud/language'); //Google's Perspective AI

const client = new Discord.Client();
const languageClient = new LanguageServiceClient();
const prefix = '!toxic_bot';

client.on('ready', () => {
  console.log(`Logged in as ${client.user.tag}!`);
});

client.on('message', async (message) => {
  if (message.content.startsWith(prefix)) {
    const userMessage = message.content.slice(prefix.length).trim();

    const document = {
      content: userMessage,
      type: 'PLAIN_TEXT',
    };
    const [result] = await languageClient.analyzeSentiment({
      document: document,
      encodingType: 'UTF8',
    });

    // toxic %
    const toxicityPercentage = (result.documentSentiment.score * 100).toFixed(2);

    message.channel.send(`The toxicity percentage of your message is ${toxicityPercentage}%.`);
  }
});

client.login('DISCORD_BOT_TOKEN');
