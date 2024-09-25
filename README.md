# Light Split
### Description
On a group trip, people usually cover different parts of the expenses. For example, friend A might be in charge of booking plane tickets for the group, and friend B 
booking hotels and renting a car. During the trip, more shared expenses will occur, and it'll be a hassle to pay each other back everytime it happens. **Light Split solves this
problem by minimizing such internal transactions at the end of the trip.** Just a few clicks to document who pays and who shares the expense and Light Split will begin to keep track 
of transaction.

So far, Light Split is a pure backend project built with Spring Boot and MongoDB. Basic user authuentication and authorization are set up with Spring Security using Json Web Tokens(JWT). 

### Future Feature 
1. User interface 
2. More sophisticated user authorization. Group creator should be able to verify who's joining the group, and people shouldn't have access to information from group they don't belong to. 

### How to set up Light Split
1. Clone this git repository locally.
2. Set up spring boot with Maven. 
3. Create an account for mongoDB and download MongoDB compass.
4. Once you created an account, create a database in MongoDB, then copy your database name and unique URI into application.properties file. **(Don't forget to save the actual value in a seperate file protected by .gititnore.)**
5. Create a file named SecurityConstants in the Security package. The class should contain 3 important pieces of secret information: 1) a base64 string JWT_SECRET for encoding JWT
2) a datatype long JWT_EXPIRATION that controls when a JWT expires (in milliseconds) 3) a string ADMIN_KEY to register with both USER and ADMIN role (See RegisterDTO.java)

### How to use Light Split 
1. 




