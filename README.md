# Running Instructions

`docker-compose up server` will start the app on 8080 port

# Comments

- Test coverage is not great, but I tried to cover at least one scenario on each layer
- Some thrown exceptions will cause 500 Internal Server Error when 400 Bad Request, 404 Not Found would be preferred
  instead. That required setting up a Rest error handler configuration class in which exceptions would be mapped to a
  http status code but that seemed too much effort for this kind of challenge
