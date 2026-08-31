# Guia do Jogador — Vanilla Outsider: Better Dogs (Melhores Cães - Português)

Bem-vindo ao guia oficial do **Vanilla Outsider: Better Dogs**. Este mod reformula completamente a inteligência artificial dos lobos e cães domesticados no Minecraft, concedendo personalidades expressivas, genética dinâmica de tamanhos, modo inteligente de guarda sentinela e interações sociais ricas.

---

## 🚀 1. Guia de Início Rápido (Quick Start)

1. **Instalação**:
   - Instale o Fabric Loader para a sua versão do Minecraft (`1.20.1`, `1.21.1`, `1.21.11`, `26.1.2`, `26.2` ou `26.3`).
   - Coloque `better-dogs-*.jar` (e `dasik-library-*.jar` para versões a partir da 26.x) na sua pasta `.minecraft/mods`.
2. **Domesticando um Lobo**:
   - Alimente um lobo selvagem com ossos como no jogo base. Logo após a domesticação, o cão receberá uma de três personalidades exclusivas: **Normal**, **Agressivo** ou **Pacifista**.
3. **Visualizando os Atributos do Cão**:
   - Caso tenha o mod **Jade / Waila** instalado, mire no seu cão para visualizar sua personalidade, escala de tamanho, petisco favorito secreto e status de endogamia.

---

## 🧠 2. Três Tipos de Personalidade (Personalities)

| Personalidade | Comportamento Principal | Ajustes de Atributos |
| :--- | :--- | :--- |
| **Normal** | Companheiro equilibrado e fiel. Patrulha perto do posto de guarda e segue o dono na distância padrão de 10 blocos. | Atributos padrão vanilla (chance de fuga com vida baixa: 50%). |
| **Agressivo (Aggressive)** | Protetor destemido. Detecta e ataca automaticamente monstros hostis a até 20 blocos e persegue até 50 blocos. Imune ao medo de tempestades. | **+15% de velocidade de corrida**, **-15% de dano**, **-10 HP** (chance de fuga: 10%). |
| **Pacifista (Pacifist)** | Sentinela atento e alarme vivo (mantém-se a até 6 blocos do dono). Evita combate direto. | **+20 HP de vida máxima**, **-10% de velocidade**, **+15% de dano de contra-ataque**, **+50% de repulsão** (chance de fuga: 100%). Concede aura de regeneração e resistência no posto de guarda. |

---

## 🍖 3. Mecânicas Interativas & Sobrevivência

- **Petisco Favorito Secreto & Correria (Zoomies)**: Cada cão tem um petisco favorito secreto gerado pelo seu UUID. Alimentá-lo cura totalmente sua vida e ativa uma alegre corrida desgovernada (*Zoomies*).
- **Carinho com Mãos Vazias (Petting)**: Agache-se e clique com o botão direito com a mão vazia no seu cão para fazer carinho, emitir corações e fortalecer o vínculo de amizade.
- **Ansiedade de Tempestade (Storm Anxiety)**: Durante tempestades com raios, cães normais e pacifistas tremem e choram de medo. Faça carinho ou alimente-os para acalmá-los (progresso «São e Salvo»).
- **Esquiva Inteligente de Perigos (Smart Hazard Safety)**: Cães percebem creepers ativos e fogem correndo a 1.5x de velocidade; também evitam saltar em penhascos fatais e lava.
- **Presentes Matinais (Morning Gifts)**: Ao receber cuidados e carinho contínuos, um cão que dormir ao lado da sua cama lhe trará presentes úteis pela manhã.
- **Comandos com Berrante de Cabra (Goat Horn Commands)**: Toque um berrante de cabra para chamar e coordenar toda a sua matilha em um raio de 64 blocos.

---

## 🧬 4. Genética & Endogamia (Genetics & Inbreeding)

- **Variação Natural de Tamanho**: Lobos selvagens surgem com escalas entre **0.70x (pequeno)** e **1.45x (gigante)**.
- **Herança Genética**: Cruzar cães de grande porte permite criar uma linhagem gigante (progresso «Dinastia Gigante»).
- **Filhotes Raquíticos por Endogamia (Inbreeding Runt)**: Cruzar parentes próximos pode gerar filhotes enfraquecidos. Alimente o filhote com uma **Maçã Dourada (Golden Apple)** para curar permanentemente o defeito genético.

---

## ⚙️ 5. Regras de Jogo Úteis (GameRules)

- `/gamerule betterdogs:bd_actionbar_feedback true/false`: Notificações na barra de ação ativadas/desativadas.
- `/gamerule betterdogs:bd_friendly_fire_protection true/false`: Ativa proteção contra fogo amigo para não bater nos seus cães sem agachar.
- `/gamerule betterdogs:bd_storm_anxiety true/false`: Ansiedade de tempestade ativada/desativada.
- `/gamerule betterdogs:bd_cliff_safety true/false`: Segurança em penhascos ativada/desativada.
