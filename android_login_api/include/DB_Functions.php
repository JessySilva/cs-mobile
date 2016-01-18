<?php
 
class DB_Functions {
 
    private $conn;
 
    // construtor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }
 
    // destruidor
    function __destruct() {
         
    }
 
    /**
     * Armazenar novo usuário
     * retorna detalhes do usuário
     */
    public function storeUser($name, $email, $password) {
        $uuid = uniqid('', true);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // senha encriptada
        $salt = $hash["salt"]; // salto
 
        $stmt = $this->conn->prepare("INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("sssss", $uuid, $name, $email, $encrypted_password, $salt);
        $result = $stmt->execute();
        $stmt->close();
 
        // verificação do armazenamento
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
 
            return $user;
        } else {
            return false;
        }
    }
 
    /**
     * Get user by email and password
     */
   /**
 *  public function getUserByEmailAndPassword($email, $password,$database) {
 *  
 *         $stmt = $this->conn->prepare("SELECT * FROM $database WHERE email = ?");
 *         $stmt->bind_param("s", $email);
 *          if ($stmt->execute()) {
 *             $user = $stmt->get_result()->fetch_assoc();
 *             $hashed_password = $this->checkhashSHA($user["salt"],$password);
 *         if($hashed_password == $user["encrypted_password"]){
 *         $stmt->close();
 *         return $user;
 *      } else {
 *         $stmt->close();
 *         return NULL;
 *      }
 *   } else {
 *         $stmt->close();
 *         return NULL;
 *      }
 *        $stmt->close();
 * }
 */
 
        public function getUserByEmailAndPassword($email, $password) {
           
           $result = mysqli_query($this->conn,"SELECT * FROM users WHERE email = '$email'") or die(mysqli_connect_errno());
           $no_of_rows = mysqli_num_rows($result);
             if ($no_of_rows > 0) {
                  $result = mysqli_fetch_array($result);
                  $salt = $result['salt'];
                  $encrypted_password = $result['encrypted_password'];
                  $hash = $this->checkhashSSHA($salt, $password);
             if ($encrypted_password == $hash) {
                  return $result;
             }
      } else {
                  return false;
       }
}
 
    /**
     * Checa se o usuario existe
     */
    public function isUserExisted($email) {
        $stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");
 
        $stmt->bind_param("s", $email);
 
        $stmt->execute();
 
        $stmt->store_result();
 
        if ($stmt->num_rows > 0) {
            // usuario existente
            $stmt->close();
            return true;
        } else {
            // usuario não existe
            $stmt->close();
            return false;
        }
    }
 
    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {
 
        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }
 
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {
 
        $hash = base64_encode(sha1($password . $salt, true) . $salt);
 
        return $hash;
    }
 
}
 
?>
