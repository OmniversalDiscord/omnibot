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
  fromGuard: any;
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

export interface GuardOk<T> {
  type: "success";
  data: T;
}

export function guardOk<T>(data: T): GuardOk<T> {
  return { type: "success", data: data };
}

export function guardError(message: string): GuardError {
  return { type: "error", message };
}

export type GuardResult<T> = GuardOk<T> | GuardError;

// Utility type to unwrap a type from a Promise
type UnwrapPromise<T> = T extends Promise<infer U> ? U : T;

// Utility type to extract the 'data' type from GuardResult
type ExtractGuardData<T> = T extends GuardOk<infer U> ? U : never;

// GuardData type that extracts the type T from a Guard function
export type GuardData<T extends (context: any) => any> = ExtractGuardData<
  UnwrapPromise<ReturnType<T>>
>;

export type CommandGuard = (context: Context) => Promise<GuardResult<any>>;
