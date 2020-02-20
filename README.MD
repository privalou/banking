# Banking
This is a simple RESTful web application for account storing and money transferring. 

# Stack of that application includes:

1. Spark
2. H2 In-Memory database
3. Jooq

# REST API 
The REST API to the banking app is described below.

## Account 

### Request
`GET /api/v1/accounts/:id/balance` - obtaining balance of account.

### Response

    {
      "status": "SUCCESS",
      "data": 10.00
    }
    
### Request
`POST /api/v1/accounts/` - creating account.
    
Body:

    {
    	"balance":"10.00"
    }

### Response

    {
      "status": "SUCCESS",
      "data": {
        "id": 9,
        "balance": 10.00
      }
    }

## Transfers 

### Request
`GET /api/v1/transfers/:accountId` - obtaining history of account transfers.

### Response

    {
      "status": "SUCCESS",
      "data": [
        {
          "senderId": 1,
          "receiverId": 2,
          "amount": 10.00
        },
        {
          "senderId": 1,
          "receiverId": 2,
          "amount": 1.00
        }
      ]
    }
    
### Request
`POST /api/v1/transfers/` - executing money transfer
    
Body:

    {
    	"senderId": 1,
    	"receiverId": 2,
    	"amount": 100
    }

### Response

    {
      "status": "SUCCESS"
    }


To create the executable jar run:

`mvn clean install` 

And then you should run jar with command: 

`java -jar target/banking.jar`

