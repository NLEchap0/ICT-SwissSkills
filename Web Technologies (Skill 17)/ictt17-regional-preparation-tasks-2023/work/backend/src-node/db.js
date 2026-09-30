const mysql = require("mysql2");

const connection = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "",
  database: "skills",
});

connection.connect((err) => {
  if (err) {
    console.error("Error connecting to database: " + err.stack);
    process.exit(1);
  }
});

/**
 * Executes a prepared statement.
 * @param sql {string} The SQL query. You can use '?' as a placeholder for parameters.
 * @param params {(string|number)[]} The parameters in the same order as they should be filled into the query.
 * @return {Promise<Record<string, string>[]>} Promise which resolves to an array of rows as objects.
 */
module.exports.executeQuery = (sql, params) => {
  return new Promise((resolve, reject) => {
    connection.query(sql, params, (err, results) => {
      if (err) {
        reject(err);
        return;
      }
      resolve(results);
    });
  });
};
