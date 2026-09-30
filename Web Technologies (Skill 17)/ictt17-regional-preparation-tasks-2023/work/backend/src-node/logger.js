/**
 * Logs every HTTP request.
 * @param req {e.Request}
 * @param res {e.Response}
 * @param next {e.NextFunction}
 */
module.exports.logger = function logger(req, res, next) {
  res.on("finish", () => {
    process.stdout.write(`[${new Date().toISOString()}] "${req.method} ${
      req.originalUrl || req.url
    }" ${res.statusCode} "${req.headers["user-agent"]}"
`);
  });

  next();
};
