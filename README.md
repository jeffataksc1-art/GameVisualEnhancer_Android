# GameVisualEnhancer_Android

Projeto Android mínimo, sem bibliotecas de terceiros, preparado para gerar um APK pelo GitHub Actions.

## Alvo: POCO X6 Pro

- ABI configurada: `arm64-v8a`
- `minSdk 26`
- `targetSdk 35`
- Compile SDK 35
- Java 17
- Android Gradle Plugin 8.6.1 + Gradle 8.7

O POCO X6 Pro é usado como referência de dispositivo/arquitetura. O app não contém código específico do chipset e não promete aumento de FPS.

## O que o APK faz

O app fornece uma camada visual por sobreposição com controles de intensidade, contraste (perfil visual) e brilho. Como as APIs normais do Android não permitem aplicar uma ColorMatrix diretamente à superfície de outro jogo sem privilégios especiais, o APK usa uma camada translúcida de tonalidade/escurecimento que funciona sem root. É necessário conceder a permissão **Exibir sobre outros apps**.

> Importante: filtros de sobreposição alteram a aparência da tela; eles não aumentam o desempenho do jogo, não fazem overclock e não modificam arquivos do jogo.

## Gerar pelo GitHub Actions

1. Crie um repositório no GitHub.
2. Envie o conteúdo desta pasta para o repositório.
3. Abra **Actions → Build APK → Run workflow** (ou faça push em `main`/`master`).
4. Ao terminar, baixe o artefato `GameVisualEnhancer-debug-apk`.

O workflow instala Gradle 8.7 diretamente e não depende de um `gradle-wrapper.jar` no repositório.
