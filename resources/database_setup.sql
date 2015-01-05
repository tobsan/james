
CREATE TABLE Subscribers(
    SubscriberID VARCHAR(25) PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    CoAddress VARCHAR(100),
    Address VARCHAR(100) NOT NULL,
    ZipCode VARCHAR(10) NOT NULL,
    City VARCHAR(50) NOT NULL,
    Country VARCHAR(50),
    Note VARCHAR(50)
);

CREATE TABLE Issues(
    IssueYear INT,
    IssueNumber INT,
    PRIMARY KEY (IssueYear, IssueNumber)
);

CREATE TABLE Distributor(
    DistributorName VARCHAR(10) PRIMARY KEY,
    LongName VARCHAR(100)
);

INSERT INTO Distributor VALUES("VTD", "Västsvensk Tidningsdistribution");
INSERT INTO Distributor VALUES("TB", "Tidningsbärarna");
INSERT INTO Distributor VALUES("BRING", "Bring CityMail");
INSERT INTO Distributor VALUES("POSTEN", "Posten");
INSERT INTO Distributor VALUES("NONE", "No distributor (NoThanks list)");

CREATE TABLE Filter(
    ZipCode VARCHAR(10),
    Distributor VARCHAR(10) REFERENCES Distributor(DistributorName),
    PRIMARY KEY (ZipCode, Distributor)
);

CREATE TABLE DistributedBy(
    SubscriberID VARCHAR(25) REFERENCES Subscribers(SubscriberID) PRIMARY KEY,
    Distributor VARCHAR(10) REFERENCES Distributor(DistributorName)
);

CREATE TABLE DistributedTo(
    SubscriberID VARCHAR(25) REFERENCES Subscribers(SubscriberID),
    IssueYear INT REFERENCES Issues(IssueYear),
    IssueNumber INT REFERENCES Issues(IssueNumber),
    PRIMARY KEY (SubscriberID, IssueYear, IssueNumber)
);

CREATE VIEW VIP AS SELECT * FROM Subscribers NATURAL JOIN DistributedBy NATURAL JOIN DistributedTo WHERE Distributor = 'POSTEN' AND LENGTH(SubscriberID) < 6;

