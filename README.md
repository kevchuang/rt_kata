# rt_kata

# Prerequisites
  Docker
 
# Configuration
  
  First you need to create a docker network.
  `docker network create kata`
  
  Then you need to build the docker image.
  `docker build -t api_kata .`
  
  And finally you can run the containers. It will create 2 containers (api_kata and db_kata), the api_kata containers is bind on port 8086.
  `docker-compose up`
  
  
# API Documentation
  
  **Create an account**
  
  `POST localhost:8086/account?name=Kevin Durant`
  
  To create an account you need to specify the paramater name in the url. The parameter name doesn't support digits.
  
  **Get account balance**
  
  `GET localhost:8086/account?id=1`
  
  To get the account balance, you need to specify the account id.
  
  **Make an operation**
  
  `POST localhost:8086/operations?type=deposit&amount=30&account_id=1`

  To make an operation, you need to specify 3 paramaters:
    - `type:` deposit or withdrawal
    - `amount:` the operation amount
    - `account_id:` account identifier
    
  **Get operations**
  
  `GET localhost:8086/operations?account_id=1`
  
  To get all the operations of an account you need to specify the `account_id`
  
  
  



  
