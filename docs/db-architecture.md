## **Movie/Streaming Database Schema Structure**

```
Movie Table (PK: movieId)
├── movieId (INT, AUTO_INCREMENT, PRIMARY KEY)
├── title (VARCHAR(200), NOT NULL)
├── releaseYear (INT)
├── runtime (INT) -- in minutes
├── rating (DECIMAL(3,1)) -- e.g., 8.5
├── description (TEXT)
├── genreId (INT, FOREIGN KEY → Genre.genreId) -- Many-to-One
├── directorId (INT, FOREIGN KEY → Director.directorId) -- Many-to-One
├── language (VARCHAR(50))
└── posterUrl (VARCHAR(255))

Actor Table (PK: actorId)
├── actorId (INT, AUTO_INCREMENT, PRIMARY KEY)
├── firstName (VARCHAR(100), NOT NULL)
├── lastName (VARCHAR(100), NOT NULL)
├── birthDate (DATE)
├── nationality (VARCHAR(50))
└── bio (TEXT)

Director Table (PK: directorId)
├── directorId (INT, AUTO_INCREMENT, PRIMARY KEY)
├── firstName (VARCHAR(100), NOT NULL)
├── lastName (VARCHAR(100), NOT NULL)
├── birthDate (DATE)
├── nationality (VARCHAR(50))
└── awardsWon (INT)

Genre Table (PK: genreId)
├── genreId (INT, AUTO_INCREMENT, PRIMARY KEY)
├── genreName (VARCHAR(50), NOT NULL, UNIQUE) -- Action, Drama, Comedy, etc.
└── description (VARCHAR(255))

MovieCast Table (Junction Table - Many-to-Many: Movie ↔ Actor)
├── castId (INT, AUTO_INCREMENT, PRIMARY KEY)
├── movieId (INT, FOREIGN KEY → Movie.movieId, ON DELETE CASCADE)
├── actorId (INT, FOREIGN KEY → Actor.actorId, ON DELETE CASCADE)
├── characterName (VARCHAR(100)) -- Role they played
├── castOrder (INT) -- 1 = lead role, 2 = supporting, etc.
└── UNIQUE(movieId, actorId) -- Same actor can't be added twice to same movie

StreamingPlatform Table (PK: platformId)
├── platformId (INT, AUTO_INCREMENT, PRIMARY KEY)
├── platformName (VARCHAR(100), NOT NULL, UNIQUE) -- Netflix, Disney+, etc.
├── subscriptionFee (DECIMAL(6,2))
└── launchDate (DATE)

MovieAvailability Table (Junction Table - Many-to-Many: Movie ↔ Platform)
├── availabilityId (INT, AUTO_INCREMENT, PRIMARY KEY)
├── movieId (INT, FOREIGN KEY → Movie.movieId, ON DELETE CASCADE)
├── platformId (INT, FOREIGN KEY → StreamingPlatform.platformId, ON DELETE CASCADE)
├── availableFrom (DATE)
├── availableUntil (DATE)
└── UNIQUE(movieId, platformId)

User Table (PK: userId) -- OPTIONAL: for watchlist feature
├── userId (INT, AUTO_INCREMENT, PRIMARY KEY)
├── username (VARCHAR(50), NOT NULL, UNIQUE)
├── email (VARCHAR(100), NOT NULL, UNIQUE)
├── joinDate (DATE)
└── subscriptionType (ENUM('free', 'premium'))

Watchlist Table (Junction Table - Many-to-Many: User ↔ Movie) -- OPTIONAL
├── watchlistId (INT, AUTO_INCREMENT, PRIMARY KEY)
├── userId (INT, FOREIGN KEY → User.userId, ON DELETE CASCADE)
├── movieId (INT, FOREIGN KEY → Movie.movieId, ON DELETE CASCADE)
├── addedDate (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
└── UNIQUE(userId, movieId)
```

---

## **Relationship Summary**

### **One-to-Many (Vertical)**
```
Genre (1) → Movies (Many)
├── One genre has many movies
└── FK: Movie.genreId

Director (1) → Movies (Many)
├── One director directs many movies
└── FK: Movie.directorId

StreamingPlatform (1) → MovieAvailability (Many)
User (1) → Watchlist (Many)
```

### **Many-to-Many (Requires Junction Tables)**
```
Movie (M) ↔ MovieCast ↔ Actor (N)
├── Many movies have many actors
├── Many actors appear in many movies
└── Junction stores: characterName, castOrder

Movie (M) ↔ MovieAvailability ↔ StreamingPlatform (N)
├── Many movies on many platforms
└── Junction stores: availability dates

User (M) ↔ Watchlist ↔ Movie (N) -- OPTIONAL
├── Many users watch many movies
└── Junction stores: addedDate
```