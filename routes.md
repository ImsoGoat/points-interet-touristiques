# Documentation des Endpoints de l'API Points d'Intérêt Touristiques

## Endpoints pour les Lieux (Places)

### 1. Récupérer tous les lieux
- **URL** : `/api/places`
- **Méthode** : `GET`
- **Paramètres** :
  - `userId` : ID de l'utilisateur (doit être un administrateur).
- **Description** : Récupère tous les lieux, accessible uniquement pour les administrateurs.
- **Exemple dans Postman** :
  ```
  GET http://localhost:8080/api/places?userId=1
  ```

---

### 2. Ajouter un lieu
- **URL** : `/api/places`
- **Méthode** : `POST`
- **Body (JSON)** :
  ```json
  {
    "name": "Tour Eiffel",
    "description": "Un monument célèbre à Paris.",
    "location": "Paris, France",
    "latitude": 48.858844,
    "longitude": 2.294351
  }
  ```
- **Description** : Permet à un utilisateur de proposer un lieu. Les lieux sont créés avec un statut `UNVALIDATED`.
- **Exemple dans Postman** :
  ```
  POST http://localhost:8080/api/places
  Body (raw, JSON) :
  {
    "name": "Tour Eiffel",
    "description": "Un monument célèbre à Paris.",
    "location": "Paris, France",
    "latitude": 48.858844,
    "longitude": 2.294351
  }
  ```

---

### 3. Récupérer un lieu par ID
- **URL** : `/api/places/{id}`
- **Méthode** : `GET`
- **Paramètres** :
  - `id` : ID du lieu à récupérer.
  - `userId` : ID de l'utilisateur (les utilisateurs normaux ne peuvent voir que les lieux validés).
- **Description** : Récupère un lieu spécifique. Les utilisateurs normaux ne peuvent voir que les lieux validés, tandis que les administrateurs peuvent tout voir.
- **Exemple dans Postman** :
  ```
  GET http://localhost:8080/api/places/1?userId=2
  ```

---

### 4. Mettre à jour un lieu
- **URL** : `/api/places/{id}`
- **Méthode** : `PUT`
- **Paramètres** :
  - `id` : ID du lieu à mettre à jour.
  - `userId` : ID de l'utilisateur (doit être un administrateur).
- **Body (JSON)** :
  ```json
  {
    "name": "Nouveau Nom",
    "description": "Nouvelle description.",
    "location": "Nouvelle localisation",
    "latitude": 45.764043,
    "longitude": 4.835659
  }
  ```
- **Description** : Met à jour les informations d'un lieu. Accessible uniquement par les administrateurs.
- **Exemple dans Postman** :
  ```
  PUT http://localhost:8080/api/places/1?userId=1
  ```

---

### 5. Supprimer un lieu
- **URL** : `/api/places/{id}`
- **Méthode** : `DELETE`
- **Paramètres** :
  - `id` : ID du lieu à supprimer.
  - `userId` : ID de l'utilisateur (doit être un administrateur).
- **Description** : Supprime un lieu de la base de données. Accessible uniquement par les administrateurs.
- **Exemple dans Postman** :
  ```
  DELETE http://localhost:8080/api/places/1?userId=1
  ```

---

### 6. Récupérer les lieux validés
- **URL** : `/api/places/validatedPlaces`
- **Méthode** : `GET`
- **Description** : Récupère tous les lieux validés. Accessible par tous les utilisateurs.
- **Exemple dans Postman** :
  ```
  GET http://localhost:8080/api/places/validatedPlaces
  ```

---

### 7. Récupérer les lieux non validés
- **URL** : `/api/places/unvalidatedPlaces`
- **Méthode** : `GET`
- **Paramètres** :
  - `userId` : ID de l'utilisateur (doit être un administrateur).
- **Description** : Récupère tous les lieux non validés. Accessible uniquement par les administrateurs.
- **Exemple dans Postman** :
  ```
  GET http://localhost:8080/api/places/unvalidatedPlaces?userId=1
  ```

---

### 8. Récupérer les lieux refusés
- **URL** : `/api/places/rejectedPlaces`
- **Méthode** : `GET`
- **Paramètres** :
  - `userId` : ID de l'utilisateur (doit être un administrateur).
- **Description** : Récupère tous les lieux refusés. Accessible uniquement par les administrateurs.
- **Exemple dans Postman** :
  ```
  GET http://localhost:8080/api/places/rejectedPlaces?userId=1
  ```

---

### 9. Récupérer les lieux non validés et refusés
- **URL** : `/api/places/unvalidatedAndRejectedPlaces`
- **Méthode** : `GET`
- **Paramètres** :
  - `userId` : ID de l'utilisateur (doit être un administrateur).
- **Description** : Récupère les lieux ayant un statut `UNVALIDATED` ou `REJECTED`. Accessible uniquement par les administrateurs.
- **Exemple dans Postman** :
  ```
  GET http://localhost:8080/api/places/unvalidatedAndRejectedPlaces?userId=1
  ```

---

### 10. Valider un lieu
- **URL** : `/api/places/{id}/validate`
- **Méthode** : `PATCH`
- **Paramètres** :
  - `id` : ID du lieu à valider.
  - `userId` : ID de l'utilisateur (doit être un administrateur).
- **Description** : Change le statut d'un lieu en `VALIDATED`.
- **Exemple dans Postman** :
  ```
  PATCH http://localhost:8080/api/places/1/validate?userId=1
  ```

---

### 11. Rejeter un lieu
- **URL** : `/api/places/{id}/reject`
- **Méthode** : `PATCH`
- **Paramètres** :
  - `id` : ID du lieu à rejeter.
  - `userId` : ID de l'utilisateur (doit être un administrateur).
- **Description** : Change le statut d'un lieu en `REJECTED`.
- **Exemple dans Postman** :
  ```
  PATCH http://localhost:8080/api/places/1/reject?userId=1
  ```

---

### 12. Ajouter une note à un lieu
- **URL** : `/api/places/{id}/rate`
- **Méthode** : `POST`
- **Paramètres** :
  - `id` : ID du lieu à noter.
  - `userId` : ID de l'utilisateur.
  - `rating` : La note (entre 1 et 10).
- **Description** : Ajoute une note à un lieu. Seuls les lieux validés peuvent être notés.
- **Exemple dans Postman** :
  ```
  POST http://localhost:8080/api/places/1/rate?userId=2&rating=8
  ```

---

### 13. Récupérer les notes d'un lieu
- **URL** : `/api/places/{id}/ratings`
- **Méthode** : `GET`
- **Paramètres** :
  - `id` : ID du lieu.
- **Description** : Récupère toutes les notes d'un lieu.
- **Exemple dans Postman** :
  ```
  GET http://localhost:8080/api/places/1/ratings
  ```

---

### 14. Récupérer la moyenne des notes d'un lieu
- **URL** : `/api/places/{id}/average-rating`
- **Méthode** : `GET`
- **Paramètres** :
  - `id` : ID du lieu.
- **Description** : Récupère la moyenne des notes d'un lieu.
- **Exemple dans Postman** :
  ```
  GET http://localhost:8080/api/places/1/average-rating
  ```
  ---

## Pagination des lieux validés
### Description
Récupère les lieux validés avec pagination.
- **Méthode HTTP** : `GET`
- **URL** : `/api/places/validatedPlaces/paginated`
- **Paramètres** :
  - `page` (optionnel) : Numéro de page (par défaut : 0).
  - `size` (optionnel) : Taille de la page (par défaut : 10).
  - `sort` (optionnel) : Champ et ordre de tri (par défaut : `name,asc`).
- **Réponses** :
  - `200 OK` : Page des lieux validés.
  - `400 Bad Request` : Si les paramètres de pagination sont invalides.

---

## Pagination des lieux non validés
### Description
Récupère les lieux non validés avec pagination.
- **Méthode HTTP** : `GET`
- **URL** : `/api/places/unvalidatedPlaces/paginated`
- **Paramètres** : Même que pour les lieux validés.
- **Réponses** :
  - `200 OK` : Page des lieux non validés.

---

## Pagination des lieux rejetés
### Description
Récupère les lieux rejetés avec pagination.
- **Méthode HTTP** : `GET`
- **URL** : `/api/places/rejectedPlaces/paginated`
- **Paramètres** : Même que pour les lieux validés.
- **Réponses** :
  - `200 OK` : Page des lieux rejetés.

---

## Pagination des lieux non validés et rejetés
### Description
Récupère les lieux non validés et rejetés avec pagination.
- **Méthode HTTP** : `GET`
- **URL** : `/api/places/unvalidatedAndRejectedPlaces/paginated`
- **Paramètres** : Même que pour les lieux validés.
- **Réponses** :
  - `200 OK` : Page des lieux non validés et rejetés.
