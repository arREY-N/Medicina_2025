<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");

$servername = "localhost"; $username = "root"; $password = ""; $dbname = "medisina_draft";
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(array("error" => "Connection failed: " . $conn->connect_error));
    exit();
}

// Adjust selected columns based on your Account model and what you want to expose
$sql = "SELECT id, username, email, designationId, firstName, lastName, isActive FROM accounts";
$result = $conn->query($sql);
$accounts = array();

if ($result) {
    while($row = $result->fetch_assoc()) {
        $row['id'] = (int)$row['id'];
        $row['designationId'] = (int)$row['designationId'];
        $row['isActive'] = (bool)$row['isActive']; // Ensure boolean type
        // Do NOT send password back to client
        $accounts[] = $row;
    }
    http_response_code(200);
    echo json_encode($accounts);
} else {
    http_response_code(500);
    echo json_encode(array("error" => "Query failed: " . $conn->error));
}
$conn->close();
?>