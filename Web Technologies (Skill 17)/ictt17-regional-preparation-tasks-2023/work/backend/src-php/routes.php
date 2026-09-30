<?php

namespace Skills17;

use Skills17\Controller\PasteController;
use Skills17\Lib\Router;

// Instantiate paste controller instance
$pasteController = new PasteController();

$router = new Router();

// Register all routes
// Variables can be added with the :name notation and will be available in the Skills17\Lib\Http\Request object
// which is passed to the controller method.
// The example below can be called via GET http://localhost:4000/api/paste/blubb
$router->get('/api/paste/:var1', [$pasteController, 'example']); // <- calls the 'example' method on the $pasteController

return $router;
