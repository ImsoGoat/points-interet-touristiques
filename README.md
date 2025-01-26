# Points d'Intérêt Touristiques

## Description
Ce projet est une application **Spring Boot** permettant de gérer des lieux d'intérêt touristique via une API REST. Il inclut des fonctionnalités pour la gestion des utilisateurs (administrateurs et utilisateurs simples), la validation ou le rejet des lieux, la notation des lieux validés, ainsi que la pagination des données.

## Fonctionnalités
### Gestion des lieux :
- **CRUD** complet sur les lieux d'intérêt.
- Validation et rejet des lieux par un administrateur.
- Notation des lieux validés par les utilisateurs.
- Pagination pour les lieux validés, rejetés ou non validés.

### Gestion des utilisateurs :
- Création et suppression des utilisateurs.
- Rôles :
  - **Utilisateur** : accès public.
  - **Administrateur** : gestion protégée (validation, rejet, suppression).

### API REST :
- Documentation des endpoints via Swagger.
- Pagination et tri des résultats.

## Prérequis
- **Java 17** ou version supérieure.
- **Maven** pour la gestion des dépendances.
- Une base de données **H2** (configuration incluse pour une utilisation en mémoire ou persistante).

## Installation
1. Clonez le dépôt :
   ```bash
   git clone https://github.com/votre-repository.git
   cd points-interet-touristiques
   ```

2. Compilez et lancez l'application avec Maven :
   ```bash
   mvn spring-boot:run
   ```

3. Accédez à l'API via : `http://localhost:8080/api/`.

## Configuration
### Configuration par défaut :
- La base de données est configurée en **H2**.
- Les fichiers de configuration sont situés dans `src/main/resources` :
  - **application.properties** : configuration par défaut.
  - **application-prod.properties** : configuration pour un environnement de production.
  - **application-test.properties** : configuration pour les tests.

## Guide d'utilisation
### Endpoints principaux :
- **Gestion des lieux** :
  - `GET /api/places` : Récupérer tous les lieux (admin).
  - `POST /api/places` : Créer un lieu.
  - `PATCH /api/places/{id}/validate` : Valider un lieu (admin).
  - `PATCH /api/places/{id}/reject` : Rejeter un lieu (admin).
  - `GET /api/places/validatedPlaces` : Récupérer les lieux validés.
  - `GET /api/places/validatedPlaces/paginated` : Récupérer les lieux validés avec pagination.

- **Gestion des utilisateurs** :
  - `POST /api/users` : Créer un utilisateur.
  - `DELETE /api/users/all` : Supprimer tous les utilisateurs.

- **Documentation Swagger** : Accessible via `http://localhost:8080/swagger-ui.html`.

## Tests
- Les tests unitaires couvrent les fonctionnalités publiques et les validations principales.
- Les tests sont situés dans le package `src/test/java`.
- Pour exécuter les tests :
  ```bash
  mvn test
  ```

## Auteurs
- **Annen Julien**
- Projet réalisé dans le cadre du cours JeeSpring à L'He-Arc.

