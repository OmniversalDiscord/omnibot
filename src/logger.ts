import pino from "pino";
import config from "config";

export const logger = pino(config.get("logging") as pino.LoggerOptions);
