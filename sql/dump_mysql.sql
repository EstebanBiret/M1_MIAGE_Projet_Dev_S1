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
  `nutriscore` char(128) NOT NULL,
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
  `dateFinPanier` datetime NOT NULL,
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
-- Insertion des données
--

COMMIT;