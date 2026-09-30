/**
 * Sends a 404 error to the client.
 * @param req {e.Request}
 * @param res {e.Response}
 * @param _next {e.NextFunction}
 * @param customMessage {string} Custom error message to be returned
 */
module.exports.sendNotFound = (req, res, _next, customMessage = "Not found") => {
  res.status(404);
  res.send({ error: customMessage });
};

/**
 * Used to wrap async functions in express route handlers. Without this, rejected promises do not end the request,
 * and we have a memory leak as well as a never ending request.
 * @param handler {function(e.Request, e.Response): Promise<any>}
 * @return {(function(e.Request, e.Response, e.NextFunction): void)|*}
 */
module.exports.handleAsync = (handler) => {
  return (req, res, next) => {
    handler(req, res).catch((err) => next(err));
  };
};
