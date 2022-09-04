# Wordle-Bot
This Discord Wordle Bot (made utilizing the Java Discord API- JDA) is ran on a PebbleHost server and allows for Wordle players to see all sorts of statistics about their gameplay, is connected to a **MySQL Database** that stores player data, and provides a fun interactive experience for the players through its connection to a jokes API and by providing hints via **web scraping**. Players interact with the bot via commands.

show the read me channel

## Database Connection
When the main function is ran the "MySQLConnection" class establishes a connection to the database and reads the data into a local **Hash Map** where the key is the players name (always unique) and the values are the associated statistics. I made sure to use prepared statements when querying this data as this is a good practice to avoid **SQL injection vulnerabilities**. Each time a player sends their Wordle, or a new member joins, the database connection is established once again and is updated accordingly.

show screenshot of database

## Web Scraping
When a user sends the command for a hint, certain websites are "scraped" for clues to the Wordle word for the day. The player receives this hint in a private chat, so that other players do not have the clues spoiled to them.

show hint example

## Other
When a player joins the Discord server, the bot **automatically** assigns them a "player role" that enables them to interact with the bot. When a player sends their Wordle the text is filtered via **regular expressions** and then the computations of the statistics take place. After this, the bot sends a message that shows a funny quip to let the user know that their Wordle successfully went through.

show screenshot of quip response

The commands below trigger the bot to send messages in an **embedded format**.

show embedded commands
