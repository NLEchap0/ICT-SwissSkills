<?php

namespace Skills17\Lib;

use Skills17\Lib\Http\Request;
use Skills17\Lib\Http\Response;

class Router
{
    /**
     * Holds all registered routes
     */
    private array $routes = [];

    /**
     * Register a GET route for the given path
     *
     * @param string $path Request path
     * @param callable $handler Handler function that will be called for this request path
     */
    public function get(string $path, callable $handler): void
    {
        $this->registerRoute('GET', $path, $handler);
    }

    /**
     * Register a POST route for the given path
     *
     * @param string $path Request path
     * @param callable $handler Handler function that will be called for this request path
     */
    public function post(string $path, callable $handler): void
    {
        $this->registerRoute('POST', $path, $handler);
    }

    /**
     * Register a PUT route for the given path
     *
     * @param string $path Request path
     * @param callable $handler Handler function that will be called for this request path
     */
    public function put(string $path, callable $handler): void
    {
        $this->registerRoute('PUT', $path, $handler);
    }

    /**
     * Register a PATCH route for the given path
     *
     * @param string $path Request path
     * @param callable $handler Handler function that will be called for this request path
     */
    public function patch(string $path, callable $handler): void
    {
        $this->registerRoute('PATCH', $path, $handler);
    }

    /**
     * Register a DELETE route for the given path
     *
     * @param string $path Request path
     * @param callable $handler Handler function that will be called for this request path
     */
    public function delete(string $path, callable $handler): void
    {
        $this->registerRoute('DELETE', $path, $handler);
    }

    /**
     * Calls the route for the given request if it exists
     *
     * @param Request $request Request instance
     * @param Response $response Response instance
     * @return bool True if a route for the request exists, false otherwise
     */
    public function callRoute(Request $request, Response $response): bool
    {
        if (!isset($this->routes[$request->getHttpMethod()])) {
            return false;
        }

        foreach ($this->routes[$request->getHttpMethod()] as $route) {
            if (preg_match($route['pattern'], $request->getPath(), $matches)) {
                $request->setParams(array_filter($matches, 'is_string', ARRAY_FILTER_USE_KEY));

                call_user_func($route['handler'], $request, $response);
                return true;
            }
        }

        return false;
    }

    /**
     * Register a route for the given path
     *
     * @param string $httpMethod HTTP request method
     * @param string $path Request path
     * @param callable $handler Handler function that will be called for this request path
     */
    private function registerRoute(string $httpMethod, string $path, callable $handler): void
    {
        if (!isset($this->routes[$httpMethod])) {
            $this->routes[$httpMethod] = [];
        }

        $pattern = '/^' . str_replace('/', '\/', preg_quote($path)) . '$/';

        // replace variables (:var) in the path with named capture groups
        $pattern = preg_replace('/\\\:([^\/\\\\$]*)/', '(?\'$1\'[^\/]*)', $pattern);

        $this->routes[$httpMethod][] = [
            'pattern' => $pattern,
            'handler' => $handler,
        ];
    }
}
