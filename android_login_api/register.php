<?php

/**
 *     / )|                                   |( \
 *    / / |         Charles Silva             | \ \
 *  _( (_ |  _  [www.charlessilva.com.br]  _  | _) )_
 * (((\ \)|_/ )___________________________( \_|(/ /)))
 * (\\\\ \_/ /                             \ \_/ ////)
 *  \       /                               \       /
 *   \    _/                                 \_    /
 *   /   /                                     \   \
 * Autor   : Charles Silva (suporte@charlessilva.com.br)
 * Linguagem : PHP (Hypertext Preprocessor)
 * URL: www.charlessilva.com.br
 * twitter: http://twitter.com/charlessilva_
 * GitHub: https://github.com/silvacharles
 */

 
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
 
// json respota do array
$response = array("error" => FALSE);
 
if (isset($_POST['name']) && isset($_POST['email']) && isset($_POST['password'])) {
 
    // recebe os parametros do post
    $name = $_POST['name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
 
    // verificar se o usuário existe com o mesmo e-mail
    if ($db->isUserExisted($email)) {
        // usuario existe
        $response["error"] = TRUE;
        $response["error_msg"] = "Usuário já existe com " . $email;
        echo json_encode($response);
    } else {
        // criando novo usuario
        $user = $db->storeUser($name, $email, $password);
        if ($user) {
            // usuario armazenado com sucesso
            $response["error"] = FALSE;
            $response["uid"] = $user["unique_id"];
            $response["user"]["name"] = $user["name"];
            $response["user"]["email"] = $user["email"];
            $response["user"]["created_at"] = $user["created_at"];
            $response["user"]["updated_at"] = $user["updated_at"];
            echo json_encode($response);
        } else {
            // falha ao armazenar usuario
            $response["error"] = TRUE;
            $response["error_msg"] = "Ocorreu um erro desconhecido no registro!";
            echo json_encode($response);
        }
    }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Parâmetros necessários (nome, e-mail ou senha) está faltando!";
    echo json_encode($response);
}
?>
