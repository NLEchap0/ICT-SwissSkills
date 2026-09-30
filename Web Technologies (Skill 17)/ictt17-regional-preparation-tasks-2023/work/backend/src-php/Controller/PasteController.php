<?php

namespace Skills17\Controller;

use Skills17\Lib\Db\Database;
use Skills17\Lib\Http\Request;
use Skills17\Lib\Http\Response;
use Skills17\Lib\Uuid;

class PasteController
{
    // set up the DB connection which is available at $this->db
    use Database;

    /**
     * Example route that can be deleted or adapted.
     * Contains usage examples and can be called via GET http://localhost:4000/api/paste/blubb (defined in src-php/routes.php)
     *
     * @param Request $request
     * @param Response $response
     * @return Response
     */
    public function example(Request $request, Response $response)
    {
        $stmt = $this->db->prepare('SELECT * FROM paste WHERE access_token = :access_token');
        $stmt->bindValue(':access_token', 'a8fce094-4c6d-4f04-8588-d35d087c4432');
        $stmt->execute();
        $results = $stmt->fetchAll();

        return $response->send([
            'foo' => 'bar',
            'requestParams' => $request->getParams(),
            'uuid' => Uuid::v4(),
            'results' => $results,
        ], 200);
    }
}
