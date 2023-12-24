import {
  ChatInputCommandInteraction,
  REST,
  type SlashCommandBuilder,
} from "discord.js";

export interface ContextData {
  // Empty for now
}

export interface Context {
  interaction: ChatInputCommandInteraction;
  data: ContextData;
}

export interface RegistrationContext {
  restClient: REST;
  data: ContextData;
}

export type Command = (context: Context) => Promise<void>;

export type CommandBuilder = Omit<
  SlashCommandBuilder,
  "addSubcommand" | "addSubcommandGroup"
>;

export interface GuardError {
  type: "error";
  message: string;
}

export interface GuardOk {
  type: "success";
}

export function guardOk(): GuardOk {
  return { type: "success" };
}

export function guardError(message: string): GuardError {
  return { type: "error", message };
}

export type GuardResult = GuardOk | GuardError;

export type CommandGuard = (context: Context) => Promise<GuardResult>;
