## General information
The application uses database in memory (H2) for both production and test environments. No need to call external services and mock them. 

- The classes ``AccountRepositoryTest`` and `TransactionRepositoryTest` test the insertion/reading from the H2 in-memory database.
- The service `TransactionService` contains the business logic. The class `TransactionServiceTest` tests all the test cases described inside the `doc/**.pdf` file.
- The controller `TransactionController` contains the REST API requested.
- The controllers `AccountsController` and `TransactionsController` return all the data from the database (for testing purposes)
- The class `TransactionControllerTest` is using `MockMvc` to make fake calls to the application and test again the test cases described inside the `doc/**.pdf` file.
- The file `src/main/resources/import.sql` contains an initial load of the application (for testing purposes)

## Testing
The application is being tested from 
- the junit tests on the repositories and on the services,
- the MockMvc fake calls inside the `TransactionControllerTest`
- By starting up the application and using the postman collection. 
  - Start the application by using the command `mvn clean spring-boot:run` or `mvn clean package && java -jar target/transactions-0.0.1-SNAPSHOT.jar`
  - Import the postman collection inside postman
  - Use the `Create Transaction` API to create the transaction
  - Use the `List Accounts` API and `List Transactions` API to verify the data inside the H2 in-memory database.
  