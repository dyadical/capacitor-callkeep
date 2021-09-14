export interface CapCallKeepPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
