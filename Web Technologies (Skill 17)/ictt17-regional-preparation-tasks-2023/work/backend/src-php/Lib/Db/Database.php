<?php

namespace Skills17\Lib\Db;

use PDO;

trait Database
{
    /**
     * PDO Instance
     */
    public PDO $db;

    /**
     * Database constructor
     */
    public function __construct()
    {
        // create a new connection to the database
        $this->db = new PDO('mysql:host=127.0.0.1;dbname=skills;charset=utf8mb4', 'root', '');
        $this->db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        $this->db->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
    }
}
