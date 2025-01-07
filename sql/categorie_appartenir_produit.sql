 CREATE TABLE `categorie` (
  `idCategorie` int(11) NOT NULL AUTO_INCREMENT,
  `nomCategorie` varchar(128) NOT NULL,
   PRIMARY KEY (idCategorie)
);

CREATE TABLE `produit` (
  `idProduit` int(11) NOT NULL AUTO_INCREMENT,
  `libelleProduit` varchar(128) NOT NULL,
  `prixUnitaire` DECIMAL(6,2) NOT NULL,
  `prixKilo` DECIMAL(6,2) NOT NULL,
  `nutriscore` char(128) NOT NULL,
  `libelleProduit` varchar(128) NOT NULL,
  `poidsProduit` DECIMAL(6,3) NOT NULL,
  `conditionnementProduit` varchar(128) NOT NULL,
  `marqueProduit` varchar(128) NOT NULL,
   PRIMARY KEY (idClient)
);

 CREATE TABLE `categorie` (
  `idCategorie` int(11) NOT NULL AUTO_INCREMENT,
  `nomCategorie` varchar(128) NOT NULL,
   PRIMARY KEY (idCategorie)
);

 CREATE TABLE `appartenir` (
   PRIMARY KEY (idCategorie, idProduit),
   FOREIGN KEY(idCategorie) REFERENCES categorie(idCategorie),
    FOREIGN KEY(idProduit) REFERENCES produit(idProduit)
);



