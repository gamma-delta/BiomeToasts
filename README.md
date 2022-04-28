# Template Mod

for Forge Minecraft Modding, yahoo

## To Set Up

1. Import the project and let Gradle do its thing
2. Swap the comments on the mappings so it uses Parchment and `genIntellijRuns`
3. Change the mod ID and name in the `mods.toml`
4. Move the `TemplateMod` file to where it needs to go

### To Get CI Set Up And Appease My Patrons

1. In Jenkins, create a new "Multibranch Pipeline"
2. Add a Branch Source:
    - Repo View Creds
    - Github repo URL
    - Build strategies: named branches, exact name, `main`
3. Add (the Discord `git-log` channel url) and `https://ci.blamejared.com/github-webhook/` as webhooks in Github