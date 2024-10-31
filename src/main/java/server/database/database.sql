create database bantau;
go

use bantau;
go

CREATE TABLE players (
    username NVARCHAR(50) PRIMARY KEY,
	password NVARCHAR(50),
    points INT DEFAULT 0,
    total_wins INT DEFAULT 0,
    total_losses INT DEFAULT 0,
    total_afk INT DEFAULT 0
);
go

CREATE TABLE matches (
    match_id INT PRIMARY KEY IDENTITY(1,1),
    user1_username NVARCHAR(50) NOT NULL,
    user2_username NVARCHAR(50) NOT NULL,
    timestamp DATETIME DEFAULT GETDATE(),
    result_user1 NVARCHAR(10) CHECK (result_user1 IN ('win', 'loss', 'afk', 'cancelled')),
    result_user2 NVARCHAR(10) CHECK (result_user2 IN ('win', 'loss', 'afk', 'cancelled')),
    points_change_user1 INT CHECK (points_change_user1 IN (1, 0, -1)),
    points_change_user2 INT CHECK (points_change_user2 IN (1, 0, -1)),
    FOREIGN KEY (user1_username) REFERENCES players(username),
    FOREIGN KEY (user2_username) REFERENCES players(username)
);
go

insert into players (username, password)
values ('test1', '1'), ('test2', '2'), ('test3', '3');