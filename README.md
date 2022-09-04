# Wordle-Bot
This Discord Wordle Bot (made utilizing the Java Discord API- JDA) is ran on a PebbleHost server and allows for Wordle players to see all sorts of statistics about their gameplay, is connected to a **MySQL Database** that stores player data, and provides a fun interactive experience for the players through its connection to a jokes API and by providing hints via **web scraping**. Players interact with the bot via commands.

## Database Connection
When the main function is ran the "MySQLConnection" class establishes a connection to the database and reads the data into a local **Hash Map** where the key is the players name (always unique) and the values are the associated statistics. I made sure to use prepared statements when querying this data as this is a good practice to avoid SQL injection vulnerabilities. Each time a player sends their Wordle, or a new member joins, the database connection is established once again and is updated accordingly.

## API Use and Web Scraping
