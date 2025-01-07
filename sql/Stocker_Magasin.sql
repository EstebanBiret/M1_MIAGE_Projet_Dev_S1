CREATE table magasin(
    idMagasin int AUTO_INCREMENT PRIMARY KEY,
    nomMagasin varchar(20) NOT NULL,
    villeMagasin varchar(20) NOT NULL
);

CREATE table stocker(
    idMagasin int,
    idProduit int,
    quantiteEnStock int NOT NULL,
    PRIMARY KEY(idMagasin,idProduit),
    FOREIGN KEY(idMagasin) REFERENCES magasin(idMagasin),
    FOREIGN KEY(idProduit) REFERENCES produit(idProduit)
);