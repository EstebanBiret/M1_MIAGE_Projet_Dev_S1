CREATE table magasin(
    idMagasin int(11) AUTO_INCREMENT PRIMARY KEY,
    nomMagasin varchar(128) NOT NULL,
    adresseMagasin varchar(128) NOT NULL
);

CREATE table stocker(
    idMagasin int(11),
    idProduit int(11),
    quantiteEnStock int(11) NOT NULL,
    PRIMARY KEY(idMagasin,idProduit),
    FOREIGN KEY(idMagasin) REFERENCES magasin(idMagasin),
    FOREIGN KEY(idProduit) REFERENCES produit(idProduit)
);
