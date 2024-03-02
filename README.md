# Bitmex Bot

Welcome to Bitmex Bot - an automated trading bot for placing and closing orders on the exchange using the Fibonacci sequence.

## Description

Bitmex Bot is designed to automate the exchange trading process, allowing users to employ a strategy based on the Fibonacci sequence. The bot adapts to market changes, automatically closing outdated orders and placing new ones according to current market conditions.

The bot operates with the following parameters:

**Level** - the number of orders the bot will operate with, which is also the same quantity arranged in the Fibonacci sequence.

**Step** - the distance between the prices of the orders placed by the bot, multiplied by the number in the Fibonacci sequence.

**Coefficient** - the coefficient of the amount that the bot will place, also multiplied by the Fibonacci sequence. The higher the order amount, the lower the price at which the bot will attempt to buy a portion of Bitcoin. Conversely, for sell orders, the higher the order amount, the higher the price at which the bot will try to sell a previously acquired portion of Bitcoin.  

## Key Features

- **Automated Trading:** The bot automatically places and closes orders according to the selected Fibonacci sequence.
- **Parameter Configuration:** Users can configure trading strategy parameters.
- **Order History:** View the history of all past orders the bot has worked with.
- **Stop and Close:** Users can stop the bot, and upon completion, it offers the option to close any remaining unnecessary orders.

## Technologies

- **Java Spring Boot with Spring MVC:** The bot is developed using Java Spring Boot for reliability and efficiency, with the added benefit of Spring MVC for robust web application development.
- **PostgreSQL with JpaRepository:** PostgreSQL is employed for storing orders, utilizing JpaRepository for seamless interaction with the database.
- **HTML/CSS with Bootstrap and JavaScript:** The bot's web interface is crafted using HTML and CSS, enhanced with Bootstrap for responsive design, and incorporates JavaScript scripts for dynamic user interactions.


## Installation and Running Instructions

1. Sign up on the [Bitmex](https://testnet.bitmex.com/app/login) test platform.
2. Obtain login [credentials](https://testnet.bitmex.com/app/apiKeys). In the "Key Permissions" section, select "Order".
3. Visit the project's [website](https://bitmex-bot-2f34d5d4f1fb.herokuapp.com/)

## Usage Instructions

1. Log in using your ApiKey and ApiSecretKey.
2. Configure trading strategy parameters: Step, Coefficient and Level.
3. Click "Start" to initiate the bot.
4. Use the web interface to monitor order history and manage the bot.

## Contacts

- Email: dandonch24@gmail.com
- Telegram: [DanDonch](https://t.me/DanDonch)


