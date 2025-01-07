CREATE TABLE 'client'(
   'idClient' int(15) NOT NULL AUTO_INCREMENT,
   'nomClient' varchar(150) NOT NULL,
   'adresseClient' varchar(150) NOT NULL,
   'telClient' varchar(150) NOT NULL,
   PRIMARY KEY(idClient),
   FOREIGN KEY(idMagasin) REFERENCES magasin(idMagasin)
);


CREATE TABLE 'profil'(
    'idProfil' int(15) NOT NULL AUTO_INCREMENT,
    'nomProfil' varchar(150) NOT NULL,
    PRIMARY KEY (idProfil)
);

CREATE TABLE 'client_profil'(
    'idClient' int(15) NOT NULL AUTO_INCREMENT,
    'idProfil' int(15) NOT NULL AUTO_INCREMENT,
    PRIMARY KEY(idClient, idProfil),
    FOREIGN KEY(idClient) REFERENCES client(idClient),
    FOREIGN KEY(idProfil) REFERENCES profil(idProfil)
);



