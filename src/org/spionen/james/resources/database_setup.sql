--
--   Copyright 2010-2015 Maxim Fris, Tobias Olausson
--
--   This file is part of James.
--
--   James is free software: you can redistribute it and/or modify
--   it under the terms of the GNU General Public License as published by
--   the Free Software Foundation, either version 3 of the License, or
--   (at your option) any later version.
--
--   James is distributed in the hope that it will be useful,
--   but WITHOUT ANY WARRANTY; without even the implied warranty of
--   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--   GNU General Public License for more details.
--
--   You should have received a copy of the GNU General Public License
--   along with James. If not, see <http://www.gnu.org/licenses/>.
--

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

