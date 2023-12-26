# OmniBot

OmniBot is a comprehensive bot that we use in Omniversal for a variety of functions. Using a bespoke bot avoids the common issue of duplicate commands when many different bots are used together, thus providing a simpler and more cohesive experience.

The bot is, in parts, intentionally coded to Omniversal, and is not hostable in other servers without modifications.

## Development guide

### Setup

OmniBot uses Bun instead of Node.js, which can be installed from https://bun.sh. After installing Bun, run the following commands:

```sh
git clone https://github.com/OmniversalDiscord/omnibot
cd omnibot
bun install
```

Create a new testing bot on Discord via the Developer Portal, then create a new `.env` file in the root directory with the following entry:

```dotenv
DISCORD_TOKEN=<your bot token>
```

Make sure the testing bot has all the permissions it needs, including `applications.commands`

Then you can run the bot with either `npm run dev` or `npm run dev:watch`, the latter of which will reload the bot when you make changes to the code.

### Configuration

All config files are stored in `config`. `development.yml` and `production.yml` are used when the respective `NODE_ENV` is set (or `development` if none is set), and `default.yml` by both. `custom-enviroment-variables.yml` is used to map environment variables to config values.

### Creating handlers

There are two types of handlers in OmniBot, **commands** and **message handlers**. Commands will be triggered whenever its associated slash command is run, while all message handlers will be triggered when any message is sent.

Commands are defined in `src/handlers/commands` and message handlers are stored in `src/handlers/messages`.

### Adding a new message handler

Create a new file called `<handlerName>.ts` in `src/handlers/messages`, and export a default function which takes a `Message` object as its only argument. For example, a simple "ping" handler could be defined as such:

```ts
// src/handlers/messages/ping.ts

import { Message } from 'discord.js';

export default async function ping(message: Message) {
  if (message.content === 'ping') {
    await message.channel.send('Pong!');
  }
}
```

### Adding a new command

Commands are more involved than message handlers, but have a similar principle. Create a new file called `<commandName>.ts` in `src/handlers/commands`. All commands **must** export a default function which receives a `Context` object as its only parameter, and a `register` function which optionally receives a `RegistrationContext` parameter, and returns a `SlashCommandBuilder` object. The `register` function can optionally be marked as `async`. Subcommands are not currently supported, but will be once a subcommand is needed. 

For example, the same "ping" handler from before could be defined as a command as such:

```ts
// src/handlers/commands/ping.ts

import { SlashCommandBuilder } from "discord.js";
import { CommandBuilder, Context } from "../../framework/types.ts";

// It's recommanded to annotate with CommandBuilder type
// to avoid using unsupported SlashCommandBuilder methods
export const register = (): CommandBuilder => 
  new SlashCommandBuilder()
    .setName("ping")
    .setDescription("Replies with pong");

export default async function ping({ interaction }: Context) {
  await interaction.reply("Pong!");
}
```

#### Command guards

Optionally, a command can export a `guard` function, which runs before the main function and allows for pre-execution checks and data loading. A guard either returns `guardError("message")` or `guardOk()`. `guardOk` can also optionally take data, which can then be passed into the main function. For example, let's augment the "ping" command by taking a user argument which the bot will ping, but fails if the user specified is Coffee Crew.

```ts
// src/handlers/commands/ping.ts
import {SlashCommandBuilder} from "discord.js";
import {
  CommandBuilder,
  Context,
  GuardData,
  guardError,
  guardOk,
} from "../../framework/types.ts";

export const register = (): CommandBuilder =>
  new SlashCommandBuilder()
    .setName("ping")
    .setDescription("Pings a user")
    .addUserOption((option) =>
      option.setName("user")
        .setDescription("The user to ping")
        .setRequired(true)
      );

// Guard function can also optionally be async
export function guard({ interaction }: Context) {
  const member = interaction.options.getMember("user", true);
  
  if (member.roles.cache.some((r) => r.name === "Coffee Crew")) {
    return guardError("You can't ping Coffee Crew! 😠");
  }

  // Wrapping the data in an object is recommended,
  // though unnecessary here
  return guardOk({ toPing: member });
}

// Our command now also pulls fromGuard from context, which contains the data
// returned by the guard function
export default async function ping({ fromGuard, interaction }: Context) {
  const { toPing } = fromGuard as GuardData<typeof guard>; // GuardData is a type helper,
                                                           // that extracts the data type 
                                                           // from the guard function
  await interaction.reply(`${toPing} get pinged!!`);
}
```

For a more complex example, check out `src/handlers/commands/resize.ts`, whick takes a `RegistrationContext` parameter and uses a guard.

### Logging and config

The logger and config can both be imported if required. OmniBot uses `pico` for logging and `config` for config management.

```ts
import {logger} from "../../logger.ts";
import config from "config";
import { Snowflake } from "discord.js";

// Log a message
logger.info("Hello world!");

// Load a config value
const guildId = config.get<Snowflake>("discord.guildId");
```