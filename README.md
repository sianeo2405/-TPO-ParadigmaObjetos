# RPG por Turnos: Oathbreaker - The Sunken Realm

## 📝 Descripción del Proyecto
Este proyecto consiste en el diseño e implementación de un **videojuego de rol (RPG) por turnos** desarrollado bajo el Paradigma de Orientado a Objetos. Inspirado en la profundidad estratégica de *Dungeons & Dragons* y la estética de combate de *Expedition 33*, el sistema se centra en combates tácticos sucesivos sin exploración libre del mapa.

El núcleo del juego permite controlar una *party* de entre 2 y 4 héroes con clases especializadas que deben enfrentarse a diversas amenazas en un sistema de progresión por niveles.

---

## 🛠️ Requerimientos Técnicos (Fase A)
Siguiendo las pautas de la **Fase A**, el sistema modela los siguientes elementos:

* **Sistema de Combate:** Basado en turnos dinámicos calculados según la velocidad de cada entidad.
* **Gestión de Clases:** Implementación de arquetipos como Guerrero, Mago, Arquero o Curador.
* **Atributos Mínimos:** Cada personaje almacena nombre, vida (HP), maná/puntos de acción (PA), ataque, defensa, velocidad, nivel y experiencia.
* **Habilidades y Recursos:** Uso de habilidades que consumen recursos específicos (ej. PA).
* **Inventario y Equipamiento:** Administración de armas, armaduras y accesorios (anillos) que modifican estadísticas.
* **Progresión:** Sistema de recompensas de experiencia, oro y subida de nivel con mejora de atributos.

---

## 📊 Arquitectura y Diseño
El diseño se ha documentado mediante diagramas UML para asegurar un acoplamiento bajo y una alta cohesión entre componentes.

### Diagrama de Clases
El diagrama de clases describe la jerarquía del dominio, incluyendo personajes, enemigos, ítems y la estructura de datos del sistema.
> *Ubicación: `docs/diagrama_clases.png`*

### Diagramas de Secuencia
Se han definido flujos críticos para entender el comportamiento dinámico del motor:
1.  **Lógica de Combate:** Detalla el ciclo de vida de un turno y las interacciones entre atacante y objetivo.
2.  **Sistema de Mapa/Navegación:** Explica la transición entre pantallas y estados del juego.

---

## 📁 Estructura del Proyecto y Capturas
A continuación, se detalla la estructura del proyecto y se muestran capturas de pantalla que ilustran el flujo y la interfaz gráfica implementada.

### Árbol de Directorios
```text
.
├── assets/             # Recursos visuales e imágenes de la interfaz
├── docs/               # Diagramas de Clase y Secuencia (Entregables Fase A)
├── src/
│   ├── model/          # Lógica de negocio (Java)
│   ├── controller/     # Motor de combate y turnos
│   ├── view/           # Interfaz gráfica (Swing/JavaFX)
│   └── persistence/    # Guardado y recuperación de partidas
└── README.md