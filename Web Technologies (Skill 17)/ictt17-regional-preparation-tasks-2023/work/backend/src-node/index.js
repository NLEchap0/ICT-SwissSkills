//
// This is the main script.
// Start with:
//
// node index.js
//
//
const express = require("express");
const bodyParser = require("body-parser");
const { logger } = require("./logger");
const { sendNotFound } = require("./util");
const { pasteRouter } = require("./routes/paste");

const app = express();

// Using bodyParser to parse JSON bodies into JS objects. We set the maximum payload to 3mb, such that the parser does
// not throw errors when a 2mb payload arrives. Otherwise, we cannot gracefully return a formatted error response.
app.use(bodyParser.json({limit: '3mb'}));

// log HTTP requests
app.use(logger);

// activate sub-routes
app.use("/api/paste", pasteRouter);

// map every other route and return 404
app.use("/*", (req, res) => sendNotFound(req, res));

// add error handler for uncaught errors
app.use("/*", (err, req, res, _next) => {
  console.error("Unknown error occurred:", req.method, req.url, err);
  res.status(500).send({ error: String(err) });
});

// start server
const port = process.argv[2] || 4000;
app.listen(port, () => {
  console.log(`Server started on http://localhost:${port}`);
});
