import { Client, Role, Snowflake } from "discord.js";
import { logger } from "../logger.ts";

class RoleSectionCache {
  private client!: Client;
  private guildId!: Snowflake;
  private sectionPattern!: RegExp;
  private roleSections: Map<string, Role[]> = new Map();

  register(client: Client, guildId: Snowflake, sectionPattern: RegExp) {
    this.client = client;
    this.guildId = guildId;
    this.sectionPattern = sectionPattern;

    // We don't need to bind on role creation because roleUpdate fires when the role is inevitably renamed
    client.once("ready", this.updateRoleSections.bind(this));
    client.on("roleUpdate", this.updateRoleSections.bind(this));
    client.on("roleDelete", this.updateRoleSections.bind(this));
  }

  updateRoleSections() {
    logger.debug("Updating role sections...");
    this.roleSections.clear();

    const roles = this.client.guilds.cache
      .get(this.guildId)
      ?.roles.cache.sort((a, b) => b.position - a.position)
      .values();

    if (roles === undefined) {
      logger.warn("Could not find guild or associated roles");
      return;
    }

    let currentSection = "other";
    let currentSectionRoles: Role[] = [];
    for (let role of roles) {
      // If the role name matches the section pattern, it defines a new section
      if (this.sectionPattern.test(role.name)) {
        this.roleSections.set(currentSection, currentSectionRoles);
        currentSection = role.name.match(this.sectionPattern)![1].toLowerCase();
        currentSectionRoles = [];
      } else {
        currentSectionRoles.push(role);
        logger.debug(`Added ${role.name} to ${currentSection}`);
      }
    }

    this.roleSections.set(currentSection, currentSectionRoles);
  }

  get(section: string) {
    if (this.roleSections.size === 0) {
      throw new Error("Role section cache was used before initialization");
    }
    return this.roleSections.get(section);
  }
}

export const roleSectionCache = new RoleSectionCache();
