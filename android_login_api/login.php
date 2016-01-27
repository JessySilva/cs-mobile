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
 
// json reposta do  array
$response = array("error" => FALSE);
 
if (isset($_POST['email']) && isset($_POST['password'])) {
 
    // recebendo parametros do post
    $email = $_POST['email'];
    $password = $_POST['password'];
 
    // obtem o usuário por e-mail e senha
    $user = $db->getUserByEmailAndPassword($email, $password);
 
    if ($user != false) {
        // utilização é encontrada
        $response["error"] = FALSE;
        $response["uid"] = $user["unique_id"];
        $response["user"]["name"] = $user["name"];
        $response["user"]["email"] = $user["email"];
        $response["user"]["created_at"] = $user["created_at"];
        $response["user"]["updated_at"] = $user["updated_at"];
        echo json_encode($response);
    } else {
        // usuário não foi encontrado com as credenciais
        $response["error"] = TRUE;
        $response["error_msg"] = "Credenciais de login estão errados. Por favor, tente novamente!";
        echo json_encode($response);
    }
} else {
    // é necessário parametros do post, está faltando
    $response["error"] = TRUE;
    $response["error_msg"] = "Necessário parâmetros: e-mail ou senha está faltando!";
    echo json_encode($response);
}
?>
