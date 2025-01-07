CREATE TABLE `commande` (
  `idCommande` int(11) NOT NULL AUTO_INCREMENT,
  `statutCommande` varchar(128) NOT NULL,
  `dateCommande` date NOT NULL,
  `heureCommande` time NOT NULL,
   PRIMARY KEY (idCommande)
);

CREATE TABLE `panier` (
  `idPanier` int(11) NOT NULL AUTO_INCREMENT,
  `idClient` int(11) NOT NULL,
   PRIMARY KEY (idPanier),
   FOREIGN KEY (idClient) REFERENCES client(idClient)
);

CREATE TABLE `panier_produit` (
    `idPanier` int(11) NOT NULL,
    `idProduit` int(11) NOT NULL,
    `quantiteVoulue` int(11) NOT NULL,
    `modeLivraison` varchar(128) NOT NULL,
    PRIMARY KEY (idPanier, idProduit),
    FOREIGN KEY (idPanier) REFERENCES panier(idPanier),
    FOREIGN KEY (idProduit) REFERENCES produit(idProduit)
);