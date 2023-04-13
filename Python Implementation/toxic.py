import discord
from google.cloud import language_v1 #will be using Google's Perspective AI

client = discord.Client()

@client.event
async def on_ready():
    print('Logged in through {0.user}'.format(client))

@client.event
async def on_message(message):
    if message.content.startswith('!toxic_bot'):
        user_message = message.content.split('!toxic_bot')[1].strip()

        client = language_v1.LanguageServiceClient()
        document = language_v1.Document(content=user_message, type_=language_v1.Document.Type.PLAIN_TEXT)
        response = client.analyze_sentiment(request={'document': document, 'features': {'extract_document_sentiment': True, 'extract_entity_sentiment': False, 'extract_syntax': False}})

        # toxic %
        toxicity_percentage = round(response.document_sentiment.score * 100, 2)

        await message.channel.send(f'The toxicity percentage of your message is {toxicity_percentage}%.')

client.run('DISCORD_BOT_TOKEN')
