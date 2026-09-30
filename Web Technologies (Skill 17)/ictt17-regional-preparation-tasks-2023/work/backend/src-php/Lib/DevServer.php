<?php

namespace Skills17\Lib;

use Skills17\Lib\Http\Request;
use Skills17\Lib\Http\Response;

require_once __DIR__ . '/../../vendor/autoload.php';
$router = require_once __DIR__ . '/../routes.php';

// ensure the app is started with the php dev server and not accessed using apache since the tests use port 4000
if ($_SERVER['SERVER_PORT'] !== '4000') {
    echo 'Please start the PHP development server by executing "composer start" in the work/backend/ folder.';
    echo '<br>';
    echo 'The API is then available at <a href="http://localhost:4000">http://localhost:4000</a>';
    die();
}

// create request & response object
$path = explode('?', $_SERVER['REQUEST_URI'])[0];
$requestMethod = $_SERVER['REQUEST_METHOD'];
$requestBody = json_decode(file_get_contents('php://input') ?: '{}', true);
$requestHeaders = getallheaders();
$request = new Request($path, $requestMethod, $requestBody, $requestHeaders);
$response = new Response();

// log request
file_put_contents('php://stderr', '[' . date('D M j G:i:s Y') . '] ' . $request->getHttpMethod() . ' ' . $request->getPath() . "\n");

// call route
$exists = $router->callRoute($request, $response);

if (!$exists) {
    $response->send(['error' => 'Not found'], 404);
}
