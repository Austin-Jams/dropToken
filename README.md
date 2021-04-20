README.md
Start Mongodb on port 27017: If you need to install on Mac -- brew tap mongodb/brew -- brew install mongodb-community -- brew services start mongodb-community -- brew services start mongodb-community (when done)

Then run ./gradlew to start the application.

I wrote my game in Java using the Spring Framework to help out with my API. My datasource is MongoDb. I used two collections, Game and Move, to store the data. Validation is done in the Controller. Manager is called when needing to talk to the DB as it has a constructor dependency injection of the Repository. My implementation of search takes a slice of each direction and then passes through it too see if a winning sequence exists. Pretty straightforward for horizontal and vertical search, but for the diagonals I calculated a border position and then iterated from that point to capture the slice.
