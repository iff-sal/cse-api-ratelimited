install redis-stack. this program uses redis json. so any redis with redisjson module will work.


the data is already in resources folder.



start the redis-stack server.

start the application by running main.java

the endpoint is http://localhost:4567/stock

some example queries

http://localhost:4567/stock?symbol=AAF&date=05-JAN-24

http://localhost:4567/stock?symbol=AAF&date=05-JAN-24&field=CLOSE%20PRICE%20(Rs.)



switch between different rate limiting algorithm by commenting out the ratelimiter in main.java

try to get response from the endpoint http://localhost:4567/stock in different speed and see the rate limiter in action.

to run the test. start docker and run. it run on offical redis testcontainer.


## how this works

the application will read from excel files in resources folder. there are different reader implementation for different file types but every reader return a lsit of map. and each map is row where key is column and value is the cell value.

then using gson it will serlize to json object and then upload to redis using a custom built uploader which support json commands like json get json set.

there is rate limiter middleware between endpoint. every rate limiter algorithms implements ratelimiter interface and returns isAllowed() boolean. if its true then api ednpoint will send request data other wise it will send 429 limit exceed response.



