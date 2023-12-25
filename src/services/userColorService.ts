import { GuildMember, Role } from "discord.js";

export class UserColorService {
  readonly member: GuildMember;
  readonly colors: Role[];
  private _currentColor: Role | undefined;

  get currentColor() {
    return this._currentColor;
  }

  public constructor(colors: Role[], member: GuildMember) {
    this.colors = colors;
    this.member = member;
    this._currentColor = this.member.roles.cache.find((role) =>
      this.colors.includes(role),
    );
  }

  public async clearColor() {
    if (this._currentColor) {
      await this.member.roles.remove(this._currentColor);
    }
  }

  public async setColor(colorId: string) {
    // Get the role object from the id
    const color = this.colors.find((role) => role.id === colorId);

    if (!color) {
      throw new Error(`Couldn't find a color with id ${colorId}`);
    }

    await this.clearColor();
    await this.member.roles.add(color);
    this._currentColor = color;
  }
}
