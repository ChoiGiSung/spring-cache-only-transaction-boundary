# spring-cache-only-transaction-boundary
The implementation of this feature is provided for cases where Hibernate's first-level cache is not utilized.    
In MyBatis, local cache clears the cache regardless of entity when an insert, update, or delete statement occurs  
https://stackoverflow.com/questions/49695413/spring-cache-binded-only-to-the-current-transaction  
