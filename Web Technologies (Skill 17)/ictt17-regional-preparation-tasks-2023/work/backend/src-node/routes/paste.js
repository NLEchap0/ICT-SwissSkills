const express = require("express");
const crypto = require("crypto");
const { handleAsync } = require("../util");
const { executeQuery } = require("../db");

const pasteRouter = express.Router();

// Example route that can be deleted or adapted.
// This can be called via GET http://localhost:4000/api/paste/blubb
pasteRouter.get(
  "/:var1",
  handleAsync(async (req, res, next) => {
    const results = await executeQuery(
      "SELECT * from paste where access_token = ?",
      ['a8fce094-4c6d-4f04-8588-d35d087c4432']
    );
    res.send({ foo: "bar", uuid: crypto.randomUUID(), results });
  })
);

module.exports = { pasteRouter };
