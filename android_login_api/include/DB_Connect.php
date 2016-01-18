<?php
class DB_Connect {
    private $conn;
 
    // conectando ao banco de dados
    public function connect() {
        require_once 'include/Config.php';
         
        // conectando mysql no banco de dados
        $this->conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
         
        // retorna para o banco de dados handler
        return $this->conn;
    }
}
 
?>
