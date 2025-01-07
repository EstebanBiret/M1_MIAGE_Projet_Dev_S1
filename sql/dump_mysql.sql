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
-- Structure de la table `client`
--

CREATE TABLE `client` (
  `idClient` int(11) NOT NULL AUTO_INCREMENT,
  `nomClient` varchar(128) NOT NULL,
  `adresseClient` varchar(128) NOT NULL,
  `telClient` varchar(128) NOT NULL,
   PRIMARY KEY (idClient)
);

--
-- Insertion des données dans la table `client`
--

INSERT INTO `client` (`nomClient`, `adresseClient`, `telClient`) VALUES
('Test', '1 rue mysql', '0654452112');

-- --------------------------------------------------------

-- Index pour la table `custom_location`
--
-- ALTER TABLE `custom_location`
--  ADD PRIMARY KEY (`id`),
--  ADD KEY `custom_location_fk_id_nar` (`id_narrative`),
--  ADD KEY `fk_custom_location_country` (`id_country`);

--
-- Contraintes pour la table `custom_location`
--
-- ALTER TABLE `custom_location`
--  ADD CONSTRAINT `custom_location_fk_id_nar` FOREIGN KEY (`id_narrative`) REFERENCES `narrative` (`id`),
--  ADD CONSTRAINT `fk_custom_location_country` FOREIGN KEY (`id_country`) REFERENCES `country` (`id`);

COMMIT;