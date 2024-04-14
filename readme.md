# kredinbizde (DefineX Final Project)

## Project Description

This project is developed to fetch customer information and process credit card or loan applications using a common database among banks operating in the credit sector. Through a central database and services like Akbank, banks can access customer information and process applications. The project routes through a gateway, communicates in the background via APIs, and utilizes MySQL database. As an example of RabbitMQ usage, every transaction in the database sends log information via RabbitMQ to the notification-service, which then stores this information in a MySQL database. Therefore, queries may take some time. You can comment out these lines to perform faster queries. All necessary unit tests in the service layer for "kredinbizde-service" and "akbank-service" run successfully.

### Running the Project

1. Run the docker-compose files in the directories "kredinbizde-service", "akbank-service", and "notification-service" to set up the necessary database and services.
2. First, run "kredinbizde-discovery" project.(Eureka server)
3. Run the "kredinbizde-gw" (gateway) project.
4. Run the "kredinbizde-service" project. Make sure that the databases and necessary services are ready and running.
5. Run the "akbank-service" project.
6. Run the "notification-service" project. This service consumes log messages from RabbitMQ and stores them in the MySQL database.
7. If the projects are running smoothly, you can execute your queries by importing the sample queries from the "postman_queries.json" file into Postman.

#### Tasks

- "kredinbizde-discovery" is the project where the Eureka server is located.
- "kredinbizde-gw" is the Gateway project that facilitates communication between projects and handles routing.
- "akbank-service" is the service used to apply for and query credit card and loan applications. To apply on for a user, that user must be a customer of the that bank.
- "kredinbizde-service" is the main project where user and bank information is stored and managed in the MySQL database.
- "notification-service" is a service that consumes log messages via RabbitMQ and stores them in a MySQL database.

##### Notes

- For the queries in "akbank-service" to work correctly, the ID of Akbank in the "banks" table in the "kredinbizde" database needs to be manually set in the 
"akbank-service" project. The default ID value is 2. So, if the ID of Akbank is not 2 in the database, queries may not work correctly or you may encounter errors.
- The delete user feature is added only for deleting objects created for unit tests in akbank-service. Instead of deleting from the database, especially for important and live projects, it would be more appropriate to maintain an active boolean and change it.