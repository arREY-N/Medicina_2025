<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");

$servername = "localhost"; $username = "root"; $password = ""; $dbname = "medicina_db";
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(array("error" => "Connection failed: " . $conn->connect_error));
    exit();
}

// Columns based on your new Order data class
$sql = "SELECT id, medicineId, supplierId, quantity, price, expirationDate, orderDate, remainingQuantity FROM orders ORDER BY orderDate DESC, id DESC";
$result = $conn->query($sql);
$orders = array();

if ($result) {
    while($row = $result->fetch_assoc()) {
        $row['id'] = (int)$row['id'];
        $row['medicineId'] = (int)$row['medicineId'];
        $row['supplierId'] = (int)$row['supplierId'];
        $row['quantity'] = (int)$row['quantity'];
        $row['price'] = (float)$row['price'];
        // expirationDate and orderDate are likely strings 'YYYY-MM-DD' from DB (if DATE type)
        // or need formatting if DATETIME
        $row['remainingQuantity'] = (int)$row['remainingQuantity'];
        $orders[] = $row;
    }
    http_response_code(200);
    echo json_encode($orders);
} else {
    http_response_code(500);
    echo json_encode(array("error" => "Query failed: " . $conn->error));
}
$conn->close();
?>