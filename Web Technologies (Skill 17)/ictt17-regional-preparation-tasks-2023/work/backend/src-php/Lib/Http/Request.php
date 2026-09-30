<?php

namespace Skills17\Lib\Http;

class Request
{
    /**
     * Request path
     */
    private string $path;

    /**
     * HTTP request method
     */
    private string $httpMethod;

    /**
     * Holds the values of all path params.
     * E.g. for path /api/paste/:var1, it will look like this: {"var1":"..."}
     */
    private array $params;

    /**
     * Holds the request body.
     * The request body always contains json content which is already parsed/decoded.
     * E.g. for request body '{"foo": "bar"}', $body will be ['foo' => 'bar']
     */
    private mixed $body;

    /**
     * Holds all request headers
     */
    private array $headers;

    /**
     * Request construct
     *
     * @param string $path Request path
     * @param string $httpMethod HTTP request method
     * @param mixed $body Parsed request body
     * @param array $headers Request headers
     * @param array $params Path parameters
     */
    public function __construct(string $path, string $httpMethod, mixed $body = [], array $headers = [], array $params = [])
    {
        $this->path = $path;
        $this->httpMethod = $httpMethod;
        $this->params = $params;
        $this->body = $body;
        $this->headers = $headers;
    }

    /**
     * Get the request path
     *
     * @return string
     */
    public function getPath(): string
    {
        return $this->path;
    }

    /**
     * Get the HTTP request method
     *
     * @return string
     */
    public function getHttpMethod(): string
    {
        return $this->httpMethod;
    }

    /**
     * Get the path parameters.
     * E.g. for path /api/paste/:var1, it will look like this: {"var1":"..."}
     *
     * @return array
     */
    public function getParams(): array
    {
        return $this->params;
    }

    /**
     * Get a single path parameter
     *
     * @param $name Path parameter name
     * @param $default Default value if the parameter does not exist
     * @return mixed
     */
    public function getParam(string $name, mixed $default = null): mixed
    {
        if (!isset($this->params[$name])) {
            return $default;
        }

        return $this->params[$name];
    }

    /**
     * Set the path parameters
     *
     * @param array $params New path parameters
     */
    public function setParams(array $params): void
    {
        $this->params = $params;
    }

    /**
     * Get the parsed request body.
     * The request body always contains json content which is already parsed/decoded.
     * E.g. for request body '{"foo": "bar"}', $body will be ['foo' => 'bar']
     *
     * @return mixed
     */
    public function getBody(): mixed
    {
        return $this->body;
    }

    /**
     * Get all request headers
     *
     * @return array
     */
    public function getHeaders(): array
    {
        return $this->headers;
    }

    /**
     * Get the value of a single header
     *
     * @param $name Header name
     * @param $default Default value if the header does not exist
     * @return mixed
     */
    public function getHeader(string $name, mixed $default = null): mixed
    {
        if (!isset($this->headers[$name])) {
            return $default;
        }

        return $this->headers[$name];
    }
}
