SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

--
-- Base de données : `projet_dev_m1_miage_s1`
--
CREATE DATABASE IF NOT EXISTS `projet_dev_m1_miage_s1` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `projet_dev_m1_miage_s1`;

-- --------------------------------------------------------

--
-- Création des tables
--

CREATE table magasin(
  `idMagasin` int(11) NOT NULL AUTO_INCREMENT,
  `nomMagasin` varchar(128) NOT NULL,
  `adresseMagasin` varchar(128) NOT NULL,
  PRIMARY KEY(idMagasin)
);

CREATE TABLE `produit` (
  `idProduit` int(11) NOT NULL AUTO_INCREMENT,
  `libelleProduit` varchar(128) NOT NULL,
  `prixUnitaire` DECIMAL(6,2) NOT NULL,
  `prixKilo` DECIMAL(6,2),
  `nutriscore` enum('A', 'B', 'C', 'D', 'E') NOT NULL,
  `poidsProduit` DECIMAL(6,3) NOT NULL,
  `conditionnementProduit` varchar(128) NOT NULL,
  `marqueProduit` varchar(128) NOT NULL,
  PRIMARY KEY (idProduit)
);

CREATE TABLE `categorie` (
  `idCategorie` int(11) NOT NULL AUTO_INCREMENT,
  `nomCategorie` varchar(128) NOT NULL,
  PRIMARY KEY (idCategorie)
);

CREATE TABLE `profil` (
  `idProfil` int(11) NOT NULL AUTO_INCREMENT,
  `nomProfil` varchar(128) NOT NULL,
  PRIMARY KEY (idProfil)
);

CREATE TABLE `client` (
  `idClient` int(11) NOT NULL AUTO_INCREMENT,
  `idMagasin` int(11) NOT NULL,
  `nomClient` varchar(128) NOT NULL,
  `prenomClient` varchar(128) NOT NULL,
  `adresseClient` varchar(128) NOT NULL,
  `telClient` varchar(128) NOT NULL,
  PRIMARY KEY(idClient),
  FOREIGN KEY(idMagasin) REFERENCES magasin(idMagasin)
);

CREATE TABLE `client_profil` (
  `idClient` int(11) NOT NULL,
  `idProfil` int(11) NOT NULL,
  PRIMARY KEY(idClient, idProfil),
  FOREIGN KEY(idClient) REFERENCES client(idClient),
  FOREIGN KEY(idProfil) REFERENCES profil(idProfil)
);

CREATE TABLE `appartenir` (
  `idCategorie` int(11) NOT NULL,
  `idProduit` int(11) NOT NULL,
  PRIMARY KEY (idCategorie, idProduit),
  FOREIGN KEY(idCategorie) REFERENCES categorie(idCategorie),
  FOREIGN KEY(idProduit) REFERENCES produit(idProduit)
);

CREATE table `stocker` (
  `idMagasin` int(11) NOT NULL,
  `idProduit` int(11) NOT NULL, 
  `quantiteEnStock` int(11) NOT NULL,
  PRIMARY KEY(idMagasin,idProduit),
  FOREIGN KEY(idMagasin) REFERENCES magasin(idMagasin),
  FOREIGN KEY(idProduit) REFERENCES produit(idProduit)
);

CREATE TABLE `panier` (
  `idPanier` int(11) NOT NULL AUTO_INCREMENT,
  `idClient` int(11) NOT NULL,
  `panierTermine` boolean NOT NULL,
  `dateDebutPanier` datetime NOT NULL,
  `dateFinPanier` datetime,
  PRIMARY KEY (idPanier),
  FOREIGN KEY (idClient) REFERENCES client(idClient)
);

CREATE TABLE `commande` (
  `idCommande` int(11) NOT NULL AUTO_INCREMENT,
  `idPanier` int(11) NOT NULL,
  `TypeCommande` enum('livraison', 'retrait', 'mixte') NOT NULL,
  `statutCommande` enum('en attente', 'preparation', 'retrait', 'envoi', 'terminee') NOT NULL,
  `dateReception` datetime NOT NULL,
  `datePreparation` datetime,
  `dateFinalisation` datetime,
  PRIMARY KEY (idCommande),
  FOREIGN KEY (idPanier) REFERENCES panier(idPanier)
);

CREATE TABLE `panier_produit_magasin` (
  `idPanier` int(11) NOT NULL,
  `idProduit` int(11) NOT NULL,
  `idMagasin` int(11) NOT NULL,
  `quantiteVoulue` int(11) NOT NULL,
  `modeLivraison` enum('livraison','retrait') NOT NULL,
  PRIMARY KEY (idPanier, idProduit, idMagasin),
  FOREIGN KEY (idPanier) REFERENCES panier(idPanier),
  FOREIGN KEY (idProduit) REFERENCES produit(idProduit),
  FOREIGN KEY (idMagasin) REFERENCES magasin(idMagasin)
);

--
-- Insertion des données (petites pour tests)
--

INSERT INTO magasin (nomMagasin, adresseMagasin) VALUES
('Carrefour Toulouse Purpan', '36 Allée Jean Jaurès, 31000 Toulouse'),
('Auchan Toulouse Balma', 'Route de Lavaur, 31130 Balma'),
('Intermarché Saint-Orens', '2 Rue des Champs Pinsons, 31650 Saint-Orens'),
('Lidl Blagnac', '5 Avenue de l\'Aérodrome, 31700 Blagnac'),
('Biocoop Toulouse Minimes', '12 Avenue des Minimes, 31200 Toulouse'),
('Monoprix Capitole', '10 Place du Capitole, 31000 Toulouse'),
('Leclerc Roques-sur-Garonne', 'Route d\'Espagne, 31120 Roques'),
('Casino Ramonville', '24 Rue de l\'Université, 31520 Ramonville-Saint-Agne');

INSERT INTO profil (nomProfil) VALUES
('Famille'),
('Bio'),
('Vegan'),
('Végétarien'),
('Sans Gluten'),
('Gourmand');

INSERT INTO client (idMagasin, nomClient, prenomClient, adresseClient, telClient) VALUES
(1, 'Martin', 'Claire', '5 Rue de l\'Occitanie, 31000 Toulouse', '0601020304'),
(2, 'Dupuis', 'Antoine', '12 Avenue de Balma, 31130 Balma', '0611223344'),
(3, 'Lemoine', 'Sarah', '18 Rue de Saint-Orens, 31650 Saint-Orens', '0622334455'),
(4, 'Durand', 'Luc', '7 Rue des Ailes, 31700 Blagnac', '0633445566'),
(5, 'Petit', 'Emma', '10 Avenue des Minimes, 31200 Toulouse', '0644556677'),
(6, 'Moreau', 'Julien', '15 Place du Capitole, 31000 Toulouse', '0655667788'),
(7, 'Bernard', 'Sophie', '20 Route d\'Espagne, 31120 Roques', '0666778899'),
(8, 'Girard', 'Laura', '25 Rue de l\'Université, 31520 Ramonville-Saint-Agne', '0677889900');

INSERT INTO client_profil (idClient, idProfil) VALUES
(1, 1), -- Claire Martin est une cliente Famille
(2, 2), -- Antoine Dupuis est un client Bio
(3, 3), -- Sarah Lemoine est Vegan
(4, 4), -- Luc Durand est Végétarien
(5, 5), -- Emma Petit est Sans Gluten
(6, 6), -- Julien Moreau est Gourmand
(7, 1), -- Sophie Bernard est Famille
(8, 2); -- Laura Girard est Bio

INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES 
('Jus d\'orange', 2.5, 3.2, 'A', 1.0, '1L', 'Tropicana'),
('Jus d\'orange', 1.8, 2.5, 'B', 1.0, '1L', 'Carrefour'),
('Jus d\'orange', 3.0, 3.8, 'A', 1.5, '1.5L', 'Andros'),
('Pâtes Penne', 1.2, 2.4, 'B', 0.5, '500g', 'Barilla'),
('Pâtes Penne', 1.0, 2.0, 'C', 0.5, '500g', 'Panzani'),
('Pâtes Spaghetti', 1.5, 3.0, 'A', 0.5, '500g', 'Barilla'),
('Chocolat noir', 1.8, 18.0, 'C', 0.1, '100g', 'Lindt'),
('Chocolat noir', 2.0, 20.0, 'B', 0.1, '100g', 'Nestlé'),
('Céréales chocolat', 3.8, 10.1, 'C', 0.375, '375g', 'Kellogg\'s'),
('Céréales chocolat', 4.0, 10.6, 'B', 0.375, '375g', 'Nestlé'),
('Poulet entier', 8.99, 0.0, 'A', 1.5, '1 pièce', 'Label Rouge'),
('Pizza surgelée', 4.5, 11.2, 'D', 0.4, '1 pièce', 'Buitoni'),
('Pizza surgelée', 5.0, 12.5, 'C', 0.4, '1 pièce', 'Dr. Oetker'),
('Eau minérale', 0.6, 0.0, 'A', 1.5, '1.5L', 'Evian'),
('Eau minérale', 0.4, 0.0, 'B', 1.5, '1.5L', 'Cristaline'),
('Lait demi-écrémé', 0.89, 0.89, 'B', 1.0, '1L', 'Lactel'),
('Lait demi-écrémé', 1.0, 1.0, 'A', 1.0, '1L', 'Candia'),
('Saumon fumé', 5.0, 25.0, 'B', 0.2, '200g', 'Labeyrie'),
('Saumon fumé', 4.8, 24.0, 'A', 0.2, '200g', 'Delpeyrat');

INSERT INTO stocker (idMagasin, idProduit, quantiteEnStock) VALUES
(1, 1, 120), -- Carrefour Toulouse Purpan
(1, 4, 80), -- Carrefour Toulouse Purpan
(1, 6, 50), -- Carrefour Toulouse Purpan
(2, 2, 100), -- Auchan Toulouse Balma
(2, 8, 30), -- Auchan Toulouse Balma
(2, 10, 70), -- Auchan Toulouse Balma
(3, 12, 40), -- Intermarché Saint-Orens
(3, 14, 60), -- Intermarché Saint-Orens
(3, 17, 100), -- Intermarché Saint-Orens
(4, 3, 80), -- Lidl Blagnac
(4, 5, 60), -- Lidl Blagnac
(4, 9, 20), -- Lidl Blagnac
(5, 11, 45), -- Biocoop Toulouse Minimes
(5, 18, 10), -- Biocoop Toulouse Minimes
(6, 7, 30), -- Monoprix Capitole
(6, 15, 25), -- Monoprix Capitole
(7, 13, 90), -- Leclerc Roques-sur-Garonne
(7, 16, 40), -- Leclerc Roques-sur-Garonne
(8, 19, 35); -- Casino Ramonville

INSERT INTO categorie (nomCategorie) VALUES 
('Boissons'), ('Viandes'), ('Produits Laitiers'), ('Epicerie'), ('Surgelés');


INSERT INTO appartenir (idCategorie, idProduit) VALUES 
(1, 1), (1, 2), (1, 3), -- Jus d'orange
(4, 4), (4, 5), -- Pâtes Penne
(4, 6), -- Pâtes Spaghetti
(4, 7), (4, 8), -- Chocolat noir
(4, 9), (4, 10), -- Céréales chocolat
(2, 11), -- Poulet entier
(5, 12), (5, 13), -- Pizza surgelée
(1, 14), (1, 15), -- Eau minérale
(3, 16), (3, 17), -- Lait demi-écrémé
(2, 18), (2, 19); -- Saumon fumé


INSERT INTO panier (idClient, panierTermine, dateDebutPanier, dateFinPanier) VALUES
(1, 0, '2025-01-06 09:00:00', NULL), -- Panier en cours (client 1)
(1, 1, '2024-12-20 10:00:00', '2024-12-21 12:00:00'), -- Ancien panier terminé (client 1)
(2, 1, '2024-12-15 14:30:00', '2024-12-16 10:00:00'), -- Panier terminé (client 2)
(3, 0, '2025-01-05 08:00:00', NULL), -- Panier en cours (client 3)
(4, 1, '2024-11-25 11:00:00', '2024-11-26 15:00:00'), -- Panier terminé (client 4)
(5, 0, '2025-01-07 13:00:00', NULL), -- Panier en cours (client 5)
(5, 1, '2024-12-10 16:00:00', '2024-12-11 18:00:00'); -- Ancien panier terminé (client 5)

INSERT INTO commande (idPanier, statutCommande, dateCommande) VALUES
(2, 'terminée', '2025-01-02 16:45:00'),
(3, 'préparation', '2025-01-03 15:45:00'),
(5, 'retrait', '2025-01-05 13:30:00'),
(6, 'livraison', '2025-01-06 19:15:00'),
(7, 'terminée', '2025-01-08 10:15:00');

INSERT INTO panier_produit_magasin (idPanier, idProduit, idMagasin, quantiteVoulue, modeLivraison) VALUES
-- Panier client 1 (en cours)
(1, 1, 1, 3, 'retrait'),
(1, 2, 2, 1, 'livraison'),
-- Ancien panier client 1
(2, 3, 1, 2, 'livraison'),
(2, 4, 1, 5, 'retrait'),
-- Panier client 2 (terminé)
(3, 5, 2, 1, 'retrait'),
(3, 6, 3, 2, 'livraison'),
-- Panier client 3 (en cours)
(4, 7, 4, 4, 'livraison'),
(4, 8, 4, 6, 'retrait'),
(4, 9, 4, 1, 'livraison'),
-- Panier client 4 (terminé)
(5, 10, 5, 2, 'retrait'),
(5, 9, 6, 1, 'livraison'),
-- Panier client 5 (en cours)
(6, 8, 7, 3, 'livraison'),
(6, 7, 7, 2, 'retrait'),
-- Ancien panier client 5
(7, 2, 8, 5, 'livraison'),
(7, 3, 8, 1, 'retrait');

COMMIT;