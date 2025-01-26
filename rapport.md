# Rapport de Projet - Points d'Intérêt Touristiques

## 1. Architecture Implémentée

L'architecture implémentée repose sur une structure **Spring Boot**, respectant le modèle MVC (Model-View-Controller). Voici les principaux composants :

- **Model** :
  - Les entités principales incluent `User`, `Place`, et les autres objets nécessaires à la gestion des lieux et utilisateurs.
  - L'utilisation de **JPA** permet de mapper les entités aux tables de la base de données, avec des relations comme `@OneToMany`, `@ManyToOne`, ou `@ElementCollection`.

- **Controller** :
  - Les `PlaceController` et `UserController` exposent des endpoints RESTful pour la gestion des utilisateurs et des lieux.
  - Pagination, validation des entrées et gestion des statuts des lieux (VALIDATED, UNVALIDATED, REJECTED) sont intégrées.

- **Service** :
  - Le service `PlaceService` centralise la logique métier, comme la validation des lieux, l'attribution des notes, ou la récupération paginée des données.
  - Les interactions directes avec la base de données sont déléguées aux repositories.

- **Repository** :
  - L'interface `PlaceRepository` et `UserRepository`, basées sur **Spring Data JPA**, permettent des opérations simples et complexes via des méthodes comme `findByStatus` ou `findByStatusIn`.

---

## 2. Problèmes, Résolutions, Choix

### Problèmes Rencontrés
1. **Gestion des notes des utilisateurs** :
   - Initialement, les notes d'un lieu étaient stockées dans une liste, ce qui permettait à un utilisateur de noter plusieurs fois un même lieu.
   - **Résolution** : Implémentation d'une `Map<Long, Integer>` pour associer chaque utilisateur à une unique note pour un lieu.

2. **Validation des données entrantes** :
   - Les tests échouaient lorsque des données non valides étaient envoyées, notamment pour la pagination.
   - **Résolution** : Ajout d'une validation des paramètres dans les endpoints (par ex. : tailles de page > 0).

3. **Pagination et tri** :
   - Initialement, la pagination était absente, ce qui rendait difficile la gestion d'une grande quantité de données.
   - **Résolution** : Intégration de `Pageable` avec des paramètres configurables (page, taille, tri).

### Choix Techniques
- **H2 Database** :
  - Environnement de test avec une base en mémoire pour garantir l'isolation des tests.
  - Environnement de production avec une base H2 persistante.
- **Spring Security** :
  - Protection des endpoints avec des vérifications spécifiques, comme les autorisations pour les admins.
- **Tests Unitaires et d'Intégration** :
  - Utilisation de `MockMvc` pour simuler des requêtes HTTP et vérifier les comportements des endpoints.

---

## 3. Bilan

### Réalisation du Cahier des Charges
- Tous les points mentionnés dans le cahier des charges ont été réalisés :
  - Gestion des utilisateurs avec création et suppression.
  - Gestion des lieux incluant leur création, validation, rejet, et notation.
  - Pagination des données pour les lieux validés, rejetés ou non validés.

### Bilan et Évolution
- **Points Positifs** :
  - Utilisation des bonnes pratiques de Spring Boot pour implémenter une architecture RESTful découplée avec injection de dépendances.
  - Mise en place d'une base de données relationnelle H2, entièrement intégrée à l'application avec une gestion appropriée des entités via JPA.
  - Pagination des résultats des lieux pour une meilleure performance et expérience utilisateur dans les requêtes.
  - Tests unitaires couvrant les endpoints publics et les cas d'usage essentiels.
  - Gestion des rôles utilisateur/administrateur avec des règles d'accès claires :
    - Les utilisateurs peuvent visualiser et noter des lieux validés.
    - Les administrateurs ont des privilèges étendus pour gérer les lieux et les utilisateurs.
  - Mise en place d'un système de validation et de rejet pour assurer la modération des contenus ajoutés.

- **Améliorations Futures** :
  - Étendre les tests pour couvrir davantage de scénarios complexes.
  - Préparer une interface utilisateur (front-end) pour faciliter l'interaction avec les APIs.

---

## 4. Guide d'Installation et de Configuration

### Pré-requis
- **JDK** : OpenJDK 17.
- **Maven** : Version 3+.
- **Postman** pour tester les endpoints RESTful.

### Installation
1. **Cloner le projet** :
   ```bash
   git clone <repository-url>
   cd points-interet-touristiques
   ```

2. **Configurer la base de données** :
   - Les fichier `application.properties` contiennent les configurations pour H2 (production) et en mémoire (test).

3. **Construire le projet** :
   ```bash
   mvn clean install
   ```

4. **Lancer l'application** :
   ```bash
   mvn spring-boot:run
   ```

5. **Accéder à l'application** :
   - **H2 Console** : Accessible à `/h2-console`.
   - **API Endpoints** : Utilisez Postman ou un navigateur pour interagir avec les routes.

---

## Conclusion

Le projet a permis de développer une application robuste et extensible, répondant aux objectifs fixés dans le cahier des charges. La modularité de l'architecture facilite l'ajout de nouvelles fonctionnalités tout en garantissant la maintenabilité. 

Concernant le planning, il n'y en a pas eu dans ce projet, car j'avais omis d'en établir un au début du semestre. Par la suite, les délais étant devenus serrés en raison d'une exécution concentrée sur la fin du semestre, j'ai jugé qu'il serait inutile d'en créer un a posteriori. Malgré cela, j'ai su m'adapter et structurer efficacement les étapes du projet pour aboutir à une solution fonctionnelle.


## Annexes

routes.md qui indique tous les endpoints mis en place pour gérer l'application
