# Snippets

## Release

1. Update version
   ```bash
   ./mvnw versions:set -DnewVersion=<newVersion>
   ```
2. Commit + Push
3. Build
   ```bash
   ./mvnw clean package
   ```
4. Upload assets
   ```bash
   ./mvnw -N github-release:release -Dpassword= -Dusername=<GitHubApiKey>
   ```
