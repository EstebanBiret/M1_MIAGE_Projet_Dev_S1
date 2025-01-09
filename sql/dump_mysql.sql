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
  `idMagasin` int(11) NOT NULL,
  `panierTermine` boolean NOT NULL,
  `dateDebutPanier` datetime NOT NULL,
  `dateFinPanier` datetime,
  PRIMARY KEY (idPanier),
  FOREIGN KEY (idClient) REFERENCES client(idClient),
  FOREIGN KEY (idMagasin) REFERENCES magasin(idMagasin)
);

CREATE TABLE `commande` (
  `idCommande` int(11) NOT NULL AUTO_INCREMENT,
  `idPanier` int(11) NOT NULL,
  `statutCommande` enum('préparation', 'retrait', 'envoi', 'terminée') NOT NULL,
  `dateCommande` datetime NOT NULL,
  PRIMARY KEY (idCommande),
  FOREIGN KEY (idPanier) REFERENCES panier(idPanier)
);

CREATE TABLE `panier_produit` (
  `idPanier` int(11) NOT NULL,
  `idProduit` int(11) NOT NULL,
  `quantiteVoulue` int(11) NOT NULL,
  `modeLivraison` enum('livraison','retrait') NOT NULL,
  PRIMARY KEY (idPanier, idProduit),
  FOREIGN KEY (idPanier) REFERENCES panier(idPanier),
  FOREIGN KEY (idProduit) REFERENCES produit(idProduit)
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

INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES ('Jus d\'orange', 2.5, 3.2, 'A', 1.0, '1L', 'Tropicana');
INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES ('Poulet entier', 8.99, 0.0, 'A', 1.5, '1 pièce', 'Label Rouge');
INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES ('Chocolat noir', 1.8, 18.0, 'C', 0.1, '100g', 'Lindt');
INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES ('Pâtes Penne', 1.2, 2.4, 'B', 0.5, '500g', 'Barilla');
INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES ('Lait demi-écrémé', 0.89, 0.89, 'B', 1.0, '1L', 'Lactel');
INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES ('Pizza surgelée', 4.5, 0.0, 'D', 0.4, '1 pièce', 'Buitoni');
INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES ('Yaourt nature', 0.5, 0.0, 'A', 0.125, '1 pot', 'Danone');
INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES ('Céréales chocolat', 3.8, 0.0, 'C', 0.375, '375g', 'Kellogg\'s');
INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES ('Eau minérale', 0.6, 0.0, 'A', 1.5, '1.5L', 'Evian');
INSERT INTO produit (libelleProduit, prixUnitaire, prixKilo, nutriscore, poidsProduit, conditionnementProduit, marqueProduit) VALUES ('Saumon fumé', 5.0, 25.0, 'B', 0.2, '200g', 'Labeyrie');

INSERT INTO stocker (idMagasin, idProduit, quantiteEnStock) VALUES
(1, 1, 120), -- Carrefour Toulouse Purpan a 120 Jus d'orange
(1, 4, 80),  -- Carrefour Toulouse Purpan a 80 Pâtes Penne
(2, 3, 150), -- Auchan Toulouse Balma a 150 Chocolat noir
(2, 5, 100), -- Auchan Toulouse Balma a 100 Lait demi-écrémé
(3, 6, 50),  -- Intermarché Saint-Orens a 50 Pizzas surgelées
(4, 9, 200), -- Lidl Blagnac a 200 Eaux minérales
(5, 10, 30), -- Biocoop Toulouse Minimes a 30 Saumons fumés
(6, 8, 70),  -- Monoprix Capitole a 70 Céréales chocolat
(7, 7, 90),  -- Leclerc Roques-sur-Garonne a 90 Yaourts nature
(8, 2, 60);  -- Casino Ramonville a 60 Poulets entiers

INSERT INTO categorie (nomCategorie) VALUES ('Boissons');
INSERT INTO categorie (nomCategorie) VALUES ('Viandes');
INSERT INTO categorie (nomCategorie) VALUES ('Produits Laitiers');
INSERT INTO categorie (nomCategorie) VALUES ('Epicerie');
INSERT INTO categorie (nomCategorie) VALUES ('Surgelés');

INSERT INTO appartenir (idCategorie, idProduit) VALUES (1, 1); -- Jus d'orange dans Boissons
INSERT INTO appartenir (idCategorie, idProduit) VALUES (3, 1); -- Jus d'orange dans Produits Laitiers (exemple pour une catégorie mixte)
INSERT INTO appartenir (idCategorie, idProduit) VALUES (2, 2); -- Poulet entier dans Viandes
INSERT INTO appartenir (idCategorie, idProduit) VALUES (4, 3); -- Chocolat noir dans Epicerie
INSERT INTO appartenir (idCategorie, idProduit) VALUES (4, 4); -- Pâtes Penne dans Epicerie
INSERT INTO appartenir (idCategorie, idProduit) VALUES (3, 5); -- Lait demi-écrémé dans Produits Laitiers
INSERT INTO appartenir (idCategorie, idProduit) VALUES (1, 5); -- Lait demi-écrémé dans Boissons
INSERT INTO appartenir (idCategorie, idProduit) VALUES (5, 6); -- Pizza surgelée dans Surgelés
INSERT INTO appartenir (idCategorie, idProduit) VALUES (3, 7); -- Yaourt nature dans Produits Laitiers
INSERT INTO appartenir (idCategorie, idProduit) VALUES (4, 8); -- Céréales chocolat dans Epicerie
INSERT INTO appartenir (idCategorie, idProduit) VALUES (1, 9); -- Eau minérale dans Boissons
INSERT INTO appartenir (idCategorie, idProduit) VALUES (2, 10); -- Saumon fumé dans Viandes
INSERT INTO appartenir (idCategorie, idProduit) VALUES (5, 10); -- Saumon fumé dans Surgelés

INSERT INTO panier (idClient, idMagasin, panierTermine, dateDebutPanier, dateFinPanier) VALUES
(1, 1, FALSE, '2025-01-01 10:00:00', NULL), -- Claire Martin a un panier en cours
(2, 2, TRUE, '2025-01-02 15:00:00', '2025-01-02 16:30:00'), -- Antoine Dupuis a terminé son panier
(3, 3, TRUE, '2025-01-03 14:00:00', '2025-01-03 15:30:00'), -- Sarah Lemoine a terminé son panier
(4, 4, FALSE, '2025-01-04 16:00:00', NULL), -- Luc Durand a un panier en cours
(5, 5, TRUE, '2025-01-05 12:00:00', '2025-01-05 13:15:00'), -- Emma Petit a terminé son panier
(6, 6, TRUE, '2025-01-06 18:00:00', '2025-01-06 19:00:00'), -- Julien Moreau a terminé son panier
(7, 7, FALSE, '2025-01-07 14:00:00', NULL), -- Sophie Bernard a un panier en cours
(8, 8, TRUE, '2025-01-08 09:00:00', '2025-01-08 10:00:00'), -- Laura Girard a terminé son panier
(2, 1, TRUE, '2025-01-09 10:30:00', '2025-01-09 12:00:00'), -- Antoine Dupuis a un second panier
(3, 2, TRUE, '2025-01-10 16:00:00', '2025-01-10 17:30:00'), -- Sarah Lemoine a un second panier
(1, 4, TRUE, '2025-01-11 11:00:00', '2025-01-11 12:15:00'), -- Claire Martin a un autre panier terminé
(6, 7, TRUE, '2025-01-12 13:00:00', '2025-01-12 14:30:00'); -- Julien Moreau a un autre panier terminé

INSERT INTO commande (idPanier, statutCommande, dateCommande) VALUES
(2, 'terminée', '2025-01-02 16:45:00'),
(3, 'préparation', '2025-01-03 15:45:00'),
(5, 'retrait', '2025-01-05 13:30:00'),
(6, 'livraison', '2025-01-06 19:15:00'),
(8, 'terminée', '2025-01-08 10:15:00'),
(9, 'préparation', '2025-01-09 12:30:00'),
(10, 'livraison', '2025-01-10 17:45:00'),
(11, 'terminée', '2025-01-11 12:30:00'),
(12, 'préparation', '2025-01-12 14:45:00');

INSERT INTO panier_produit (idPanier, idProduit, quantiteVoulue, modeLivraison) VALUES
(1, 1, 2, 'retrait'), -- Claire a commandé 2 Jus d'orange en retrait
(1, 4, 1, 'retrait'), -- Claire a commandé 1 Pâtes Penne en retrait
(2, 3, 5, 'livraison'), -- Antoine a commandé 5 Chocolat noir en livraison
(3, 5, 3, 'livraison'), -- Sarah a commandé 3 Lait demi-écrémé en livraison
(3, 7, 2, 'retrait'),  -- Sarah a commandé 2 Yaourts nature en retrait
(5, 8, 4, 'livraison'), -- Emma a commandé 4 Céréales chocolat en livraison
(6, 6, 3, 'retrait'), -- Julien a commandé 3 Pizzas surgelées en retrait
(7, 9, 10, 'retrait'), -- Sophie a commandé 10 Eaux minérales en retrait
(8, 10, 2, 'livraison'), -- Laura a commandé 2 Saumons fumés en livraison
(9, 2, 1, 'retrait'), -- Antoine a commandé 1 Poulet entier en retrait
(10, 1, 3, 'livraison'), -- Sarah a commandé 3 Jus d'orange en livraison
(11, 4, 2, 'retrait'), -- Claire a commandé 2 Pâtes Penne en retrait
(12, 5, 6, 'livraison'); -- Julien a commandé 6 Lait demi-écrémé en livraison

COMMIT;