# Fantasy Premier League - Entity & DTO Documentation

## Overview

Complete entity and DTO implementation for a Fantasy Premier League clone application using **Lombok** for entities and **Java Records** for DTOs.

---

## 📊 Entity Relationship Diagram

```
User (1) ←→ (1) UserTeam
UserTeam (1) ←→ (M) UserTeamPlayer ←→ (M) Player
Player (1) ←→ (M) PlayerGameweekStats
Gameweek (1) ←→ (M) Match
Gameweek (1) ←→ (M) PlayerGameweekStats
Match (1) ←→ (M) PlayerGameweekStats
```

---

## 🎯 Business Rules

### Squad Building
- **Budget**: Maximum 100.0
- **Squad Size**: Exactly 15 players
- **Validation**: Total player cost ≤ budget

### Scoring System
| Event | FWD | MID | DEF/GK |
|-------|-----|-----|--------|
| Goal | +4 | +5 | +6 |
| Assist | +3 | +3 | +3 |
| Yellow Card | -1 | -1 | -1 |
| Red Card | -3 | -3 | -3 |

---

## 📦 Entities (Lombok)

### Core Entities

#### `User.java`
- Authentication entity
- One-to-One with `UserTeam`
- Fields: username, email, password, timestamps

#### `UserTeam.java`
- Fantasy team management
- Budget tracking (100.0 default)
- Validation methods: `isValidSquadSize()`, `isWithinBudget()`
- One-to-Many with `UserTeamPlayer`

#### `Player.java`
- Real player data
- Position-based goal scoring: `getGoalPoints()`
- Fields: name, position, realTeam, price, totalPoints
- One-to-Many with `PlayerGameweekStats`

#### `Gameweek.java`
- Season week tracking
- Status: UPCOMING, ACTIVE, COMPLETED
- Helper methods: `isActive()`, `isCompleted()`

#### `Match.java`
- Match data
- Fields: homeTeam, awayTeam, scores, kickoffTime
- Many-to-One with `Gameweek`

#### `PlayerGameweekStats.java`
- Player performance per gameweek
- **Scoring Logic**: `calculatePoints()` method
- Fields: minutesPlayed, goals, assists, yellowCards, redCards, pointsEarned
- Relationships: Player, Gameweek, Match

#### `UserTeamPlayer.java`
- Junction table (Team ↔ Player)
- Tracks purchase price and selection date
- Composite unique constraint

---

## 📝 DTOs (Java Records)

### Authentication

```java
RegisterRequest(username, email, password)
LoginRequest(emailOrUsername, password)
UserResponse(id, username, email, createdAt)
AuthResponse(token, type, user)
```

### Team Management

```java
TeamCreateRequest(teamName, playerIds) // Validates 15 players
TeamResponse(id, teamName, budget, remainingBudget, totalPoints, players, playerCount)
```

### Player Information

```java
PlayerResponse(id, name, position, realTeam, price, totalPoints, goalPoints)
PlayerSummary(id, name, position, realTeam, price, totalPoints) // Lightweight
```

### Gameweek & Match

```java
GameweekResponse(id, gameweekNumber, startDate, endDate, status)
MatchResponse(id, gameweekNumber, homeTeam, awayTeam, homeScore, awayScore, kickoffTime, finished)
PlayerStatsResponse(id, playerId, playerName, position, gameweekNumber, matchDetails, minutesPlayed, goals, assists, yellowCards, redCards, pointsEarned)
```

### Error Handling

```java
ErrorResponse(message, error, status)
```

---

## 🔧 Key Features

### Lombok Annotations Used
- `@Data` - Getters, setters, toString, equals, hashCode
- `@Builder` - Builder pattern
- `@NoArgsConstructor` - Default constructor (required by JPA)
- `@AllArgsConstructor` - All-args constructor

### JPA Annotations
- `@Entity`, `@Table` - Entity mapping
- `@Id`, `@GeneratedValue` - Primary key
- `@OneToOne`, `@OneToMany`, `@ManyToOne` - Relationships
- `@Enumerated` - Enum mapping
- `@CreationTimestamp`, `@UpdateTimestamp` - Automatic timestamps

### Validation Annotations (DTOs)
- `@NotBlank` - Non-empty strings
- `@NotNull` - Non-null values
- `@Email` - Email format
- `@Size` - Length constraints

---

## 💡 Usage Examples

### Creating a Team

```java
TeamCreateRequest request = new TeamCreateRequest(
    "My Dream Team",
    List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L)
);
```

### Calculating Player Points

```java
PlayerGameweekStats stats = PlayerGameweekStats.builder()
    .player(player)
    .goals(2)
    .assists(1)
    .yellowCards(0)
    .redCards(0)
    .build();

stats.calculatePoints(); // Automatically calculates based on position
```

### Validating Squad

```java
UserTeam team = userTeamRepository.findById(teamId);
boolean valid = team.isValidSquadSize() && team.isWithinBudget();
```

---

## 📁 File Structure

```
src/main/java/com/example/backend/
├── model/
│   ├── Position.java (enum)
│   ├── EventType.java (enum)
│   ├── User.java
│   ├── UserTeam.java
│   ├── Player.java
│   ├── Gameweek.java
│   ├── Match.java
│   ├── PlayerGameweekStats.java
│   └── UserTeamPlayer.java
└── dto/
    ├── RegisterRequest.java
    ├── LoginRequest.java
    ├── UserResponse.java
    ├── AuthResponse.java
    ├── TeamCreateRequest.java
    ├── TeamResponse.java
    ├── PlayerResponse.java
    ├── PlayerSummary.java
    ├── GameweekResponse.java
    ├── MatchResponse.java
    ├── PlayerStatsResponse.java
    └── ErrorResponse.java
```

---

## ✅ Next Steps

1. **Repositories**: Create JPA repositories for each entity
2. **Services**: Implement business logic (team creation, scoring, etc.)
3. **Controllers**: Create REST endpoints
4. **Data Loading**: Parse `fique_data` file and populate database
5. **Security**: Implement JWT authentication
6. **Validation**: Add custom validators for business rules
