# Yolo Group test assignment

## Rules

The original rules are present in the `test-assignment-junior-java-.txt` file.

Since the original victory condition `playerGuess > serverNunmber`with the reward formula
`win = bet * (99 / (100 - number))` resulted in an average RTP of over 500% and `99` almost always winning
with the highest reward, I decided to change the victory condition to `playerGuess < serverNumber`.

## Running

### Prerequisites

- Java 11

### Using IntelliJ
- Import the project
- Run the `YoloGroupTestAssignmentApplication` class or tests

### Using the Maven Wrapper

- `mvnw spring-boot:run` to run the application
- `mvnw test` to run the tests
- `mvnw test -Dtest="BetServiceRtpTest"` to run only the RTP test

## API

The server is running on port `8080` specified in `application.properties`.

You can use Postman to test it.


### REST

Send a `POST` request to `localhost:8080/bet` with the JSON body.

Using `curl`:

```shell
curl -X POST -H "Content-Type: application/json" -d "{"guess": 50, "amount": 40.5}" http://localhost:8080/bet
```

### WebSocket

Do a handshake with `ws://localhost:8080/ws/bet` and then send the JSON body.

### Correct JSON

#### Request

```json
{
    "guess": 50,
    "amount": 40.5
}
```

#### Response

```json
{
    "win": 80.19
}
```

### Incorrect JSON

#### Request

```json
{
  "guess": -50
}
```

#### Response

```json
{
  "status": "BAD_REQUEST",
  "message": "Validation failed",
  "errors": [
    "amount: must not be null",
    "guess: Guess should be an integer in range [1, 100]"
  ]
}
```