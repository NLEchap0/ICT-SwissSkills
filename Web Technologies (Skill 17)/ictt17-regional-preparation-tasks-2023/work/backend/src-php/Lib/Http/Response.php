<?php

namespace Skills17\Lib\Http;

class Response
{
    /**
     * Sends a JSON response.
     *
     * @param mixed $json JSON that should be sent
     * @param int $statusCode HTTP response status code
     */
    public function send(mixed $json, int $statusCode = 200): void
    {
        http_response_code($statusCode);

        if ($statusCode === 204) {
            file_put_contents('php://stderr', '[' . date('D M j G:i:s Y') . "] INFO: Skip sending response body for status code 204\n");
        } else {
            header('Content-Type: application/json');
            echo json_encode($json, JSON_PRETTY_PRINT);
        }
    }
}
