<?php
 
class DbHandler
{
    private $conn;
 
    //Constructor
    function __construct()
    {
        require_once dirname(__FILE__) . '/config.php';
        require_once dirname(__FILE__) . '/db_connect.php';
        // opening db connection
        $db = new DbConnect();
        $this->conn = $db->connect();
    }
 
    //Function to get the user with email
    public function getUser($code_user)
    {
        $stmt = $this->conn->prepare("SELECT code_parent AS code_user, name, created_on FROM parent WHERE code_parent = ? UNION SELECT code_child AS code_user, name, created_on FROM child WHERE code_child = ? ;");
        $stmt->bind_param("s", $code_user);
        $stmt->execute();
        $user = $stmt->get_result()->fetch_assoc();
        return $user;
    }
 
    //Function to check whether user exist or not
    private function isUserExists($code_user)
    {
        $stmt = $this->conn->prepare("SELECT code_user FROM all_users WHERE code_user=?");
        $stmt->bind_param("s", $code_user);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }
 
    //Function to store gcm registration token in database
    public function storeGCMToken($code_user, $token)
    {
        $stmt = $this->conn->prepare("UPDATE all_users SET gcm_registration_id =? WHERE code_user=?");
        $stmt->bind_param("ss", $token, $code_user);
        if ($stmt->execute())
            return true;
        return false;
    }
 
    //Function to get the registration token from the database
    //The id is of the person who is sending the message
    //So we are excluding his registration token as sender doesnt require notification
    public function getRegistrationTokens($code_user){
        $stmt = $this->conn->prepare("SELECT gcm_registration_id FROM all_users WHERE NOT code_user = ?;");
        $stmt->bind_param("s",$code_user);
        $stmt->execute();
        $result = $stmt->get_result();
        $tokens = array();
        while($row = $result->fetch_assoc()){
            array_push($tokens,$row['gcm_registration_id']);
        }
        return $tokens;
    }
 
    //Function to add message to the database
    public function addMessage($code_user, $code_family, $message){
        $stmt = $this->conn->prepare("INSERT INTO messages (message,code_user, code_family) VALUES (?,?,?)");
        $stmt->bind_param("sss", $message, $code_user, $code_family);
        if($stmt->execute())
            return true;
        return false;
    }
 
    //Function to get messages from the database 
    public function getMessages($code_family){
        $stmt = $this->conn->prepare("SELECT a.message_id AS message_id, a.message AS message, a.created_on AS created_on, a.code_user AS code_user, a.code_family AS code_family, b.name AS name FROM messages a, (SELECT code_parent AS code_user, name FROM parent UNION SELECT code_child AS code_user, name FROM child) b WHERE a.code_user = b.code_user AND code_family = ? ORDER BY a.message_id ASC;");
         $stmt->bind_param("s", $code_family);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result;
    }

    public function getName($code_user){
        $stmt = $this->conn->prepare("SELECT a.name AS name FROM (SELECT code_parent AS code_user, name FROM parent UNION SELECT code_child AS code_user, name FROM child) a WHERE code_user = ?;");
        $stmt->bind_param("s",$code_user);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result;
    }
 }